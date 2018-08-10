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
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        var maxWidth = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        var maxHeight: Int = 0
        var childState: Int = 0
        val count = parent.childCount
        var topView: View? = null
        var buttonsCount: Int = 0
        var totalMargins: Int = 0


        for (i in 0 until count) {
            val child = parent.getChildAt(i)

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
            topViewHeight = topView.measuredHeight
            val childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, 0, maxWidth)
            val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec, 0, topViewHeight)
            topView.measure(childWidthMeasureSpec, childHeightMeasureSpec)
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

        for (i in 0 until count) {
            val child = parent.getChildAt(i)

            if (child.visibility == View.GONE) {
                continue
            }

            val lp = child.layoutParams as (BarViewGroup.LayoutParams)

            if (lp.hasWeight) {
                child.layout(left, top, right, top + topViewHeight)
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