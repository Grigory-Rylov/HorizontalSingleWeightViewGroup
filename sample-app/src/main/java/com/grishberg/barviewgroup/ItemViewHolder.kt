package com.grishberg.barviewgroup

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val title: TextView = view.findViewById(R.id.itemTitle)
    fun bind(itemData: ItemData) {
        title.text = itemData.title
    }
}