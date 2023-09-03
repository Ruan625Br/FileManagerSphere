/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ListAdapter.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.files.ui

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class ListAdapter<T, VH : RecyclerView.ViewHolder>(
    callback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<VH>() {
    private val listDiffer = ListDiffer(AdapterListUpdateCallback(this), callback)

    val list: List<T>
        get() = listDiffer.list

    override fun getItemCount(): Int = list.size

    fun getItem(position: Int): T = list[position]

    final override fun getItemId(position: Int): Long = RecyclerView.NO_ID

    open fun refresh() {
        val list = listDiffer.list
        listDiffer.list = emptyList()
        listDiffer.list = list
    }

    open fun replace(list: List<T>, clear: Boolean) {
        if (clear) {
            listDiffer.list = emptyList()
        }
        listDiffer.list = list
    }

    open fun clear() {
        listDiffer.list = emptyList()
    }
}