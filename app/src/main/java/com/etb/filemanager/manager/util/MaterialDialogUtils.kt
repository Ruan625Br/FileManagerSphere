package com.etb.filemanager.manager.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.RadioGroup
import com.etb.filemanager.R
import com.etb.filemanager.files.util.ContextUtils
import com.etb.filemanager.util.file.FileUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

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
        val eInputLayout = dialogView.findViewById<TextInputLayout>(R.id.eInputLayout)
        val eInputEditText = dialogView.findViewById<TextInputEditText>(R.id.eInputEditText)

        eInputEditText.setText(text)

        MaterialAlertDialogBuilder(context).setTitle(title).setView(dialogView).setCancelable(false)
            .setPositiveButton(textPositiveButton) { dialog, which ->
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
            .setPositiveButton(textPositiveButton) { dialog, which ->
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

    @SuppressLint("InflateParams", "SuspiciousIndentation", "MissingInflatedId")
    fun createDialogArchive(
        callback: (DialogInfoResult) -> Unit
    ) {
        val context = ContextUtils.getContext()

        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.content_dialog_archive, null)
        val title = context.getString(R.string.fab_menu_action_create_file)
        val textPositiveButton = context.getString(R.string.ok)
        val typeGroup = dialogView.findViewById<RadioGroup>(R.id.typeGroup)


        val mDialog =
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setView(dialogView)
                .setPositiveButton(textPositiveButton) { dialog, which ->
                    val extension = when (val typeId = typeGroup.checkedRadioButtonId) {
                        R.id.zipRadio -> "zip"
                        R.id.tarXzRadio -> "tar.xz"
                        R.id.sevenZRadio -> "7z"
                        else -> throw AssertionError(typeId)
                    }
                callback(DialogInfoResult(true))


            }
        mDialog.setNegativeButton(R.string.dialog_cancel) { _, _ ->
            callback(DialogInfoResult(false))
        }

        mDialog.show()
    }

}
