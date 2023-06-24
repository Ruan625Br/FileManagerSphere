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
import androidx.recyclerview.selection.SelectionTracker
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
import com.etb.filemanager.interfaces.settings.PopupSettingsListener
import com.etb.filemanager.interfaces.settings.util.SelectPreferenceUtils
import com.etb.filemanager.manager.selection.Details
import com.etb.filemanager.manager.util.FileUtils
import com.etb.filemanager.util.file.style.ColorUtil
import com.etb.filemanager.util.file.style.IconUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class FileModelAdapter(
    private var fileModels: MutableList<FileModel>, private val mContext: Context, private val listener: FileListener
) : RecyclerView.Adapter<FileModelAdapter.ViewHolder>() {

    private val fileUtils: FileUtils = FileUtils.getInstance()
    private lateinit var popupSettings: PopupSettingsListener
    private var pathStack = Stack<String>()
    private val basePath = "/storage/emulated/0"
    private var currentPath = basePath

    private lateinit var selectPreferenceUtils: SelectPreferenceUtils
    private lateinit var fileAdapterListenerUtil: FileAdapterListenerUtil


    private val selectedItems = mutableListOf<FileModel>()
    var isActionMode = false
    private val mainScope = MainScope()

    var selectionTracker: SelectionTracker<Long>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileModelAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false)
        return ViewHolder(view)


    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: FileModelAdapter.ViewHolder, position: Int) {
        val fileViewModel = fileModels[position]
        val mimeTypeUtil = MimeTypeUtil()
        val filePath = fileViewModel.filePath
        val colorUtil = ColorUtil()
        val iconUtil = IconUtil()


        val mimeType = getFileMimeType(fileViewModel.filePath)


        if (!fileViewModel.isDirectory) {

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
        val itemDetails = Details(fileViewModel, position)
        holder.itemDetails = itemDetails


        selectPreferenceUtils = SelectPreferenceUtils.getInstance()
        fileAdapterListenerUtil = FileAdapterListenerUtil.getInstance()

        currentPath = fileViewModel.filePath


      if (isActionMode) {
            if (selectionTracker?.isSelected(itemDetails.selectionKey)!!) {
                holder.itemView.isActivated = true
                holder.itemFile.background = iconUtil.getBackgroundItemSelected(mContext)
            } else {
                holder.itemView.isActivated = false
                holder.itemFile.background = iconUtil.getBackgroundItemNormal(mContext)
            }
        } else {
            holder.itemView.isActivated = false
            holder.itemFile.background = iconUtil.getBackgroundItemNormal(mContext)
        }


        holder.itemFile.setOnLongClickListener {
            if (!isActionMode) {
                listener.showBottomSheet(fileViewModel)
            }

            true
        }


        holder.titleFile.text = fileViewModel.fileName

        holder.itemFile.setOnClickListener {
            if (!isActionMode) {
                if (fileViewModel.isDirectory) {
                    listener.openFile(fileViewModel)
                } else{
                    listener.openFileWith(fileViewModel)
                }
                holder.itemView.isActivated = false
                holder.itemFile.background = iconUtil.getBackgroundItemNormal(mContext)


            } else {
                if (selectionTracker?.isSelected(itemDetails.selectionKey)!!) {
                    holder.itemView.isActivated = true
                    holder.itemFile.background = iconUtil.getBackgroundItemSelected(mContext)
                } else {
                    holder.itemView.isActivated = false
                    holder.itemFile.background = iconUtil.getBackgroundItemNormal(mContext)
                }
                   }



        }

        if (fileViewModel.isDirectory) {
            holder.dateFile.visibility = View.GONE
            holder.sizeFile.visibility = View.GONE

            holder.iconPreview.visibility = View.GONE
            holder.iconFile.setImageDrawable(iconUtil.getIconFolder(mContext))

        } else {
            holder.dateFile.visibility = View.VISIBLE
            holder.sizeFile.visibility = View.VISIBLE
            holder.dateFile.text = fileUtils.getFormatDateFile(fileViewModel.filePath, true)
            holder.sizeFile.text = fileUtils.getFileSizeFormatted(fileViewModel.fileSize)


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

    override fun getItemCount(): Int {
        return fileModels.size
    }


    class ViewHolder(ItemFileView: View) : RecyclerView.ViewHolder(ItemFileView) {
        lateinit var itemDetails: Details


        val titleFile: TextView = itemView.findViewById(R.id.tv_file_title)
        val dateFile: TextView = itemView.findViewById(R.id.tv_file_date)
        val sizeFile: TextView = itemView.findViewById(R.id.tv_file_size)
        val iconFile: ImageView = itemView.findViewById(R.id.iv_icon_file)
        val itemFile: LinearLayout = itemView.findViewById(R.id.item_file)
        val itemBorder: LinearLayout = itemView.findViewById(R.id.linearLayout2)
        val iconPreview: ImageView = itemView.findViewById(R.id.iv_preview)


    }


    fun removeFile(fileName: String) {
        val fileToRemove = fileModels.firstOrNull { it.fileName == fileName }
        if (fileToRemove != null) {
            val position = fileModels.indexOf(fileToRemove)
            fileModels.removeAt(position)
            notifyItemRemoved(position)
        }

    }


    fun removeSelectedItem(item: FileModel) {
        val position = fileModels.indexOf(item)
        if (position != -1) {
            fileModels[position] = item
            selectedItems.remove(item)
            notifyItemChanged(position)
        }

    }



    fun setSelectedItem(item: FileModel) {
        mainScope.launch {
            if (selectedItems.contains(item)) {
                removeSelectedItem(item)
            } else {
                val position = fileModels.indexOf(item) // Encontra a posição do item na lista do Adapter
                if (position != -1) {
                    fileModels[position] = item // Atualiza o item na lista do Adapter
                    selectedItems.add(item)
                    notifyItemChanged(position) // Notifica o Adapter sobre a mudança no item específico
                }
            }

        }

    }

}
