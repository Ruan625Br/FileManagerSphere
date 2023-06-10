package com.etb.filemanager.manager.category.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.etb.filemanager.R
import com.etb.filemanager.interfaces.settings.PopupSettingsListener
import com.etb.filemanager.interfaces.settings.util.SelectPreferenceUtils
import com.etb.filemanager.manager.util.FileUtils

class CategoryFileModelAdapter(private var categoryFileModel: List<CategoryFileModel>, private val mContext: Context) :
    RecyclerView.Adapter<CategoryFileModelAdapter.ViewHolder>() {

    private val fileUtils: FileUtils = FileUtils.getInstance()
    private lateinit var selectPreferenceUtils: SelectPreferenceUtils
    private lateinit var popupSettings: PopupSettingsListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryFileModelAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_file_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryFileModelAdapter.ViewHolder, position: Int) {
        val categoryViewFileModel = categoryFileModel[position]
        selectPreferenceUtils = SelectPreferenceUtils.getInstance()
        holder.itemIcon.setImageResource(categoryViewFileModel.icon)
        holder.itemTitle.text = categoryViewFileModel.title








    }

    override fun getItemCount(): Int {
        return categoryFileModel.size
    }

    class ViewHolder(itemFileView: View) : RecyclerView.ViewHolder(itemFileView) {
             val itemIcon = itemFileView.findViewById<ImageView>(R.id.imageView)
             val itemTitle = itemFileView.findViewById<TextView>(R.id.item_title)
             val itemCategory = itemFileView.findViewById<LinearLayout>(R.id.itemCategory)

    }

}