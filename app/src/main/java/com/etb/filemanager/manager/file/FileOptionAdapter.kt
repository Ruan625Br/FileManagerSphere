package com.etb.filemanager.manager.file

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.etb.filemanager.R
import com.etb.filemanager.interfaces.manager.FileListener
import com.etb.filemanager.manager.adapter.FileModel


class FileOptionAdapter(
    var listener: FileListener,
    var fileItem: FileModel,
    var fileAction: MutableList<FileAction>
) :
    RecyclerView.Adapter<FileOptionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_action, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actionItem = fileAction[position]

        holder.actionTitle.text = actionItem.title
        holder.actionIcon.setImageResource(actionItem.icon)
        holder.actionBase.setOnClickListener {
            listener.onClickFileAction(
                fileItem,
                actionItem.action
            )
        }

    }

    override fun getItemCount(): Int {
        return fileAction.size
    }

    class ViewHolder(itemBarView: View) : RecyclerView.ViewHolder(itemBarView) {
        val actionTitle = itemBarView.findViewById<TextView>(R.id.tv_item_title)
        val actionBase = itemBarView.findViewById<LinearLayout>(R.id.lv_item)
        val actionIcon = itemBarView.findViewById<ImageView>(R.id.imageView4)

    }
}