package com.grishberg.strategyviewgroup

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.JELLY_BEAN
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

private val LAYOUT_TYPE_ONE_ROW = 0
private val LAYOUT_TYPE_TWO_ROWS = 1

class BarViewGroup @JvmOverloads constructor(
        ctx: Context,
        attrs: AttributeSet? = null) : ViewGroup(ctx, attrs) {
    private var strategy: LayoutStrategy = LayoutStrategy.Stub()
    private var _layoutType: Int = 0
    internal val layoutType: Int get() = _layoutType
    private val oneRowStrategy = OneRowStrategy()
    private val twoRowStrategy = TwoRowStrategy()
    internal val isRtl = isRTL(ctx)

    init {
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.BarViewGroup, 0, 0)
            try {
                _layoutType = a.getInteger(R.styleable.BarViewGroup_layoutType, 0)
            } finally {
                a.recycle()
            }
        }
        strategy = defineStrategyByType(_layoutType)
    }

    private fun isRTL(ctx: Context): Boolean {
        if (SDK_INT > JELLY_BEAN) {
            val config = ctx.resources.configuration
            return config.layoutDirection == View.LAYOUT_DIRECTION_RTL
        }
        return false
    }

    private fun defineStrategyByType(type: Int): LayoutStrategy {
        return if (type == LAYOUT_TYPE_ONE_ROW) {
            oneRowStrategy
        } else {
            twoRowStrategy
        }
    }

    fun changeLayoutType(newStrategy: LayoutStrategy,
                         layoutParamMap: HashMap<Int, LayoutParams> = HashMap()) {
        strategy = newStrategy
        updateChildLayoutParams(layoutParamMap)
        requestLayout()
    }

    fun changeLayoutType(type: Int,
                         layoutParamMap: HashMap<Int, LayoutParams> = HashMap()) {
        if (type == _layoutType || type < 0) {
            return
        }
        _layoutType = type
        strategy = defineStrategyByType(type)
        updateChildLayoutParams(layoutParamMap)
        requestLayout()
    }

    private fun updateChildLayoutParams(layoutParamMap: HashMap<Int, LayoutParams>) {
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            val oldLp = layoutParamMap[child.id]
            if (oldLp != null) {
                child.layoutParams = oldLp
            }
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
        return LayoutParams(context, attrs)
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): ViewGroup.LayoutParams {
        return ViewGroup.LayoutParams(p)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    class LayoutParams : LinearLayout.LayoutParams {
        var hasWeight: Boolean = false
        var goneMarginStart: Float = -1.0f

        constructor(c: Context, attrs: AttributeSet) : super(c, attrs) {

            // Pull the layout param values from the layout XML during
            // inflation.  This is not needed if you don't care about
            // changing the layout behavior in XML.
            val typedArray = c.obtainStyledAttributes(attrs, R.styleable.BarViewGroup)
            try {
                hasWeight = typedArray.getBoolean(R.styleable.BarViewGroup_hasWeight, false)
                goneMarginStart = typedArray.getDimension(R.styleable.BarViewGroup_goneMarginStart, -1.0f)
            } finally {
                typedArray.recycle()
            }
        }

        constructor(width: Int, height: Int) : super(width, height) {}

        constructor(source: ViewGroup.LayoutParams) : super(source) {}
    }
}