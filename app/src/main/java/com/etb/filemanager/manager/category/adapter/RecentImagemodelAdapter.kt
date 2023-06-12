package com.etb.filemanager.manager.category.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.etb.filemanager.R
import com.etb.filemanager.manager.util.FileUtils

class RecentImagemodelAdapter(private var recentImageModel: List<RecentImageModel>, private val mContext: Context) :
    RecyclerView.Adapter<RecentImagemodelAdapter.ViewHolder>() {

    private val fileUtils: FileUtils = FileUtils.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentImagemodelAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recent_image_item, parent, false)

        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: RecentImagemodelAdapter.ViewHolder, position: Int) {
        val recentImageViewModel = recentImageModel[position]
        val imagePath = recentImageViewModel.imagePath

        Glide.with(mContext)
            .load(imagePath)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions().placeholder(R.drawable.ic_image))
            .into(holder.itemImage)




    }

    override fun getItemCount(): Int {
        return recentImageModel.size
    }

    class ViewHolder(itemImageView: View) : RecyclerView.ViewHolder(itemImageView) {
        val itemImage = itemImageView.findViewById<ImageView>(R.id.imageView)


    }

}