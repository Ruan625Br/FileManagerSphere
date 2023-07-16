package com.etb.filemanager.manager.files.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.core.content.ContextCompat
import com.etb.filemanager.R
import com.etb.filemanager.manager.files.filecoroutine.CompressionType
import com.etb.filemanager.manager.files.filecoroutine.FileOperation
import com.etb.filemanager.manager.files.services.FileOperationService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import java.nio.file.Paths
import kotlin.io.path.pathString

class ModalBottomSheetCompress : BottomSheetDialogFragment() {

    private var currentPath: String? = null
    private var paths: List<String>? = null

    private lateinit var typeGroup: RadioGroup
    private lateinit var btnCompress: Button
    private lateinit var eInputEditText: TextInputEditText
    private lateinit var compressionType: CompressionType
    private lateinit var extension: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentPath = it.getString(ARG_CURRENT_PATH)
            paths = it.getStringArrayList(ARG_PATHS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.bottom_sheet_compress_content, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        typeGroup = view.findViewById(R.id.typeGroup)
        btnCompress = view.findViewById(R.id.btnCompress)
        eInputEditText = view.findViewById(R.id.eInputEditText)

        btnCompress.setOnClickListener {
            if (eInputEditText.text != null) {
                when (val typeId = typeGroup.checkedRadioButtonId) {
                    R.id.zipRadio -> {
                        extension = ".zip"
                        compressionType = CompressionType.ZIP
                    }

                    R.id.tarRadio -> {
                        extension = ".tar"
                        compressionType = CompressionType.TAR
                    }

                    R.id.sevenZRadio -> {
                        extension = ".7z"
                        compressionType = CompressionType.SEVENZ
                    }

                    R.id.gzipRadio -> {
                        extension = ".gz"
                        compressionType = CompressionType.GZIP
                    }

                    R.id.xzRadio -> {
                        extension = ".xz"
                        compressionType = CompressionType.XZ
                    }
                    R.id.tarXzRadio -> {
                        extension = ".tar"
                        compressionType = CompressionType.TARXZ
                    }
                    R.id.tarGzRadio -> {
                        extension = ".tar"
                        compressionType = CompressionType.TARGZ
                    }

                    R.id.zstdRadio -> {
                        extension = ".zst"
                        compressionType = CompressionType.ZSTD
                    }
                    else -> throw AssertionError(typeId)
                }

                compressFiles()
            }

            dismiss()
        }

    }


    private fun compressFiles() {
        val fileName = eInputEditText.text.toString() + extension
        val mCurrentPath = Paths.get(currentPath).resolve(fileName)
        val outputStream = mCurrentPath.pathString

        val mPaths = paths
        val intent = Intent(requireContext(), FileOperationService::class.java)
        intent.putStringArrayListExtra("sourcePaths", java.util.ArrayList(mPaths!!))
        intent.putExtra("destinationPath", outputStream)
        intent.putExtra("operation", FileOperation.COMPRESS)
        intent.putExtra("compressionType", compressionType)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    companion object {
        const val TAG = "ModalBottomSheetCompress"
         const val ARG_CURRENT_PATH = "argCurrentPath"
         const val ARG_PATHS = "argPaths"

        @JvmStatic
        fun newInstance(currentPath: String, paths: List<String>) {
            ModalBottomSheetCompress().apply {
                arguments = Bundle().apply {
                    putString(ARG_CURRENT_PATH, currentPath)
                    putStringArrayList(ARG_PATHS, ArrayList(paths))
                }
            }
        }
    }
}