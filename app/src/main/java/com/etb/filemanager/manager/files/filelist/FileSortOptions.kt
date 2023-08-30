package com.etb.filemanager.manager.files.filelist

import android.os.Parcelable
import com.etb.filemanager.manager.adapter.FileModel
import kotlinx.parcelize.Parcelize


@Parcelize
data class FileSortOptions(
    val sortBy: SortBy,
    val order: Order,
    val isDirectoriesFirst: Boolean
) : Parcelable {
    fun createComparator(): Comparator<FileModel> {
        return compareBy<FileModel> { file ->
            NAME_UNIMPORTANT_PREFIXES.any { prefix -> file.fileName.startsWith(prefix) }
        }.thenBy { file ->
            when (sortBy) {
                SortBy.NAME -> file.fileName
                SortBy.TYPE -> file.fileExtension
                SortBy.SIZE -> file.fileSize
                else -> file.fileName
            }
        }.thenBy { file ->
            if (isDirectoriesFirst) {
                file.isDirectory
            } else {
                null
            }
        }.let { comparator ->
            if (order == Order.DESCENDING) {
                comparator.reversed()
            } else {
                comparator
            }
        }
    }

    companion object {
        private val NAME_UNIMPORTANT_PREFIXES = listOf(".", "#")
    }

    enum class SortBy {
        NAME,
        TYPE,
        SIZE,
        LAST_MODIFIED
    }

    enum class Order {
        ASCENDING, DESCENDING
    }
}

