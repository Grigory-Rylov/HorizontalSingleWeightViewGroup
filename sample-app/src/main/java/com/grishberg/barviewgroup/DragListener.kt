package com.grishberg.barviewgroup

interface DragListener {
    fun onItemMoved(oldPos: Int, newPost: Int)

    class Stub : DragListener {
        override fun onItemMoved(oldPos: Int, newPost: Int) {
            /* stub */
        }
    }
}