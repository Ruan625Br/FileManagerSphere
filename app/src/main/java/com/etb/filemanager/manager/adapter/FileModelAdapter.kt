package com.etb.filemanager.manager.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.etb.filemanager.R
import com.etb.filemanager.files.file.common.mime.MidiaType
import com.etb.filemanager.files.file.common.mime.MimeTypeUtil
import com.etb.filemanager.files.file.common.mime.getMidiaType
import com.etb.filemanager.interfaces.manager.FileAdapterListenerUtil
import com.etb.filemanager.interfaces.manager.FileListener
import com.etb.filemanager.interfaces.settings.util.SelectPreferenceUtils
import com.etb.filemanager.manager.files.filelist.FileItemSet
import com.etb.filemanager.manager.files.filelist.PickOptions
import com.etb.filemanager.manager.files.filelist.fileItemSetOf
import com.etb.filemanager.manager.files.ui.AnimatedListAdapter
import com.etb.filemanager.manager.util.FileUtils
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
    /*private var fileModels: MutableList<FileModel>*/ private val mContext: Context, private val listener: FileListener
) : AnimatedListAdapter<FileModel, FileModelAdapter.ViewHolder>(CALLBACK) {

    private val fileUtils: FileUtils = FileUtils.getInstance()
    private val basePath = "/storage/emulated/0"
    private var currentPath = basePath

    private lateinit var selectPreferenceUtils: SelectPreferenceUtils
    private lateinit var fileAdapterListenerUtil: FileAdapterListenerUtil


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

   /* fun selectAllFiles() {
        val files = fileItemSetOf()
        val selectedFiles = selectedFiles.toSet() // Salva os itens já selecionados antes do "selectAll"
        for (index in 0 until itemCount) {
            val file = getItem(index)
            files.add(file)
        }
        listener.selectFiles(files, true)
        for (index in files.indices) {
            toggleItemSelection(index)
        }
        for (file in selectedFiles) {
            val index = fileModels.indexOf(file)
            if (index != -1) {
                toggleItemSelection(index)
            }
        }
        notifyDataSetChanged()
    }
*/
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

    fun toggleItemSelection(position: Int) {
        val file = getItem(position)
        file.isSelected = !file.isSelected
        //notifyItemChanged(position)
    }
    @SuppressLint("NotifyDataSetChanged")
     fun clearItemSelection() {
        for (index in 0 until itemCount) {
            val file = getItem(index)

            file.isSelected = false
        }
       // notifyDataSetChanged()
    }

    @Deprecated("", ReplaceWith("replaceList(list)"))
    override fun replace(list: List<FileModel>, clear: Boolean) {
        throw UnsupportedOperationException()
    }


    fun replaceList(list: List<FileModel>) {
        super.replace(list, true)
        rebuildFilePositionMap()
    }

     fun rebuildFilePositionMap() {
        filePositionMap.clear()
        for (index in 0 until itemCount) {
            val file = getItem(index)
            filePositionMap[file.filePath] = index
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileModelAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false)
        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: FileModelAdapter.ViewHolder, position: Int){
        throw UnsupportedOperationException()
    }
    override fun onBindViewHolder(holder: FileModelAdapter.ViewHolder, position: Int, payloads: List<Any>) {
        bindViewHolderAnimation(holder)
        val file = getItem(position)
        val mimeTypeUtil = MimeTypeUtil()
        val filePath = file.filePath
        val colorUtil = ColorUtil()
        val iconUtil = IconUtil()
        val mimeType = getFileMimeType(file.filePath)
        val selected = file in selectedFiles
        if (payloads.isNotEmpty()){
            return
        }


        if (!file.isDirectory) {

            if (mimeType != null && mimeType.isMimeTypeMedia()) {
                val midiaType = getMidiaType(mimeType)
                when (midiaType) {
                    MidiaType.IMAGE -> {
                        loadImage(filePath, holder)
                    }

                    MidiaType.VIDEO -> {
                        loadImage(filePath, holder)
                    }

                    else -> {
                        loadImage(filePath, holder)
                    }
                }
            } else {

                val tint = colorUtil.getColorPrimaryInverse(mContext)
                val icFile = mContext.getDrawable(R.drawable.file_generic_icon)
                icFile?.setTint(tint)

                val iconResourceId = mimeType?.let { mimeTypeUtil.getIconByMimeType(it) } ?: icFile

                holder.iconFile.setColorFilter(tint, PorterDuff.Mode.SRC_IN)

                Glide.with(mContext).load(iconResourceId).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions().placeholder(icFile)).into(holder.iconFile)

            }

        }

        selectPreferenceUtils = SelectPreferenceUtils.getInstance()
        fileAdapterListenerUtil = FileAdapterListenerUtil.getInstance()

        currentPath = file.filePath


        holder.titleFile.text = file.fileName

        holder.itemFile.setOnClickListener {
            if (selectedFiles.isEmpty()) {
                listener.openFile(file)

            } else {
                selectFile(file)
            }
        }
        holder.itemFile.setOnLongClickListener {
            if (selectedFiles.isEmpty()) {
                selectFile(file)
            } else {
                listener.showBottomSheet(file)
            }
            true
        }
        holder.itemBorder.setOnClickListener { selectFile(file) }

        if (selected || file.isSelected) {
            holder.itemFile.background = iconUtil.getBackgroundItemSelected(mContext)
        } else {
            holder.itemFile.background = iconUtil.getBackgroundItemNormal(mContext)
        }
        if (file.isDirectory) {
            holder.dateFile.visibility = View.GONE
            holder.sizeFile.visibility = View.GONE

            holder.iconPreview.visibility = View.GONE
            holder.iconFile.setImageDrawable(iconUtil.getIconFolder(mContext))

        } else {
            holder.dateFile.visibility = View.VISIBLE
            holder.sizeFile.visibility = View.VISIBLE
            holder.dateFile.text = fileUtils.getFormatDateFile(file.filePath, true)
            holder.sizeFile.text = fileUtils.getFileSizeFormatted(file.fileSize)


        }


    }

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

    fun Int.setTintResource(context: Context, color: Int) {
        val drawable = AppCompatResources.getDrawable(context, this)
        drawable?.setTint(color)
    }


    private fun loadImage(path: String, holder: ViewHolder) {
        val iconUtil = IconUtil()
        holder.iconFile.visibility = View.GONE
        holder.iconPreview.visibility = View.VISIBLE
        holder.itemBorder.background = iconUtil.getBorderPreview(mContext)
        Glide.with(mContext).load(path).diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions().override(50, 50)).apply(RequestOptions().placeholder(R.drawable.ic_image))
            .into(holder.iconPreview)


    }

    private fun String.isMimeTypeMedia(): Boolean {
        val mediaMimeTypes = listOf("video/", "audio/", "image/", "apk")
        val mimeType = this.lowercase(Locale.getDefault())
        return mediaMimeTypes.any { mimeType.startsWith(it) }
    }


    class ViewHolder(ItemFileView: View) : RecyclerView.ViewHolder(ItemFileView) {
        // lateinit var itemDetails: Details


        val titleFile: TextView = itemView.findViewById(R.id.tv_file_title)
        val dateFile: TextView = itemView.findViewById(R.id.tv_file_date)
        val sizeFile: TextView = itemView.findViewById(R.id.tv_file_size)
        val iconFile: ImageView = itemView.findViewById(R.id.iv_icon_file)
        val itemFile: LinearLayout = itemView.findViewById(R.id.item_file)
        val itemBorder: LinearLayout = itemView.findViewById(R.id.linearLayout2)
        val iconPreview: ImageView = itemView.findViewById(R.id.iv_preview)


    }

    override fun clear() {
        super.clear()
        rebuildFilePositionMap()
    }

    override val isAnimationEnabled: Boolean
        get() = true

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
