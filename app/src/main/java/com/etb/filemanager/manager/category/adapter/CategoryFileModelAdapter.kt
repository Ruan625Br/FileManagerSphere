/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - CategoryFileModelAdapter.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

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
import com.etb.filemanager.interfaces.manager.ItemListener
import com.etb.filemanager.interfaces.settings.PopupSettingsListener
import com.etb.filemanager.interfaces.settings.util.SelectPreferenceUtils
import com.etb.filemanager.manager.util.FileUtils
import java.nio.file.Paths

class CategoryFileModelAdapter(private var listener: ItemListener, private var categoryFileModel: List<CategoryFileModel>, private val mContext: Context) :
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
        val path = Paths.get(categoryViewFileModel.path)

        selectPreferenceUtils = SelectPreferenceUtils.getInstance()
        holder.itemIcon.setImageResource(categoryViewFileModel.icon)
        holder.itemTitle.text = categoryViewFileModel.title

        holder.itemCategory.setOnClickListener{
            listener.openFileCategory(path, categoryViewFileModel)
        }
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