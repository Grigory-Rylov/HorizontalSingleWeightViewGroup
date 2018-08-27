package com.grishberg.strategyviewgroup

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup

class TwoRowStrategy : LayoutStrategy {
    private val tmpChildRect = Rect()
    private var topViewHeight: Int = 0
    private var bottomViewHeight: Int = 0
    private var bottomItemWidth: Int = 0

    override fun onMeasure(parent: BarViewGroup, widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        var maxHeight = 0
        var childState = 0
        val count = parent.childCount
        var topView: View? = null
        var buttonsCount = 0
        var totalMargins = 0

        for (childIndex in 0 until count) {
            val child = parent.getChildAt(childIndex)
            if (child.visibility == View.GONE) {
                continue
            }

            val lp = child.layoutParams as BarViewGroup.LayoutParams

            if (lp.hasWeight) {
                topView = child
                continue
            }

            parent.measureChildWithMarginsEx(child, widthMeasureSpec, 0, heightMeasureSpec, 0)

            buttonsCount++
            bottomViewHeight = Math.max(bottomViewHeight,
                    child.measuredHeight + lp.topMargin + lp.bottomMargin)
            totalMargins += lp.leftMargin + lp.rightMargin
        }

        if (topView != null) {
            val childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, maxWidth)
            val childHeightMeasureSpec = if (heightMode == View.MeasureSpec.UNSPECIFIED) {
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            } else {
                ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0, topView.layoutParams.height)
            }
            topView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
            topViewHeight = topView.measuredHeight
        }

        maxHeight = Math.max(topViewHeight + bottomViewHeight, parent.getSuggestedMinimumHeightEx())
        maxWidth = Math.max(maxWidth, parent.getSuggestedMinimumWidthEx())

        if (buttonsCount > 0) {
            bottomItemWidth = (maxWidth - parent.paddingLeft - parent.paddingRight - totalMargins) / buttonsCount
        }

        // Report our final dimensions.
        parent.setMeasuredDimensionEx(
                View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                View.resolveSizeAndState(maxHeight, heightMeasureSpec, childState))
    }

    override fun onLayout(parent: BarViewGroup, changed: Boolean,
                          left: Int, top: Int, right: Int, bottom: Int) {
        val count = parent.childCount
        var leftPos = parent.paddingLeft
        val parentTop = parent.paddingTop

        for (childIndex in 0 until count) {
            val child = parent.getChildAt(childIndex)

            if (child.visibility == View.GONE) {
                continue
            }

            val lp = child.layoutParams as (BarViewGroup.LayoutParams)

            if (lp.hasWeight) {
                child.layout(left, parentTop, right, parentTop + topViewHeight)
                continue
            }

            val height = child.measuredHeight

            tmpChildRect.top = parentTop + topViewHeight + lp.topMargin
            tmpChildRect.bottom = tmpChildRect.top + height

            tmpChildRect.left = leftPos + lp.leftMargin
            tmpChildRect.right = tmpChildRect.left + bottomItemWidth

            leftPos = tmpChildRect.right + lp.rightMargin

            // Place the child.
            child.layout(tmpChildRect.left,
                    tmpChildRect.top,
                    tmpChildRect.right,
                    tmpChildRect.bottom)
        }
    }
}