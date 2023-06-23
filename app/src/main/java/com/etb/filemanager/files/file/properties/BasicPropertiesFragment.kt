package com.etb.filemanager.files.file.properties

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.etb.filemanager.R
import com.etb.filemanager.ui.view.ReadOnlyEditText

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BasicPropertiesFragment.newInstance] factory method to
 * create an instance of this fragment.
 * teste
 */
class BasicPropertiesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
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


    }

    /**
     *  Use [addListProperties] para adicionar uma lista de propriedades
     *  na [BasicPropertiesFragment] através do [addProperties]
     *  @param fileBasicProperties adicione uma [MutableList] do tipo [FileProperties] para adicionar as propriedades
     *  @author Juan nascimento
     */

    fun addListProperties(fileBasicProperties: MutableList<FileProperties>){
        for (properties in fileBasicProperties){
            addProperties(properties.title, properties.propertie)
        }
    }



    /**
     * O [addProperties] serve para adicionar propiedade no layout principal.
     * para adicionar varias propriedades ultileze o [addListProperties]
     *
     * @param title Titulo da propiedade
     * @param text Texto da propiedade
     *
     * @author Juan Nascimento
     * */
    fun addProperties(title: String, text: String){
        val inflater = LayoutInflater.from(requireContext())
        val filePropertiesItem = inflater.inflate(R.layout.file_properties_item, null)
        val tvTitle = filePropertiesItem.findViewById<TextView>(R.id.tvTitle)
        val tvText = filePropertiesItem.findViewById<TextView>(R.id.tvText)

        tvTitle.setText(title)
        tvText.setText(text)


        linearLayout.addView(filePropertiesItem)

    }

    companion object {
          @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BasicPropertiesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}