package com.grishberg.barviewgroup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

private val LAYOUT_TYPE_ONE_ROW = 0
private val LAYOUT_TYPE_TWO_ROWS = 1

class BarViewGroup @JvmOverloads constructor(
        ctx: Context,
        attrs: AttributeSet? = null) : ViewGroup(ctx, attrs) {
    private var strategy: LayoutStrategy = LayoutStrategy.Stub()
    private var layoutType: Int = 0

    init {
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(
                    attrs,
                    R.styleable.BarViewGroup,
                    0, 0)

            try {
                layoutType = a.getInteger(R.styleable.BarViewGroup_layoutType, 0)
            } finally {
                a.recycle()
            }
        }
        if (layoutType == LAYOUT_TYPE_ONE_ROW) {
            strategy = OneRowStrategy()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        strategy.onMeasure(this, widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        strategy.onLayout(this, changed, l, t, r, b)
    }

    internal fun measureChildWithMarginsEx(child: View, parentWidthMeasureSpec: Int, widthUsed: Int, parentHeightMeasureSpec: Int, heightUsed: Int) {
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed)
    }

    internal fun getSuggestedMinimumHeightEx(): Int {
        return super.getSuggestedMinimumHeight()
    }

    internal fun getSuggestedMinimumWidthEx(): Int {
        return super.getSuggestedMinimumWidth()
    }

    internal fun setMeasuredDimensionEx(measuredWidth: Int, measuredHeight: Int) {
        super.setMeasuredDimension(measuredWidth, measuredHeight)
    }

    /**
     * Any layout manager that doesn't scroll will want this.
     */
    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    // ----------------------------------------------------------------------
    // The rest of the implementation is for custom per-child layout parameters.
    // If you do not need these (for example you are writing a layout manager
    // that does fixed positioning of its children), you can drop all of this.

    override fun generateLayoutParams(attrs: AttributeSet): ViewGroup.LayoutParams {
        return BarViewGroup.LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is BarViewGroup.LayoutParams
    }

    class LayoutParams : ViewGroup.MarginLayoutParams {
        var hasWeight: Boolean = false

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {

            // Pull the layout param values from the layout XML during
            // inflation.  This is not needed if you don't care about
            // changing the layout behavior in XML.
            val typedArray = c.obtainStyledAttributes(attrs, R.styleable.BarViewGroup)
            try {
                hasWeight = typedArray.getBoolean(R.styleable.BarViewGroup_hasWeight, false)
            } finally {
                typedArray.recycle()
            }
        }

        constructor(width: Int, height: Int) : super(width, height) {}

        constructor(source: ViewGroup.LayoutParams) : super(source) {}
    }
}