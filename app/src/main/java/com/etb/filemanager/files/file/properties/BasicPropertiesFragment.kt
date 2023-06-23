package com.etb.filemanager.files.file.properties


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.etb.filemanager.R



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
            fileProperties = it.getParcelableArrayList(ARG_FILE_PROPERTIES)


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

    fun addListProperties(fileBasicProperties: MutableList<FileProperties>){
        for (properties in fileBasicProperties){
            addProperties(properties.title, properties.property)
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
    private fun addProperties(title: String, text: String){
        val inflater = LayoutInflater.from(requireContext())
        val filePropertiesItem = inflater.inflate(R.layout.file_properties_item, null)
        val tvTitle = filePropertiesItem.findViewById<TextView>(R.id.tvTitle)
        val tvText = filePropertiesItem.findViewById<TextView>(R.id.tvText)

        tvTitle.text = title
        tvText.text = text


        linearLayout.addView(filePropertiesItem)

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