/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - Details.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.selection

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import com.etb.filemanager.manager.adapter.FileModel

class Details(
    var fileModel: FileModel? = null,
    var adapterPosition: Int = -1
): ItemDetailsLookup.ItemDetails<Long>() {
    override fun getPosition(): Int {
        return adapterPosition
    }

    override fun getSelectionKey(): Long {
        return fileModel!!.id
    }


    override fun inSelectionHotspot(e: MotionEvent): Boolean {
        return super.inSelectionHotspot(e)
    }


}