package com.etb.filemanager.manager.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.etb.filemanager.manager.adapter.FileModelAdapter

class FileItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {

    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        return view?.let {
            val viewHolder = recyclerView.getChildViewHolder(view)
            if (viewHolder is FileModelAdapter.ViewHolder) {
                // return viewHolder.itemDetails
            }
            return null
        }
    }
}
