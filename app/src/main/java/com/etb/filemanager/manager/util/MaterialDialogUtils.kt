/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - MaterialDialogUtils.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import com.etb.filemanager.R
import com.etb.filemanager.files.util.FileUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

class MaterialDialogUtils {
    private val fileUtil = FileUtil()

    data class DialogResult(val confirmed: Boolean, val text: String)
    data class DialogInfoResult(val confirmed: Boolean)

    @SuppressLint("InflateParams", "SuspiciousIndentation")
    fun createBasicMaterial(
        title: String, text: String, textPositiveButton: String, context: Context, callback: (DialogResult) -> Unit
    ) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.layout_basic_dialog, null)
        val eInputEditText = dialogView.findViewById<TextInputEditText>(R.id.eInputEditText)

        eInputEditText.setText(text)

        MaterialAlertDialogBuilder(context).setTitle(title).setView(dialogView).setCancelable(false)
            .setPositiveButton(textPositiveButton) { _, _ ->
                val enteredText = eInputEditText.text.toString()
                callback(DialogResult(true, enteredText))


            }.setNegativeButton(R.string.dialog_cancel) { _, _ ->
                callback(DialogResult(false, ""))
            }.show()
    }

    @SuppressLint("InflateParams", "SuspiciousIndentation")
    fun createDialogInfo(
        title: String,
        message: String,
        textPositiveButton: String,
        textNegativeButon: String = "null",
        context: Context,
        cancelable: Boolean,
        callback: (DialogInfoResult) -> Unit
    ) {


        val mDialog = MaterialAlertDialogBuilder(context).setTitle(title).setMessage(message)
            .setPositiveButton(textPositiveButton) { _, _ ->
                callback(DialogInfoResult(true))


            }
        if (cancelable) {
            mDialog.setCancelable(true)
            mDialog.setNegativeButton(textNegativeButon) { _, _ ->
                callback(DialogInfoResult(false))
            }
        }
        mDialog.show()
    }


}
