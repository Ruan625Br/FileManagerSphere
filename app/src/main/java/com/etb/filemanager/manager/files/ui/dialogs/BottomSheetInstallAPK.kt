package com.etb.filemanager.manager.files.ui.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.etb.filemanager.databinding.FragmentBottomSheetInstallAPKListDialogBinding
import com.etb.filemanager.files.extensions.getPackageApk
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.adapter.loadFileItem
import com.etb.filemanager.manager.util.FileUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.nio.file.Path
import java.nio.file.Paths

class BottomSheetInstallAPK : BottomSheetDialogFragment() {

    private var path: Path? = null


    private var _binding: FragmentBottomSheetInstallAPKListDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            path = Paths.get(it.getString(ARG_PATH))
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding =
            FragmentBottomSheetInstallAPKListDialogBinding.inflate(inflater, container, false)
        return binding.root

    }

    @SuppressLint("WrongThread")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fileModel = path!!.loadFileItem()

        binding.iconAPK.setImageDrawable(FileUtils().getIconApk(requireContext(), fileModel.filePath))
        binding.nameAPK.text = fileModel.fileName
        binding.packageAPK.text = requireContext().getPackageApk(path!!)
        binding.installAPK.isClickable = false
        binding.viewAPK.isClickable = false

    }


    companion object {
        const val TAG = "BottomSheetInstallAPK"
        const val ARG_PATH = "argPath"

        fun newInstance(fileModel: FileModel): BottomSheetInstallAPK =
            BottomSheetInstallAPK().apply {
                arguments = Bundle().apply {
                    putString(ARG_PATH, fileModel.filePath)

                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}