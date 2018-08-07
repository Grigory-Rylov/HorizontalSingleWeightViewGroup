package com.grishberg.barviewgroup

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater

class BarLayoutUpdater {
    private var internalLayoutType: Int = -1
    private val paramsMap = HashMap<Int, BarViewGroup.LayoutParams>()

    fun cloneFromLayout(context: Context, @LayoutRes layout: Int) {
        paramsMap.clear()

        val view = LayoutInflater.from(context).inflate(layout, null, false)
        if (view is BarViewGroup) {
            internalLayoutType = view.layoutType
            val count = view.childCount
            for (i in 0 until count) {
                val child = view.getChildAt(i)
                paramsMap[child.id] = child.layoutParams as BarViewGroup.LayoutParams
            }
        }
    }

    fun applyTo(barViewGroup: BarViewGroup) {
        barViewGroup.changeLayoutType(internalLayoutType, paramsMap)
    }

    fun applywithStrategy(barViewGroup: BarViewGroup, strategy: LayoutStrategy) {
        barViewGroup.changeLayoutType(strategy, paramsMap)
    }
}