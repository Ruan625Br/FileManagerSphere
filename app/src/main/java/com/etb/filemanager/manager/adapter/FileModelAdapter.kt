package com.etb.filemanager.manager.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.etb.filemanager.R
import com.etb.filemanager.interfaces.manager.FileAdapterListenerUtil
import com.etb.filemanager.interfaces.settings.PopupSettingsListener
import com.etb.filemanager.interfaces.settings.util.SelectPreferenceUtils
import com.etb.filemanager.manager.util.FileUtils
import com.etb.filemanager.util.file.style.IconUtil
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.*


class FileModelAdapter(
    private var fileModels: MutableList<FileModel>, private val mContext: Context
) : RecyclerView.Adapter<FileModelAdapter.ViewHolder>() {

    private val fileUtils: FileUtils = FileUtils.getInstance()
    private lateinit var popupSettings: PopupSettingsListener
    private var pathStack = Stack<String>()
    private val basePath = "/storage/emulated/0"
    private var currentPath = basePath

    private lateinit var selectPreferenceUtils: SelectPreferenceUtils
    private lateinit var fileAdapterListenerUtil: FileAdapterListenerUtil


    private val selectedItems = mutableListOf<FileModel>()
    private var isActionMode = false
    private val mainScope = MainScope()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileModelAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false)

        return ViewHolder(view)
    }

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: FileModelAdapter.ViewHolder, position: Int) {
        val fileViewModel = fileModels[position]
        val item = fileModels[position]

        selectPreferenceUtils = SelectPreferenceUtils.getInstance()
        fileAdapterListenerUtil = FileAdapterListenerUtil.getInstance()

        currentPath = fileViewModel.filePath
        val iconUtil = IconUtil()


        mainScope.launch {
            if (selectedItems.contains(fileViewModel)) {
                holder.itemFile.background = iconUtil.getBackgroundItemSelected(mContext)
            } else {
                holder.itemFile.background = iconUtil.getBackgroundItemNormal(mContext)
            }
        }


        holder.titleFile.text = fileViewModel.fileName

        holder.itemFile.setOnClickListener {

            val mPath = fileViewModel.filePath
            fileAdapterListenerUtil.addItemClick(item, mPath, fileViewModel.isDirectory)
            Log.i("ADAPTER", "CLICK")
            Log.i("ADAPTER", "PATH  ${fileViewModel.filePath}")

        }
        holder.itemFile.setOnLongClickListener {
            if (!isActionMode) {
                isActionMode = true
               // holder.itemFile.background = iconUtil.getBackgroundItemSelected(mContext)


                fileAdapterListenerUtil.addItemOnLongClick(item, isActionMode)
                Log.i("INFO", "CLICOUUU")
            }

            true
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


        if (isImageOrVideo(fileViewModel.fileName)) {
            val mFile = File(fileViewModel.filePath)

            if (isImage(mFile)) {
                mainScope.launch {

                    holder.iconFile.visibility = View.GONE
                    holder.iconPreview.visibility = View.VISIBLE
                    holder.itemBorder.background = iconUtil.getBorderPreview(mContext)
                    iconUtil.getPreview(
                        IconUtil.OptionFile.IMAGE, mContext, fileViewModel.filePath, holder.iconPreview
                    )


                }

            } else if (isVideo(mFile)) {
                mainScope.launch {
                    holder.iconFile.visibility = View.GONE
                    holder.iconPreview.visibility = View.VISIBLE
                    holder.itemBorder.background = iconUtil.getBorderPreview(mContext)
                    iconUtil.getPreview(
                        IconUtil.OptionFile.VIDEO,
                        mContext,
                        fileViewModel.filePath,
                        holder.iconPreview
                    )
                }

            }
        } else{
            holder.iconPreview.visibility = View.GONE
            holder.iconFile.setImageDrawable(fileUtils.getFileIconByExtension(mContext, fileViewModel.file))

        }

    }

    //Code test
    private fun isImageOrVideo(title: String): Boolean{
        if (title.endsWith(".png") ||title.endsWith(".jpg") || title.endsWith(".jpeg")){
            return true
        } else if (title.endsWith(".mp4")){
            return true
        }
        return false
    }


    private fun isImage(file: File): Boolean {
        val fileExtension = fileUtils.getFileExtension(file)
        return fileExtension in listOf("png", "jpg", "jpeg")
    }

    private fun isVideo(file: File): Boolean {
        val fileExtension = fileUtils.getFileExtension(file)
        return fileExtension in listOf("mp4")
    }

    override fun getItemCount(): Int {
        return fileModels.size
    }

    class ViewHolder(ItemFileView: View) : RecyclerView.ViewHolder(ItemFileView) {
        val titleFile: TextView = itemView.findViewById(R.id.tv_file_title)
        val dateFile: TextView = itemView.findViewById(R.id.tv_file_date)
        val sizeFile: TextView = itemView.findViewById(R.id.tv_file_size)
        val iconFile: ImageView = itemView.findViewById(R.id.iv_icon_file)
        val itemFile: LinearLayout = itemView.findViewById(R.id.item_file)
        val itemBorder: LinearLayout = itemView.findViewById(R.id.linearLayout2)
        val iconPreview: ImageView = itemView.findViewById(R.id.iv_preview)


    }

    fun getPreviousPath(): String {
        if (!pathStack.isEmpty()) {
            if (pathStack.size == 1) {
                // Caso especial: voltando para o caminho base
                pathStack.pop()
                return basePath
            } else {
                // Obtém o caminho anterior
                pathStack.pop() // Remove o caminho atual da pilha
                val previousPath = pathStack.peek()
                return previousPath
            }
        } else {
            return basePath
        }
    }

    fun addToPathStack(path: String) {
        pathStack.push(path)
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
        val position = fileModels.indexOf(item) // Encontra a posição do item na lista do Adapter
        if (position != -1) {
            fileModels[position] = item // Atualiza o item na lista do Adapter
             selectedItems.remove(item)
            notifyItemChanged(position) // Notifica o Adapter sobre a mudança no item específico
        }

    }

    fun clearAllItemSelectedItem() {
        selectedItems.clear()
        isActionMode = false
        notifyDataSetChanged()

    }

    fun getSizeItemSelected(): Int{
        return selectedItems.size
    }

    fun setSelectedItem(item: FileModel) {
        mainScope.launch {
            if (selectedItems.contains(item)){
                removeSelectedItem(item)
            } else{
                val position = fileModels.indexOf(item) // Encontra a posição do item na lista do Adapter
                if (position != -1) {
                    fileModels[position] = item // Atualiza o item na lista do Adapter
                    selectedItems.add(item)
                    notifyItemChanged(position) // Notifica o Adapter sobre a mudança no item específico
                }
            }

        }

    }



    private fun setCenterVertical(mTextView: TextView) {
        val layoutParams = mTextView.layoutParams as RelativeLayout.LayoutParams
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
        mTextView.layoutParams = layoutParams
    }

}