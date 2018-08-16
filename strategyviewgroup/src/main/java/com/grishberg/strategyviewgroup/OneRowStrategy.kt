package com.grishberg.strategyviewgroup

import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.ViewGroup.getChildMeasureSpec


class OneRowStrategy : LayoutStrategy {

    private var stretchableViewWidth: Int = 0
    private val tmpChildRect = Rect()
    private var maxMeasuredWidth = 0
    private var maxMeasuredHeight = 0

    override fun onMeasure(parent: BarViewGroup,
                           widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val count = parent.childCount
        var stretchableView: View? = null
        var totalChildWidth = 0
        var totalChildMargins = 0
        var childState = 0

        for (childIndex in 0 until count) {

            val child = parent.getChildAt(childIndex)

            if (child.visibility == View.GONE) {
                continue
            }

            val lp = child.layoutParams as (BarViewGroup.LayoutParams)

            if (lp.hasWeight) {
                stretchableView = child
                continue
            }
            parent.measureChildWithMarginsEx(child, widthMeasureSpec, 0, heightMeasureSpec, 0)
            val measuredChildHeightWithMargins = child.measuredHeight + lp.topMargin + lp.bottomMargin
            maxMeasuredHeight = Math.max(maxMeasuredHeight, measuredChildHeightWithMargins)

            totalChildWidth += child.measuredWidth
            totalChildMargins += lp.leftMargin + lp.rightMargin

            childState = View.combineMeasuredStates(childState, child.measuredState)
        }

        updateMaxMeasuredWidthFromMeasureSpec(widthMode, widthSize, parent)

        updateMaxMeasureHeightFromMeasureSpec(heightMode, heightSize)

        if (stretchableView != null) {
            measureStretchableView(stretchableView, totalChildWidth, totalChildMargins,
                    widthMeasureSpec, heightMeasureSpec, heightMode)
            maxMeasuredHeight = Math.max(maxMeasuredHeight, stretchableView.measuredHeight)
        }

        maxMeasuredHeight = Math.max(maxMeasuredHeight, parent.getSuggestedMinimumHeightEx())
        maxMeasuredWidth = Math.max(maxMeasuredWidth, parent.getSuggestedMinimumWidthEx())

        // Report our final dimensions.
        parent.setMeasuredDimensionEx(resolveSizeAndState(maxMeasuredWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxMeasuredHeight, heightMeasureSpec, childState))
    }

    private fun measureStretchableView(stretchableView: View,
                                       totalChildWidth: Int,
                                       totalChildMargins: Int,
                                       widthMeasureSpec: Int,
                                       heightMeasureSpec: Int,
                                       heightMode: Int) {
        val lp = stretchableView.layoutParams
        stretchableViewWidth = maxMeasuredWidth - (totalChildWidth + totalChildMargins)

        val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, stretchableViewWidth)
        val childHeightMeasureSpec = if (heightMode == MeasureSpec.UNSPECIFIED) {
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        } else if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            getChildMeasureSpec(heightMeasureSpec, 0, maxMeasuredHeight)
        } else {
            getChildMeasureSpec(heightMeasureSpec, 0, stretchableView.layoutParams.height)
        }

        stretchableView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
    }

    private fun updateMaxMeasureHeightFromMeasureSpec(heightMode: Int, heightSize: Int) {
        when (heightMode) {
            MeasureSpec.EXACTLY -> //Must be this size
                maxMeasuredHeight = heightSize
            MeasureSpec.AT_MOST -> //Can't be bigger than...
                maxMeasuredHeight = Math.min(maxMeasuredHeight, heightSize)
            MeasureSpec.UNSPECIFIED -> {
                //Be whatever you want
                //height = desiredHeight;
            }
        }
    }

    private fun updateMaxMeasuredWidthFromMeasureSpec(widthMode: Int, widthSize: Int, parent: BarViewGroup) {
        when (widthMode) {
            MeasureSpec.EXACTLY -> //Must be this size
                maxMeasuredWidth = widthSize
            MeasureSpec.AT_MOST -> //Can't be bigger than...
                maxMeasuredWidth = Math.min(parent.layoutParams.width, widthSize)
            MeasureSpec.UNSPECIFIED -> {
                //Be whatever you want
                //maxWidth = desiredWidth
            }
        }
    }

    override fun onLayout(parent: BarViewGroup,
                          changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val count = parent.childCount
        var leftPos = parent.paddingLeft
        val parentTop = parent.paddingTop
        val parentBottom = bottom - top - parent.paddingBottom
        var prevChildGone = false

        for (childIndex in 0 until count) {
            val child = parent.getChildAt(childIndex)

            if (child.visibility == View.GONE) {
                prevChildGone = true
                continue
            }

            val lp = child.layoutParams as (BarViewGroup.LayoutParams)
            calculateChildRectForLayout(child, parentTop, parentBottom, lp, leftPos, prevChildGone)
            prevChildGone = false

            if (lp.hasWeight) {
                tmpChildRect.right = tmpChildRect.left + stretchableViewWidth
            }

            leftPos = tmpChildRect.right + lp.rightMargin

            // Place the child.
            child.layout(tmpChildRect.left,
                    tmpChildRect.top,
                    tmpChildRect.right,
                    tmpChildRect.bottom)
        }
    }

    private fun calculateChildRectForLayout(child: View, parentTop: Int, parentBottom: Int,
                                            lp: BarViewGroup.LayoutParams, leftPos: Int,
                                            prevChildGone: Boolean) {
        val width = child.measuredWidth
        val childHeight = child.measuredHeight
        val gravity = lp.gravity
        val leftMargin: Int = if (prevChildGone) {
            Math.round(lp.goneMarginStart)
        } else {
            lp.leftMargin
        }

        when (gravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.TOP -> {
                tmpChildRect.top = parentTop + lp.topMargin
            }

            Gravity.CENTER_VERTICAL -> {
                tmpChildRect.top =
                        (parentBottom - parentTop - (childHeight + lp.topMargin + lp.bottomMargin)) / 2
            }
            Gravity.BOTTOM -> {
                tmpChildRect.top = parentBottom - childHeight - lp.bottomMargin
            }
            else -> tmpChildRect.top = parentTop + lp.topMargin
        }

        tmpChildRect.bottom = tmpChildRect.top + childHeight

        tmpChildRect.left = leftPos + leftMargin
        tmpChildRect.right = tmpChildRect.left + width
    }
}