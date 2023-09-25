/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - ImageViewerItemFragment.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.manager.media.image.viewer

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.etb.filemanager.R
import com.etb.filemanager.databinding.FragmentImageViewerItemBinding
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.pathString

private const val ARG_IMAGE_PATH = "imagePath"

class ImageViewerItemFragment : Fragment() {

    private var _binding: FragmentImageViewerItemBinding? = null
    private val binding get() = _binding!!
    private var imagePath: Path? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imagePath = Paths.get(it.getString(ARG_IMAGE_PATH))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageViewerItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadImageViewer()
    }


    @SuppressLint("InflateParams")
    private fun loadImageViewer() {
        val path = imagePath?.pathString
        val linearLayout = binding.linearLayout
        val inflater = LayoutInflater.from(requireContext())
        val imageViewerItem = inflater.inflate(R.layout.layout_image_viewer_item, null)

        val shapeableImageView =
            imageViewerItem.findViewById<ShapeableImageView>(R.id.is_shapeableImageView)

        lifecycleScope.launch {
            try {
                val bitmap = loadResizedImageAsync(path)

                val imageMaxWidth = 1920
                val imageMaxHeight = 1080
                var imageWidth = bitmap.width
                var imageHeight = bitmap.height

/*
                if (imageHeight > imageMaxHeight) {
                    imageWidth = imageMaxWidth
                    imageHeight = imageMaxHeight

                    val shapeAppearanceModel = ShapeAppearanceModel.builder().setAllCorners(
                            CornerFamily.ROUNDED,
                            resources.getDimensionPixelSize(R.dimen.corner_radius_base).toFloat()
                        ).build()
                    shapeableImageView.shapeAppearanceModel = shapeAppearanceModel
                    shapeableImageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    shapeableImageView.layoutParams.apply {
                        width = imageMaxWidth
                        height = imageHeight
                    }
                }
*/
                Glide.with(requireContext()).load(path).diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(
                        RequestOptions().override(imageWidth, imageHeight)
                            .placeholder(R.drawable.ic_image)
                    ).into(shapeableImageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        linearLayout.addView(imageViewerItem)
    }

    private suspend fun loadResizedImageAsync(path: String?): Bitmap =
        withContext(Dispatchers.Default) {
            val maxWidth = 800
            val maxHeight = 800
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(path, this)
                inSampleSize = calculateInSampleSize(this, maxWidth, maxHeight)
                inJustDecodeBounds = false
            }

            return@withContext BitmapFactory.decodeFile(path, options)
        }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options, maxWidth: Int, maxHeight: Int
    ): Int {
        val imageHeight = options.outHeight
        val imageWidth = options.outWidth
        var inSampleSize = 1

        if (imageHeight > maxHeight || imageWidth > maxWidth) {
            val halfHeight = imageHeight / 2
            val halfWidth = imageWidth / 2
            while ((halfHeight / inSampleSize) >= maxHeight && (halfWidth / inSampleSize) >= maxWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }


    companion object {
        @JvmStatic
        fun newInstance(path: Path) = ImageViewerItemFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_IMAGE_PATH, path.pathString)

            }
        }
    }
}