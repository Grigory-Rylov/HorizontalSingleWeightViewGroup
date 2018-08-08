package com.grishberg.strategyviewgroup

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater

/**
 * Updates BarViewGroup layout.
 */
class BarLayoutUpdater(val context: Context, @LayoutRes val layout: Int) {
    private var internalLayoutType: Int = -1
    private val paramsMap: HashMap<Int, BarViewGroup.LayoutParams> by lazy { prepareParamsMap() }

    private fun prepareParamsMap(): HashMap<Int, BarViewGroup.LayoutParams> {
        val paramsMap = HashMap<Int, BarViewGroup.LayoutParams>()
        val view = LayoutInflater.from(context).inflate(layout, null, false)
        if (view is BarViewGroup) {
            internalLayoutType = view.layoutType
            val count = view.childCount
            for (i in 0 until count) {
                val child = view.getChildAt(i)
                paramsMap[child.id] = child.layoutParams as BarViewGroup.LayoutParams
            }
        }
        return paramsMap
    }

    fun applyTo(barViewGroup: BarViewGroup) {
        barViewGroup.changeLayoutType(internalLayoutType, paramsMap)
    }

    fun applywithStrategy(barViewGroup: BarViewGroup, strategy: LayoutStrategy) {
        barViewGroup.changeLayoutType(strategy, paramsMap)
    }
}