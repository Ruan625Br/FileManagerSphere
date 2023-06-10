package com.etb.filemanager.fragment

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etb.filemanager.R
import com.etb.filemanager.activity.MainActivity
import com.etb.filemanager.manager.category.adapter.CategoryFileModel
import com.etb.filemanager.manager.category.adapter.CategoryFileModelAdapter
import com.etb.filemanager.manager.category.adapter.RecentImageModel
import com.etb.filemanager.manager.category.adapter.RecentImagemodelAdapter
import com.etb.filemanager.manager.util.FileUtils
import com.etb.filemanager.manager.util.FileUtils.SpaceType
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecentFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var fileUtils: FileUtils

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

        return inflater.inflate(R.layout.fragment_recent, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fileUtils = FileUtils()
        setStorageSpaceInGB()
        initCategoryItem()
        initClick()
        setRecentImages()
    }

   @SuppressLint("SuspiciousIndentation")
   fun initCategoryItem() {
       val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)

       val categoryFileModels = ArrayList<CategoryFileModel>()
           categoryFileModels.add(CategoryFileModel(R.drawable.ic_image, "Images", "Sla", R.color.category_icon_blue))
           categoryFileModels.add(CategoryFileModel(R.drawable.ic_video, "Video", "Sla", R.color.category_icon_orange))
           categoryFileModels.add(CategoryFileModel(R.drawable.ic_document, "Document", "Sla", R.color.category_icon_purple))
           categoryFileModels.add(CategoryFileModel(R.drawable.ic_music, "Music", "Sla", R.color.category_icon_pink))
           categoryFileModels.add(CategoryFileModel(R.drawable.ic_archive, "Archive", "Sla", R.color.category_icon_light_green))
           categoryFileModels.add(CategoryFileModel(R.drawable.ic_download, "Download", "Sla", R.color.category_icon_light_red))
           categoryFileModels.add(CategoryFileModel(R.drawable.ic_whatsapp, "Whatsapp", "Sla", R.color.category_icon_green))
           categoryFileModels.add(CategoryFileModel(R.drawable.ic_download, "Others", "Sla", R.color.category_icon_red))

       recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
       val adapter = CategoryFileModelAdapter(categoryFileModels, requireContext())
       recyclerView.adapter = adapter

    }
    fun setRecentImages() {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recy_recents_images)
        val recentImage = fileUtils.getRecentImages(requireContext())
        val recentImageModel = ArrayList<RecentImageModel>()


        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        val adapter = RecentImagemodelAdapter(recentImage, requireContext())
        recyclerView.adapter = adapter
    }

    fun initClick() {
        val itemStorage = requireView().findViewById<ConstraintLayout>(R.id.c_internal_storage)
        val ivSettings = requireView().findViewById<ImageView>(R.id.iv_settings)
        val homeFragment = HomeFragment()
        val settingsFragment = SettingsFragment()
        itemStorage.setOnClickListener {
            (requireActivity() as MainActivity).starNewFragment(homeFragment)
        }

        ivSettings.setOnClickListener {
            (requireActivity() as MainActivity).starNewFragment(settingsFragment)
        }
    }

    @SuppressLint("SetTextI18n")
    fun setStorageSpaceInGB() {
        val tvSpaceUsed = requireView().findViewById<TextView>(R.id.tv_space_used)
        val tvSpaceFree = requireView().findViewById<TextView>(R.id.tv_space_free)
        val tvSpaceTotal = requireView().findViewById<TextView>(R.id.tv_space_total)
        val tvSpaceOf = requireView().findViewById<TextView>(R.id.tv_space_of)
        val cpSpace = requireView().findViewById<CircularProgressIndicator>(R.id.cp_space)
        val pbSpace = requireView().findViewById<LinearProgressIndicator>(R.id.pb_space)

        val totalSpace = fileUtils.getStorageSpaceInGB(SpaceType.TOTAL)
        val freeSpace = fileUtils.getStorageSpaceInGB(SpaceType.FREE)
        val usedSpace = fileUtils.getStorageSpaceInGB(SpaceType.USED)

        tvSpaceUsed.text = "$usedSpace GB"
        tvSpaceFree.text = "$freeSpace GB"
        tvSpaceTotal.text = "$totalSpace GB"
        tvSpaceOf.text = "$freeSpace GB of $totalSpace GB"

       /* cpSpace.progress = usedSpace
        pbSpace.progress = usedSpace*/
        val animation = ObjectAnimator.ofInt(cpSpace, "progress", 0, usedSpace)
        animation.duration = 1000
        animation.start()

        pbSpace.progress = 0 // Define o valor inicial do progresso

        val animationPb = ValueAnimator.ofInt(0, usedSpace)
        animation.duration = 1000 // Duração da animação em milissegundos

        animation.addUpdateListener { valueAnimator ->
            val progress = valueAnimator.animatedValue as Int
            pbSpace.progress = progress
        }

        animation.start()
    }






    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecentFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecentFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}