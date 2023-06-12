package com.etb.filemanager.manager.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import com.etb.filemanager.R
import com.etb.filemanager.util.file.FileUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MaterialDialogUtils {
    private val fileUtil = FileUtil()

    data class DialogResult(val confirmed: Boolean, val text: String)

    @SuppressLint("InflateParams")
    fun createBasicMaterial(
        title: String,
        text: String,
        textPositiveButton: String,
        context: Context,
        callback: (DialogResult) -> Unit
    ) {
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.layout_basic_dialog, null)
        val eInputLayout = dialogView.findViewById<TextInputLayout>(R.id.eInputLayout)
        val eInputEditText = dialogView.findViewById<TextInputEditText>(R.id.eInputEditText)

        eInputEditText.setText(text)

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton(textPositiveButton) { dialog, which ->
                val enteredText = eInputEditText.text.toString()
                   callback(DialogResult(true, enteredText))


            }
            .setNegativeButton(R.string.dialog_cancel) { _, _ ->
                callback(DialogResult(false, ""))
            }
            .show()
    }
}
