package com.etb.filemanager.manager.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.etb.filemanager.R
import com.etb.filemanager.databinding.FileItemBinding
import com.etb.filemanager.files.provider.archive.common.mime.MimeType
import com.etb.filemanager.files.provider.archive.common.mime.MimeTypeUtil
import com.etb.filemanager.files.provider.archive.common.mime.asMimeType
import com.etb.filemanager.files.provider.archive.common.mime.getMidiaType
import com.etb.filemanager.files.provider.archive.common.mime.isASpecificTypeOfMime
import com.etb.filemanager.files.util.FileUtil
import com.etb.filemanager.files.util.getDimension
import com.etb.filemanager.files.util.getDimensionPixelSize
import com.etb.filemanager.files.util.layoutInflater
import com.etb.filemanager.interfaces.manager.FileListener
import com.etb.filemanager.manager.files.filelist.FileItemSet
import com.etb.filemanager.manager.files.filelist.PickOptions
import com.etb.filemanager.manager.files.filelist.fileItemSetOf
import com.etb.filemanager.manager.files.ui.AnimatedListAdapter
import com.etb.filemanager.manager.files.ui.CheckableItemBackground
import com.etb.filemanager.manager.util.FileUtils
import com.etb.filemanager.settings.preference.InterfacePreferences
import com.etb.filemanager.settings.preference.Preferences
import com.etb.filemanager.ui.style.ColorUtil
import com.etb.filemanager.ui.style.IconUtil
import me.zhanghai.android.fastscroll.PopupTextProvider
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class FileModelAdapter(
    private val mContext: Context, private val listener: FileListener
) : AnimatedListAdapter<FileModel, FileModelAdapter.ViewHolder>(CALLBACK), PopupTextProvider {

    private val fileUtils: FileUtils = FileUtils.getInstance()
    private val basePath = "/storage/emulated/0"
    private var currentPath = basePath

    private val defaultComparator: Comparator<FileModel> =
        compareBy<FileModel> { it.fileName }.thenBy { it.fileName }
    private lateinit var _comparator: Comparator<FileModel>
    private var isSearching = false
    var comparator: Comparator<FileModel>
        get() {
            if (!::_comparator.isInitialized) {
                _comparator = defaultComparator
            }
            return _comparator
        }
        set(value) {
            _comparator = value
            if (!isSearching) {
                super.replace(list.sortedWith(value), true)
                rebuildFilePositionMap()
            }
        }

    var pickOptions: PickOptions? = null
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount, PAYLOAD_STATE_CHANGED)
        }

    private val selectedFiles = fileItemSetOf()
    private val filePositionMap = mutableMapOf<String, Int>()


    fun replaceSelectedFiles(files: FileItemSet) {
        val changedFiles = fileItemSetOf()
        val iterator = selectedFiles.iterator()
        while (iterator.hasNext()) {
            val file = iterator.next()
            if (file !in files) {
                iterator.remove()
                changedFiles.add(file)
            }
        }
        for (file in files) {
            if (file !in selectedFiles) {
                selectedFiles.add(file)
                changedFiles.add(file)
            }
        }
        for (file in changedFiles) {

            val position = filePositionMap[file.filePath]
            position?.let { notifyItemChanged(it, PAYLOAD_STATE_CHANGED) }
        }

    }

    private fun selectFile(file: FileModel) {
        if (!isFileSelectable(file)) {
            return
        }
        val selected = file in selectedFiles
        val pickOptions = pickOptions
        if (!selected && pickOptions != null && !pickOptions.allowMultiple) {
            listener.clearSelectedFiles()

        }
        listener.selectFile(file, !selected)
    }

    private fun isFileSelectable(file: FileModel): Boolean {
        val pickOptions = pickOptions ?: return true
        return if (pickOptions.pickDirectory) {
            file.isDirectory
        } else {
            !file.isDirectory
        }
    }

    fun selectAllFiles() {

        val files = fileItemSetOf()
        for (index in 0 until itemCount) {
            val file = getItem(index)
            if (isFileSelectable(file)) {
                files.add(file)
            }
        }
        listener.selectFiles(files, true)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearItemSelection() {
        for (index in 0 until itemCount) {
            val file = getItem(index)

        }
    }

    @Deprecated("", ReplaceWith("replaceList(list)"))
    override fun replace(list: List<FileModel>, clear: Boolean) {
        throw UnsupportedOperationException()
    }


    fun replaceList(list: List<FileModel>, isSearching: Boolean) {
        val clear = this.isSearching != isSearching
        this.isSearching = isSearching
        super.replace(if (!isSearching) list.sortedWith(comparator) else list, clear)
        rebuildFilePositionMap()
    }

    fun rebuildFilePositionMap() {
        filePositionMap.clear()
        for (index in 0 until itemCount) {
            val file = getItem(index)
            filePositionMap[file.filePath] = index
        }
    }

    private fun applyStyle(binding: FileItemBinding) {
        if (Preferences.Interface.isEnabledRoundedCorners) {
            mContext.let { ctx ->
                binding.itemFile.radius = ctx.getDimension(R.dimen.corner_radius_base)
                binding.layoutBase.layoutParams = binding.layoutBase.layoutParams.apply {
                    if (this is ViewGroup.MarginLayoutParams) {
                        val padding = ctx.getDimensionPixelSize(R.dimen.spacing_tiny)
                        setMargins(padding, padding, padding, padding)
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        FileItemBinding.inflate(parent.context.layoutInflater, parent, false)
    ).apply {
        applyStyle(binding)
        binding.itemFile.background = CheckableItemBackground.create(binding.itemFile.context)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        throw UnsupportedOperationException()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        bindViewHolderAnimation(holder)
        val file = getItem(position)
        val binding = holder.binding
        val filePath = file.filePath
        val mimeType = FileUtil().getMimeType(null, filePath)
        val selected = file in selectedFiles
        val isDirectory = file.isDirectory
        currentPath = file.filePath
        binding.itemFile.isChecked = selected
        if (payloads.isNotEmpty()) {
            return
        }

        setVisibility(isDirectory, mimeType, binding)
        if (!isDirectory) {
            if (mimeType != null && mimeType.isMimeTypeMedia()) {
                loadImage(filePath, binding, mimeType)

            } else {
                getIconByMimeType(mimeType, binding)

            }

        }


        binding.fileTitle.text = file.fileName
        binding.fileSize.text = FileUtils().getFileSizeFormatted(file.fileSize)
        binding.fileDate.text = FileUtils().getFormatDateFile(filePath, true)

        binding.itemFile.setOnClickListener {
            if (selectedFiles.isEmpty()) {
                listener.openFile(file)

            } else {
                selectFile(file)
            }
        }
        binding.itemFile.setOnLongClickListener {
            if (Preferences.Behavior.selectFileLongClick) {
                if (selectedFiles.isEmpty()) {
                    selectFile(file)
                } else {
                    listener.openFile(file)
                }
            } else {
                listener.showBottomSheet(file)
            }

            true
        }
        binding.itemBorder.setOnClickListener { selectFile(file) }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getFileMimeType(mPath: String): String? {
        val path: Path = Paths.get(mPath)
        val mimeType: String?
        try {
            mimeType = Files.probeContentType(path)
        } catch (e: Exception) {
            Log.e("Get File", "Erro: $e")
            return null
        }
        return mimeType
    }

    private fun setVisibility(isDir: Boolean, mimeType: String?, binding: FileItemBinding) {
        val iconUtil = IconUtil()
        val isMedia = mimeType?.isMimeTypeMedia()
        val viewFileInformationOption = Preferences.Interface.viewFileInformationOption
        binding.fileDate.visibility = when (viewFileInformationOption) {
            InterfacePreferences.ViewFileInformationOption.DATE_ONLY -> if (isDir) View.GONE else View.VISIBLE
            InterfacePreferences.ViewFileInformationOption.EVERYTHING -> if (isDir) View.GONE else View.VISIBLE
            else -> {
                View.GONE
            }
        }
        binding.fileSize.visibility = when (viewFileInformationOption) {
            InterfacePreferences.ViewFileInformationOption.SIZE_ONLY -> if (isDir) View.GONE else View.VISIBLE
            InterfacePreferences.ViewFileInformationOption.EVERYTHING -> if (isDir) View.GONE else View.VISIBLE
            else -> {
                View.GONE
            }
        }

        binding.iconPreview.visibility = if (isMedia == true) View.VISIBLE else View.GONE
        binding.iconFile.visibility = if (isMedia == true) View.GONE else View.VISIBLE


        binding.itemBorder.background =
            if (isMedia == true) iconUtil.getBorderPreview(mContext) else iconUtil.getBorderNormal(
                mContext
            )
        if (isDir) binding.iconFile.setImageDrawable(iconUtil.getIconFolder(mContext))
    }


    private fun getIconByMimeType(mimeType: String?, binding: FileItemBinding) {
        val tint = ColorUtil().getColorPrimaryInverse(mContext)
        val icFile = mContext.getDrawable(R.drawable.file_generic_icon)
        icFile?.setTint(tint)

        val iconResourceId = mimeType?.let { MimeTypeUtil().getIconByMimeType(it) } ?: icFile

        binding.iconFile.setColorFilter(tint, PorterDuff.Mode.SRC_IN)

        Glide.with(mContext).load(iconResourceId).diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions().placeholder(icFile)).into(binding.iconFile)

    }

    private fun loadImage(path: String, binding: FileItemBinding, mimeType: String) {
        val mimeTypeObject = MimeType(mimeType)
        val isApk = mimeTypeObject.isASpecificTypeOfMime(MimeType.APK)
        val imageView = binding.iconPreview

        Glide.with(mContext)
            .load(path)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .override(50, 50)
            .placeholder(R.drawable.ic_image)
            .into(imageView)
    }

    private fun setPreview(preview: Drawable, placeholder: Drawable, binding: FileItemBinding) {
        Glide.with(mContext).load(preview).diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions().placeholder(placeholder)).into(binding.iconFile)


    }

    private fun String.isMimeTypeMedia(): Boolean {
        val mediaMimeTypes = listOf("video/", "audio/", "image/")
        val mimeType = this.lowercase(Locale.getDefault())
        return mediaMimeTypes.any { mimeType.startsWith(it) }
    }


    class ViewHolder(val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun clear() {
        super.clear()
        rebuildFilePositionMap()
    }

    override val isAnimationEnabled: Boolean
        get() = Preferences.Interface.isAnimationEnabledForFileList

    companion object {
        private val PAYLOAD_STATE_CHANGED = Any()

        private val CALLBACK = object : DiffUtil.ItemCallback<FileModel>() {
            override fun areItemsTheSame(oldItem: FileModel, newItem: FileModel): Boolean =
                oldItem.filePath == newItem.filePath


            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: FileModel, newItem: FileModel): Boolean =
                oldItem == newItem

        }

    }

    override fun getPopupText(position: Int): CharSequence {
        val file = getItem(position)
        val locale = Locale(Preferences.Interface.language)
        return file.fileName.take(1).uppercase(locale)
    }

}
