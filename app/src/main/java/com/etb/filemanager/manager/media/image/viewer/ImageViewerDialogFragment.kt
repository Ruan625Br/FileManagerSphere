package com.etb.filemanager.manager.media.image.viewer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.etb.filemanager.R
import com.etb.filemanager.databinding.FragmentImageViewerDialogBinding
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.pathString


class ImageViewerDialogFragment : DialogFragment() {

    private var _binding: FragmentImageViewerDialogBinding? = null
    private val binding get() = _binding!!
    private var imagePathList: List<Path>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imagePathList =
                arguments?.getStringArrayList(ARG_IMAGE_PATH_LIST)?.map { Paths.get(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageViewerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
        val imageViewerAdapter =
            imagePathList?.let { ImageViewerStateAdapter(fragmentManager, lifecycle, it) }
        binding.viewPager.adapter = imageViewerAdapter
    }

    companion object {
        const val TAG = "ImageViewerDialogFragment"
        const val ARG_IMAGE_PATH_LIST = "imagePathList"

        @JvmStatic
        fun newInstance(imagePathList: List<Path>) =
            ImageViewerItemFragment().apply {
                arguments = Bundle().apply {
                    putStringArrayList(
                        ARG_IMAGE_PATH_LIST,
                        ArrayList(imagePathList.map { it.pathString })
                    )

                }
            }
    }
}