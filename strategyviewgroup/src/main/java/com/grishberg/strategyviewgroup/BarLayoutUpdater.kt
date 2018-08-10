package com.grishberg.strategyviewgroup

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater

/**
 * Updates BarViewGroup layout.
 */
class BarLayoutUpdater(private val context: Context, @LayoutRes val layout: Int) {
    private val params: UpdateParams by lazy { prepareParamsMap() }

    private fun prepareParamsMap(): UpdateParams {
        var internalLayoutType = 0
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
        return UpdateParams(internalLayoutType, paramsMap)
    }

    fun applyTo(barViewGroup: BarViewGroup) {
        barViewGroup.changeLayoutType(params.internalLayoutType, params.paramsMap)
    }

    fun applywithStrategy(barViewGroup: BarViewGroup, strategy: LayoutStrategy) {
        barViewGroup.changeLayoutType(strategy, params.paramsMap)
    }

    private data class UpdateParams(val internalLayoutType: Int,
                                    val paramsMap: HashMap<Int, BarViewGroup.LayoutParams>)
}