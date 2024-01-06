/*
 * Copyright (c)  2023  Juan Nascimento
 * Part of FileManagerSphere - BasicPropertiesFragment.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.files.provider.archive.common.properties


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.etb.filemanager.R
import com.etb.filemanager.files.extensions.parcelable
import com.etb.filemanager.files.provider.archive.common.mime.MediaType


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_FILE_PROPERTIES = "fileProperties"

/**
 * A simple [Fragment] subclass.
 * Use the [BasicPropertiesFragment.newInstance] factory method to
 * create an instance of this fragment.
 * teste
 */
class BasicPropertiesFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var fileProperties: MutableList<FileProperties>? = null

    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            fileProperties = it.parcelable(ARG_FILE_PROPERTIES)


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_basic_properties, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayout = requireView().findViewById(R.id.linearLayout)

        fileProperties?.let { addListProperties(it) }

    }

    /**
     * Adicione uma lista de propriedades ao [BasicPropertiesFragment] usando o método [addProperties].
     *
     * @param fileBasicProperties Uma lista mutável do tipo [FileProperties] contendo as propriedades a serem adicionadas.
     * @author Juan Nascimento
     */

    fun addListProperties(fileBasicProperties: MutableList<FileProperties>) {
        for (properties in fileBasicProperties) {
            addProperties(properties.title, properties.property, properties.isMedia, properties.mediaType, properties.mediaPath)
        }
    }


    /**
     * Adicione uma propriedade ao layout principal.
     * Para adicionar várias propriedades, utilize o método [addListProperties].
     *
     * @param title O título da propriedade.
     * @param text O texto da propriedade.
     * @see addListProperties
     * @author Juan Nascimento
     */
    @SuppressLint("InflateParams")
    private fun addProperties(
        title: String, text: String, isMedia: Boolean,
        mediaType: MediaType = MediaType.VIDEO,
        mediaPath: String = ""
    ) {
        val inflater = LayoutInflater.from(requireContext())
        val inflaterMedia = LayoutInflater.from(requireContext())

        val filePropertiesItem = inflater.inflate(R.layout.file_properties_item, null)
        val filePropertiesItemMedia = inflaterMedia.inflate(R.layout.file_properties_item_media, null)

        val tvTitle = filePropertiesItem.findViewById<TextView>(R.id.tvTitle)
        val tvText = filePropertiesItem.findViewById<TextView>(R.id.tvText)

        val ivMedia = filePropertiesItemMedia.findViewById<ImageView>(R.id.iv_preview)

        if (isMedia) {
            when (mediaType) {
                MediaType.IMAGE -> loadImage(mediaPath, ivMedia)
                MediaType.VIDEO -> loadImage(mediaPath, ivMedia)
                else -> {}
            }
            linearLayout.addView(filePropertiesItemMedia)
        } else {
            tvTitle.text = title
            tvText.text = text


            linearLayout.addView(filePropertiesItem)
        }
    }

    private fun loadImage(path: String, imageView: ImageView) {
        Glide.with(requireContext()).load(path).diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions().override(50, 50)).apply(RequestOptions().placeholder(R.drawable.ic_image))
            .into(imageView)

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, fileProperties: MutableList<FileProperties>) =
            BasicPropertiesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putParcelableArrayList(ARG_FILE_PROPERTIES, ArrayList(fileProperties))
                }
            }
    }
}