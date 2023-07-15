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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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
import com.etb.filemanager.util.file.FileUtil
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
        val filePath = file.filePath
        val mimeType = FileUtil().getMimeType(null,filePath)
        val selected = file in selectedFiles
        val isDirectory = file.isDirectory
        currentPath = file.filePath
        binding.itemFile.isChecked = selected
        if (payloads.isNotEmpty()){
            return
        }

        setVisibility(isDirectory, mimeType, binding)
        if (!isDirectory) {
            if (mimeType != null && mimeType.isMimeTypeMedia()) {
                val mediaType = getMidiaType(mimeType)
                when (mediaType) {
                    MidiaType.IMAGE -> {
                        loadImage(filePath, binding)
                    }

                    MidiaType.VIDEO -> {
                        loadImage(filePath, binding)
                    }

                 else ->{
                     loadImage(filePath, binding)
                 }
                }
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

        binding.fileDate.visibility = if (isDir) View.GONE else View.VISIBLE
        binding.fileSize.visibility = if (isDir) View.GONE else View.VISIBLE
        binding.iconPreview.visibility = if (isMedia == true) View.VISIBLE else View.GONE
            binding.iconFile.visibility = if (isMedia == true) View.GONE else View.VISIBLE


        binding.itemBorder.background = if (isMedia == true) iconUtil.getBorderPreview(mContext) else iconUtil.getBorderNormal(mContext)
        if (isDir) binding.iconFile.setImageDrawable(iconUtil.getIconFolder(mContext))
    }


    private fun getIconByMimeType(mimeType: String?, binding: FileItemBinding){
        val tint = ColorUtil().getColorPrimaryInverse(mContext)
        val icFile = mContext.getDrawable(R.drawable.file_generic_icon)
        icFile?.setTint(tint)

        val iconResourceId = mimeType?.let { MimeTypeUtil().getIconByMimeType(it) } ?: icFile

        binding.iconFile.setColorFilter(tint, PorterDuff.Mode.SRC_IN)

        Glide.with(mContext).load(iconResourceId).diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions().placeholder(icFile)).into(binding.iconFile)

    }

    private fun loadImage(path: String, binding: FileItemBinding) {

        Glide.with(binding.iconPreview)
            .clear(binding.iconPreview)
        Glide.with(mContext).load(path).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .apply(RequestOptions().override(50, 50)).apply(RequestOptions().placeholder(R.drawable.ic_image))
            .into(binding.iconPreview)

    }

    private fun setPreview(preview: Drawable, placeholder: Drawable, binding: FileItemBinding){
        Glide.with(mContext).load(preview).diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions().placeholder(placeholder)).into(binding.iconFile)


    }

    private fun String.isMimeTypeMedia(): Boolean {
        val mediaMimeTypes = listOf("video/", "audio/", "image/")
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

}
