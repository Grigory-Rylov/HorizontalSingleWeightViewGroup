package com.grishberg.barviewgroup

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    private var twoRow: Boolean = false
    private val oneRowLayoutUpdater = BarLayoutUpdater()
    private val twoRowsLayoutUpdater = BarLayoutUpdater()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        oneRowLayoutUpdater.cloneFromLayout(this, R.layout.one_row_bar)
        twoRowsLayoutUpdater.cloneFromLayout(this, R.layout.two_row_bar)

        val rootView = findViewById<View>(R.id.root)
        val barView = findViewById<BarViewGroup>(R.id.barView)

        rootView.setOnClickListener {
            twoRow = !twoRow
            if (twoRow) {
                twoRowsLayoutUpdater.applyTo(barView)
            } else {
                oneRowLayoutUpdater.applyTo(barView)
            }
        }
    }
}
