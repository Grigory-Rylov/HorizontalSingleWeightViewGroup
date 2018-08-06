package com.grishberg.barviewgroup

interface LayoutStrategy {
    fun onMeasure(parent: BarViewGroup,
                  widthMeasureSpec: Int, heightMeasureSpec: Int)

    fun onLayout(parent: BarViewGroup,
                 changed: Boolean, l: Int, t: Int, r: Int, b: Int)

    class Stub : LayoutStrategy {
        override fun onMeasure(parent: BarViewGroup, widthMeasureSpec: Int, heightMeasureSpec: Int) {
            /* stub */
        }

        override fun onLayout(parent: BarViewGroup, changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}