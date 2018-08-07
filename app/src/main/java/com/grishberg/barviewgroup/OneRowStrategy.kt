package com.grishberg.barviewgroup

import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.View.*


class OneRowStrategy : LayoutStrategy {

    private var viewWithWeightWidth: Int = 0
    private val tmpContainerRect = Rect()
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
            if (lp.hasWeight) {
                viewWithWeight = child
                continue
            }

            totalChildWidth += child.measuredWidth
            totalChildMargins += lp.leftMargin + lp.rightMargin

            maxHeight = Math.max(maxHeight, child.measuredHeight + lp.topMargin + lp.bottomMargin)
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
        }

        // Report our final dimensions.
        parent.setMeasuredDimensionEx(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec,
                        childState shl MEASURED_HEIGHT_STATE_SHIFT))
    }

    override fun onLayout(parent: BarViewGroup,
                          changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = parent.childCount
        var leftPos = parent.paddingLeft
        val rightPos = r - l - parent.paddingRight
        val parentTop = parent.paddingTop
        val parentBottom = b - t - parent.paddingBottom

        for (i in 0 until count) {
            val child = parent.getChildAt(i)

            if (child.visibility == View.GONE) {
                continue
            }

            val lp = child.layoutParams as (BarViewGroup.LayoutParams)
            val width = child.measuredWidth
            val height = child.measuredHeight

            tmpChildRect.top = parentTop + lp.topMargin
            tmpChildRect.bottom = parentBottom - lp.bottomMargin
            tmpChildRect.left = leftPos + lp.leftMargin
            tmpChildRect.right = tmpChildRect.left + width
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
}