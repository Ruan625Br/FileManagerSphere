package com.etb.filemanager.manager.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.etb.filemanager.R
import com.etb.filemanager.databinding.FileItemBinding
import com.etb.filemanager.files.file.common.mime.MidiaType
import com.etb.filemanager.files.file.common.mime.MimeTypeUtil
import com.etb.filemanager.files.file.common.mime.getMidiaType
import com.etb.filemanager.files.util.layoutInflater
import com.etb.filemanager.interfaces.manager.FileAdapterListenerUtil
import com.etb.filemanager.interfaces.manager.FileListener
import com.etb.filemanager.interfaces.settings.util.SelectPreferenceUtils
import com.etb.filemanager.manager.files.filelist.FileItemSet
import com.etb.filemanager.manager.files.filelist.PickOptions
import com.etb.filemanager.manager.files.filelist.fileItemSetOf
import com.etb.filemanager.manager.files.ui.AnimatedListAdapter
import com.etb.filemanager.manager.files.ui.CheckableItemBackground
import com.etb.filemanager.manager.files.ui.SelectableMaterialCardView
import com.etb.filemanager.manager.util.FileUtils
import com.etb.filemanager.settings.preference.Preferences
import com.etb.filemanager.util.file.style.ColorUtil
import com.etb.filemanager.util.file.style.IconUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.UnsupportedOperationException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class FileModelAdapter(
   private val mContext: Context, private val listener: FileListener
) : AnimatedListAdapter<FileModel, FileModelAdapter.ViewHolder>(CALLBACK) {

    private val fileUtils: FileUtils = FileUtils.getInstance()
    private val basePath = "/storage/emulated/0"
    private var currentPath = basePath

    private val defaultComparator: Comparator<FileModel> = compareBy<FileModel> { it.fileName }.thenBy { it.fileName }
    private lateinit var _comparator: Comparator<FileModel>
    var comparator: Comparator<FileModel>
        get()  {
            if (!::_comparator.isInitialized) {
                _comparator = defaultComparator
            }
            return _comparator
        }
        set(value) {
            _comparator = value
            super.replace(list.sortedWith(value), true)
            rebuildFilePositionMap()
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


    fun replaceList(list: List<FileModel>) {
        super.replace(list.sortedWith(comparator), false)
        rebuildFilePositionMap()
    }

     fun rebuildFilePositionMap() {
        filePositionMap.clear()
        for (index in 0 until itemCount) {
            val file = getItem(index)
            filePositionMap[file.filePath] = index
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            FileItemBinding.inflate(parent.context.layoutInflater, parent, false)
        ).apply {
           binding.itemFile.background = CheckableItemBackground.create(binding.itemFile.context)


        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        throw UnsupportedOperationException()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>) {
        bindViewHolderAnimation(holder)
        val file = getItem(position)
        val binding = holder.binding
        val mimeTypeUtil = MimeTypeUtil()
        val filePath = file.filePath
        val colorUtil = ColorUtil()
        val iconUtil = IconUtil()
        val mimeType = getFileMimeType(file.filePath)
        val selected = file in selectedFiles
        val isDirectory = file.isDirectory
        binding.itemFile.isChecked = selected
        Log.i("TEESTEEEE", "AQUI: $selected")
        if (payloads.isNotEmpty()){
            return
        }


        if (!isDirectory) {

            if (mimeType != null && mimeType.isMimeTypeMedia()) {
                val midiaType = getMidiaType(mimeType)
                when (midiaType) {
                    MidiaType.IMAGE -> {
                        loadImage(filePath, binding)
                    }

                    MidiaType.VIDEO -> {
                        loadImage(filePath, binding)
                    }

                    else -> {
                        loadImage(filePath, binding)
                    }
                }
            } else {

                val tint = colorUtil.getColorPrimaryInverse(mContext)
                val icFile = mContext.getDrawable(R.drawable.file_generic_icon)
                icFile?.setTint(tint)

                val iconResourceId = mimeType?.let { mimeTypeUtil.getIconByMimeType(it) } ?: icFile

                binding.iconFile.setColorFilter(tint, PorterDuff.Mode.SRC_IN)

                Glide.with(mContext).load(iconResourceId).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions().placeholder(icFile)).into(binding.iconFile)

            }

        }

        currentPath = file.filePath

        binding.fileTitle.text = file.fileName

        binding.itemFile.setOnClickListener {
            if (selectedFiles.isEmpty()) {
                listener.openFile(file)

            } else {
                selectFile(file)
            }
        }
        binding.itemFile.setOnLongClickListener {
            if (Preferences.Behavior.selectFileLongClick){
                if (selectedFiles.isEmpty()) {
                    selectFile(file)
                } else {
                    listener.openFile(file)
                }
            } else{
                listener.showBottomSheet(file)
            }

            true
        }
        binding.itemBorder.setOnClickListener { selectFile(file) }


        if (file.isDirectory) {
            binding.fileDate.visibility = View.GONE
            binding.fileSize.visibility = View.GONE

            binding.iconPreview.visibility = View.GONE
            binding.iconFile.setImageDrawable(iconUtil.getIconFolder(mContext))

        } else {
            binding.fileDate.visibility = View.VISIBLE
            binding.fileSize.visibility = View.VISIBLE
            binding.fileDate.text = fileUtils.getFormatDateFile(file.filePath, true)
            binding.fileSize.text = fileUtils.getFileSizeFormatted(file.fileSize)


        }

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


    private fun loadImage(path: String, binding: FileItemBinding) {

        val iconUtil = IconUtil()
        binding.iconFile.visibility = View.GONE
        binding.iconPreview.visibility = View.VISIBLE
        binding.itemBorder.background = iconUtil.getBorderPreview(mContext)
        Glide.with(mContext).load(path).diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions().override(50, 50)).apply(RequestOptions().placeholder(R.drawable.ic_image))
            .into(binding.iconPreview)


    }

    private fun String.isMimeTypeMedia(): Boolean {
        val mediaMimeTypes = listOf("video/", "audio/", "image/", "apk")
        val mimeType = this.lowercase(Locale.getDefault())
        return mediaMimeTypes.any { mimeType.startsWith(it) }
    }


    class ViewHolder(val binding: FileItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun clear() {
        super.clear()
        rebuildFilePositionMap()
    }

    override val isAnimationEnabled: Boolean
        get() = Preferences.Appearance.isAnimationEnabledForFileList

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

}
