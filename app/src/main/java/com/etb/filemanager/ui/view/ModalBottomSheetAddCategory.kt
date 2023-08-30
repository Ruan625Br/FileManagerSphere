package com.etb.filemanager.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.etb.filemanager.R
import com.etb.filemanager.interfaces.manager.ItemListener
import com.etb.filemanager.settings.preference.Preferences
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File

class ModalBottomSheetAddCategory : BottomSheetDialogFragment() {

    var itemListener: ItemListener? = null
    private lateinit var eCategoryName: TextInputLayout
    private lateinit var eCategoryPath: TextInputLayout
    private lateinit var dCategoryName: TextInputEditText
    private lateinit var dCategoryPath: TextInputEditText
    private lateinit var btnAddCategory: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_add_category, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eCategoryName = view.findViewById(R.id.eInputLayoutName)
        eCategoryPath = view.findViewById(R.id.eInputLayoutPath)
        dCategoryName = view.findViewById(R.id.categoryName)
        dCategoryPath = view.findViewById(R.id.categoryPath)
        btnAddCategory = view.findViewById(R.id.addCategory)

        btnAddCategory.setOnClickListener {
            val categoryName = dCategoryName.text?.trim().toString()
            val categoryPath = dCategoryPath.text?.trim().toString()

            val file = File(categoryPath)

            var isValid = true

            if (categoryName.isEmpty()) {
                eCategoryName.error = getString(R.string.error_empty)
                isValid = false
            } else {
                eCategoryName.error = null
            }

            if (categoryPath.isEmpty()) {
                eCategoryPath.error = getString(R.string.error_empty)
                isValid = false
            } else {
                eCategoryPath.error = null
            }

            if (!file.exists() || file.isFile) {
                eCategoryPath.error = getString(R.string.invalid_path)
                isValid = false
            } else {
                eCategoryPath.error = null

            }


            if (isValid) {
                dismiss()
                addCategory(categoryName, categoryPath)
            }
        }
    }

    private fun addCategory(name: String, path: String) {
        val listNameCategory = Preferences.Behavior.categoryNameList.toMutableList()
        val listPathCategory = Preferences.Behavior.categoryPathList.toMutableList()

        listNameCategory.add(name)
        listPathCategory.add(path)

        Preferences.Behavior.categoryNameList = listNameCategory.toList()
        Preferences.Behavior.categoryPathList = listPathCategory.toList()
        itemListener?.refreshItem()
    }

    companion object {
        const val TAG = "ModalBottomSheetAddCategory"
    }
}