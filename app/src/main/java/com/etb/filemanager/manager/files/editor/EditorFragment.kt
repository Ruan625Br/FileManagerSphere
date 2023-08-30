package com.etb.filemanager.manager.files.editor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.etb.filemanager.R

private const val ARG_FILE_PATH = "filePath"

/**
 * A simple [Fragment] subclass.
 * Use the [EditorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditorFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var filePath: String? = null
    private lateinit var codeEditor: CodeEditor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filePath = it.getString(ARG_FILE_PATH)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_editor, container, false)
    }

    fun setCodePath(path: String) {
        filePath = path
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param filePth File path.
         * @return A new instance of fragment EditorFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(filePth: String) =
            EditorFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_FILE_PATH, filePath)
                }
            }
    }
}