package com.grishberg.strategyviewgroup

import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.View.*
import android.view.ViewGroup


class OneRowStrategy : LayoutStrategy {

    private var viewWithWeightWidth: Int = 0
    private val tmpChildRect = Rect()

    override fun onMeasure(parent: BarViewGroup,
                           widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val count = parent.childCount
        var viewWithWeight: View? = null
        var maxWidth: Int = 0
        var maxHeight: Int = 0
        var totalChildWidth: Int = 0
        var totalChildMargins: Int = 0
        var childState: Int = 0


        for (i in 0 until count) {
            val child = parent.getChildAt(i)

            if (child.visibility == View.GONE) {
                continue
            }

            parent.measureChildWithMarginsEx(child, widthMeasureSpec, 0, heightMeasureSpec, 0)

            val lp = child.layoutParams as (BarViewGroup.LayoutParams)
            maxHeight = Math.max(maxHeight, child.measuredHeight + lp.topMargin + lp.bottomMargin)

            if (lp.hasWeight) {
                viewWithWeight = child
                continue
            }

            totalChildWidth += child.measuredWidth
            totalChildMargins += lp.leftMargin + lp.rightMargin

            childState = View.combineMeasuredStates(childState, child.measuredState)
        }
        //Measure Width
        when (widthMode) {
            MeasureSpec.EXACTLY -> //Must be this size
                maxWidth = widthSize
            MeasureSpec.AT_MOST -> //Can't be bigger than...
                maxWidth = Math.min(parent.layoutParams.width, widthSize)
            MeasureSpec.UNSPECIFIED -> {
                //Be whatever you want
                //maxWidth = desiredWidth
            }
        }

        //Measure Height
        when (heightMode) {
            MeasureSpec.EXACTLY -> //Must be this size
                maxHeight = heightSize
            MeasureSpec.AT_MOST -> //Can't be bigger than...
                maxHeight = Math.min(maxHeight, heightSize)
            MeasureSpec.UNSPECIFIED -> {
                //Be whatever you want
                //height = desiredHeight;
            }
        }
        maxHeight = Math.max(maxHeight, parent.getSuggestedMinimumHeightEx())
        maxWidth = Math.max(maxWidth, parent.getSuggestedMinimumWidthEx())
        if (viewWithWeight != null) {
            viewWithWeightWidth = maxWidth - (totalChildWidth + totalChildMargins)

            val childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, viewWithWeightWidth)
            val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0, maxHeight)
            viewWithWeight.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }

        // Report our final dimensions.
        parent.setMeasuredDimensionEx(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState))
    }

    override fun onLayout(parent: BarViewGroup,
                          changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val count = parent.childCount
        var leftPos = parent.paddingLeft
        val parentTop = parent.paddingTop
        val parentBottom = bottom - top - parent.paddingBottom
        var prevChildGone = false

        for (i in 0 until count) {
            val child = parent.getChildAt(i)

            if (child.visibility == View.GONE) {
                prevChildGone = true
                continue
            }

            val lp = child.layoutParams as (BarViewGroup.LayoutParams)
            calculateChildRect(child, parentTop, parentBottom, lp, leftPos, prevChildGone)
            prevChildGone = false
            leftPos = tmpChildRect.right + lp.rightMargin

            //tmpContainerRect.left = middleLeft + lp.leftMargin
            //tmpContainerRect.right = middleRight - lp.rightMargin

            //Gravity.apply(lp.gravity, width, height, mTmpContainerRect, mTmpChildRect);

            if (lp.hasWeight) {
                tmpChildRect.right = tmpChildRect.left + viewWithWeightWidth
                leftPos = tmpChildRect.right
                child.layout(tmpChildRect.left,
                        tmpChildRect.top,
                        tmpChildRect.right,
                        tmpChildRect.bottom)
                continue
            }

            // Place the child.
            child.layout(tmpChildRect.left,
                    tmpChildRect.top,
                    tmpChildRect.right,
                    tmpChildRect.bottom)
        }
    }

    private fun calculateChildRect(child: View, parentTop: Int, parentBottom: Int,
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