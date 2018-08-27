package com.grishberg.barviewgroup

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.MotionEvent
import android.view.View

private const val TAG = "DraggableItemsList"

class DraggableItemsList @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null) :
        RecyclerView(context, attrs) {
    private val adapter: ItemsAdapter
    private val idleState = Idle()
    private val onTouchDown = OnTouchDown()
    private val startMoving = StartedMoving()
    private var state: State = idleState

    var dragListener: DragListener = DragListener.Stub()

    init {
        val layoutManager = LinearLayoutManager(context)
        setLayoutManager(layoutManager)
        adapter = ItemsAdapter()
        setAdapter(adapter)
        val callback = SimpleItemTouchHelperCallback()
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(this)

        setOnTouchListener(OnTouchListener())
    }

    fun setItems(items: ArrayList<ItemData>) {
        adapter.items = items
        adapter.notifyDataSetChanged()
    }

    inner class SimpleItemTouchHelperCallback : ItemTouchHelper.Callback() {

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: ViewHolder?, direction: Int) {
            /* not used */
        }

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            Log.d(TAG, "onMove")
            state.onMove(viewHolder.adapterPosition, target.adapterPosition)
            adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }
    }

    inner class OnTouchListener : View.OnTouchListener {
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_UP) {
                Log.d(TAG, "onTouch up")
                state.onTouchUp()
            } else if (event.action == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "onTouch down")
                state.onTouchDown()
            }
            return false
        }
    }

    private open class State {
        open fun onTouchDown() { /* stub */
        }

        open fun onTouchUp() { /* stub */
        }

        open fun onMove(oldPos: Int, newPos: Int) { /* stub */
        }

        open fun setStartPos(startPos: Int) { /* stub */
        }
    }

    private inner class Idle : State() {
        override fun onTouchDown() {
            state = onTouchDown
        }
    }

    private inner class OnTouchDown : State() {
        override fun onMove(oldPos: Int, newPos: Int) {
            state = startMoving
            state.setStartPos(oldPos)
            state.onMove(oldPos, newPos)
        }
    }

    private inner class StartedMoving : State() {
        private var startPos: Int = 0
        private var endPos: Int = 0

        override fun setStartPos(pos: Int) {
            startPos = pos
        }

        override fun onMove(oldPos: Int, newPos: Int) {
            endPos = newPos
        }

        override fun onTouchUp() {
            dragListener.onItemMoved(startPos, endPos)
            state = idleState
        }
    }
}