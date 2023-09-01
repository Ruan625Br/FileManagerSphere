package com.etb.filemanager.manager.category.adapter

import android.content.Context
import android.os.Environment
import com.etb.filemanager.R
import com.etb.filemanager.settings.preference.Preferences


class CategoryFileModel(var icon: Int, var title: String, var path: String, val category: Category = Category.GENERIC)


enum class Category(){
    IMAGE,
    MOVIES,
    MUSIC,
    GENERIC,
    APPS,

}
fun getCategories(context: Context): ArrayList<CategoryFileModel> {
    val listCategoryName = Preferences.Behavior.categoryNameList
    val listCategoryPath = Preferences.Behavior.categoryPathList

    val dcimPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
    val moviesPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
    val documentsPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
    val musicPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
    val downloadsPath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath


    val categoryFileModels = ArrayList<CategoryFileModel>()
    categoryFileModels.add(
        CategoryFileModel(
            R.drawable.ic_image, context.getString(R.string.images), dcimPath
        )
    )
    categoryFileModels.add(
        CategoryFileModel(
            R.drawable.ic_video, context.getString(R.string.video), moviesPath
        )
    )
    categoryFileModels.add(
        CategoryFileModel(
            R.drawable.ic_document, context.getString(R.string.document), documentsPath
        )
    )
    categoryFileModels.add(
        CategoryFileModel(
            R.drawable.ic_music, context.getString(R.string.music), musicPath
        )
    )

    categoryFileModels.add(
        CategoryFileModel(
            R.drawable.ic_download, context.getString(R.string.download), downloadsPath
        )
    )
    categoryFileModels.add(
        CategoryFileModel(
            R.drawable.file_apk_icon, context.getString(R.string.apps), "", Category.APPS)
    )
    if (listCategoryName.isNotEmpty()) {
        for ((index, name) in listCategoryName.withIndex()) {
            val mName = name
            val mPath = listCategoryPath[index]
            categoryFileModels.add(CategoryFileModel(R.drawable.ic_folder, mName, mPath))
        }
    }

    return categoryFileModels
}
