package com.grishberg.barviewgroup

import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import com.grishberg.strategyviewgroup.BarLayoutUpdater
import com.grishberg.strategyviewgroup.BarViewGroup
import com.transitionseverywhere.TransitionManager

class MainActivity : AppCompatActivity() {
    private var twoRow: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val oneRowLayoutUpdater = BarLayoutUpdater(this, R.layout.one_row_bar)
        val twoRowsLayoutUpdater = BarLayoutUpdater(this, R.layout.two_row_bar)

        val rootView = findViewById<LinearLayout>(R.id.root)
        val barView = findViewById<BarViewGroup>(R.id.barView)
        val button1 = findViewById<ImageButton>(R.id.button2)
        val button5 = findViewById<ImageButton>(R.id.button3)
        twoRowsLayoutUpdater.applyTo(barView)
        rootView.setOnClickListener {
            twoRow = !twoRow

            TransitionManager.beginDelayedTransition(barView)
            barView.removeView(button1)
            barView.removeView(button5)
            if (twoRow) {

                barView.addView(button1,2)
                barView.addView(button5, 1)
                twoRowsLayoutUpdater.applyTo(barView)
            } else {
                oneRowLayoutUpdater.applyTo(barView)
                barView.addView(button1, 1)
                barView.addView(button5, 2)
            }
        }
    }
}
