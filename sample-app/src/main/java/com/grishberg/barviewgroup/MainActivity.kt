package com.grishberg.barviewgroup

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.grishberg.strategyviewgroup.BarLayoutUpdater
import com.grishberg.strategyviewgroup.BarViewGroup
import com.transitionseverywhere.TransitionManager

class MainActivity : AppCompatActivity() {
    private var twoRow: Boolean = false
    private val draggablItemsView by lazy { findViewById<DraggableItemsList>(R.id.draggableItems) }
    private lateinit var buttons: Array<View>
    private val barView by lazy { findViewById<ViewGroup>(R.id.barView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val oneRowLayoutUpdater = BarLayoutUpdater(this, R.layout.one_row_bar)
        val twoRowsLayoutUpdater = BarLayoutUpdater(this, R.layout.two_row_bar)

        val rootView = findViewById<LinearLayout>(R.id.root)
        val barView = findViewById<BarViewGroup>(R.id.barView)
        twoRowsLayoutUpdater.applyTo(barView)
        rootView.setOnClickListener {
            twoRow = !twoRow

            TransitionManager.beginDelayedTransition(barView)
            if (twoRow) {
                twoRowsLayoutUpdater.applyTo(barView)
            } else {
                oneRowLayoutUpdater.applyTo(barView)
            }
        }

        buttons = arrayOf(
                findViewById<View>(R.id.button1),
                findViewById<View>(R.id.button2),
                findViewById<View>(R.id.button3),
                findViewById<View>(R.id.button4),
                findViewById<View>(R.id.button5)
        )
        val items = arrayListOf(ItemData("title 1"),
                ItemData("title 2"),
                ItemData("title 3"),
                ItemData("title 4"),
                ItemData("title 5")
        )
        draggablItemsView.setItems(items)
        draggablItemsView.dragListener = ButtonsMover(2)
    }

    inner class ButtonsMover(val buttonPos: Int) : DragListener {
        override fun onItemMoved(oldPos: Int, newPos: Int) {
            TransitionManager.beginDelayedTransition(barView)

            val view1 = buttons[oldPos]

            val startPos: Int
            val endPos: Int
            if (oldPos < newPos) {
                startPos = oldPos + 1
                endPos = newPos
            } else {
                startPos = newPos + 1
                endPos = oldPos
            }

            for (i in startPos until endPos) {
                buttons[i] = buttons[i + 1]
            }

            barView.removeView(view1)
            barView.addView(view1, convertedPos(newPos))

            buttons[newPos] = view1
        }

        private fun convertedPos(srcPos: Int) =
                if (srcPos < buttonPos) {
                    srcPos
                } else {
                    srcPos + 1
                }
    }
}
