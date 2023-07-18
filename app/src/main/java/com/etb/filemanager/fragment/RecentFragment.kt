package com.etb.filemanager.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etb.filemanager.R
import com.etb.filemanager.activity.MainActivity
import com.etb.filemanager.files.util.fileProviderUri
import com.etb.filemanager.files.util.getColorByAttr
import com.etb.filemanager.interfaces.manager.ItemListener
import com.etb.filemanager.manager.category.adapter.CategoryFileModel
import com.etb.filemanager.manager.category.adapter.CategoryFileModelAdapter
import com.etb.filemanager.manager.category.adapter.RecentImagemodelAdapter
import com.etb.filemanager.manager.util.FileUtils
import com.etb.filemanager.manager.util.FileUtils.SpaceType
import com.etb.filemanager.manager.util.MaterialDialogUtils
import com.etb.filemanager.settings.preference.Preferences
import com.etb.filemanager.ui.view.ModalBottomSheetAddCategory
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.*
import java.nio.file.Path

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecentFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecentFragment : Fragment(), ItemListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private lateinit var fileUtils: FileUtils

    private var roundedCornersDrawable: GradientDrawable? = null
    private lateinit var cRecentImg: ConstraintLayout
    private lateinit var cInternalStorage: ConstraintLayout
    private lateinit var cCategoryFileItem: ConstraintLayout
    private lateinit var cBaseItem: ConstraintLayout
    private lateinit var btnAddCategory: Button
    private lateinit var adapter: CategoryFileModelAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_recent, container, false)
    }

    override fun onResume() {
        super.onResume()

        roundedCornersDrawable = null
        //initStyleView()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        cBaseItem = view.findViewById(R.id.cBaseItems)
        cInternalStorage = view.findViewById(R.id.cInternalStorage)
        cCategoryFileItem = view.findViewById(R.id.cCategoryItem)
        cRecentImg = view.findViewById(R.id.cRecentImage)
        btnAddCategory = view.findViewById(R.id.btnAddCategory)

        //requestStoragePermission()
        // initStyleView()

        fileUtils = FileUtils()
        setStorageSpaceInGB()
        initCategoryItem()
        initClick()
        setRecentImages()

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    }

    @SuppressLint("SuspiciousIndentation")
    fun initCategoryItem() {
        val listCategoryName = Preferences.Behavior.categoryNameList
        val listCategoryPath = Preferences.Behavior.categoryPathList
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recyclerView)
        val dcimPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath
        val moviesPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).absolutePath
        val documentsPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
        val musicPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).absolutePath
        val downloadsPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val whatsappPath = getString(R.string.path_whatsapp)


        val categoryFileModels = ArrayList<CategoryFileModel>()
        categoryFileModels.add(
            CategoryFileModel(
                R.drawable.ic_image,
                "Images",
                dcimPath
            )
        )
        categoryFileModels.add(
            CategoryFileModel(
                R.drawable.ic_video,
                "Video",
                moviesPath
            )
        )
        categoryFileModels.add(
            CategoryFileModel(
                R.drawable.ic_document, "Document", documentsPath
            )
        )
        categoryFileModels.add(
            CategoryFileModel(
                R.drawable.ic_music,
                "Music",
                musicPath
            )
        )

        categoryFileModels.add(
            CategoryFileModel(
                R.drawable.ic_download, "Download", downloadsPath
            )
        )
        categoryFileModels.add(
            CategoryFileModel(
                R.drawable.ic_whatsapp, "Whatsapp", whatsappPath
            )
        )
        if (!listCategoryName.isNullOrEmpty()) {
            for ((index, name) in listCategoryName.withIndex()) {
                val mName = name
                val mPath = listCategoryPath[index]
                categoryFileModels.add(CategoryFileModel(R.drawable.ic_folder, mName, mPath))
            }
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
         adapter = CategoryFileModelAdapter(this, categoryFileModels, requireContext())
        recyclerView.adapter = adapter

    }

    private fun showBottomSheetAddCategory() {
        val modalBottomSheetAddCategory = ModalBottomSheetAddCategory()


        modalBottomSheetAddCategory.show(parentFragmentManager, ModalBottomSheetAddCategory.TAG)
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun setRecentImages() {
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.recy_recents_images)

        val mainScope = CoroutineScope(Dispatchers.Main)
        mainScope.launch {
            val recentImage = withContext(Dispatchers.IO) {
                fileUtils.getRecentImages(requireContext())
            }

            recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
            val adapter = RecentImagemodelAdapter(recentImage, requireContext())
            recyclerView.adapter = adapter
        }
    }

    fun initClick() {
        val itemStorage = requireView().findViewById<ConstraintLayout>(R.id.cInternalStorage)
        val ivSettings = requireView().findViewById<ImageView>(R.id.iv_settings)
        val settingsFragment = SettingsFragment()
        itemStorage.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                requestPermissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            }
        }

        ivSettings.setOnClickListener {
            (requireActivity() as MainActivity).startNewFragment(settingsFragment)
        }
        btnAddCategory.setOnClickListener { showBottomSheetAddCategory() }

    }

    private fun openListFiles() {
        val homeFragment = HomeFragment()
        (requireActivity() as MainActivity).startNewFragment(homeFragment)
    }

    private fun initStyleView() {
        if (Preferences.Interface.isEnabledRoundedCorners) {
            if (roundedCornersDrawable == null) {
                val mCornerRadius =
                    requireContext().resources.getDimensionPixelSize(R.dimen.corner_radius_base)
                        .toFloat()
                val colorPrimary = getColorByAttr(com.google.android.material.R.attr.colorPrimary)
                roundedCornersDrawable = GradientDrawable().apply {
                    cornerRadius = mCornerRadius
                    setColor(colorPrimary)
                }
            }

            cBaseItem.background = roundedCornersDrawable
            cInternalStorage.background = roundedCornersDrawable
            cCategoryFileItem.background = roundedCornersDrawable
            cRecentImg.background = roundedCornersDrawable
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

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestManageExternalPermission()
        } else {
            requestReadWritePermission()
        }

    }

    private fun requestReadWritePermission() {
        val readWritePermission = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val READ_WRITE_PERMISSION_REQUEST_CODE = 1

        if (arePermissionsGranted(readWritePermission)) {
            openListFiles()
            setRecentImages()
        } else {
            val title = getString(R.string.permission_required)
            val message = getString(R.string.permission_required_body)
            val textPositiveButton = getString(R.string.allow)
            val textNegativeButton = getString(R.string.dialog_cancel)

            MaterialDialogUtils().createDialogInfo(
                title, message, textPositiveButton, textNegativeButton, requireContext(), true
            ) { dialogResult ->
                val isConfirmed = dialogResult.confirmed
                if (isConfirmed) {
                    ActivityCompat.requestPermissions(
                        requireActivity(), readWritePermission, READ_WRITE_PERMISSION_REQUEST_CODE
                    )
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)


                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestManageExternalPermission() {
        if (Environment.isExternalStorageManager()) {
            openListFiles()
            setRecentImages()
        } else {
            val title = getString(R.string.permission_required)
            val message = getString(R.string.permission_required_body)
            val textPositiveButton = getString(R.string.allow)
            val textNegativeButton = getString(R.string.dialog_cancel)

            MaterialDialogUtils().createDialogInfo(
                title, message, textPositiveButton, textNegativeButton, requireContext(), true
            ) { dialogResult ->
                val isConfirmed = dialogResult.confirmed
                if (isConfirmed) {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                    requestPermissionLauncher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                }
            }

        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(), permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    private val requestStoragePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            val isGranted = it.value
            if (isGranted) {
                openListFiles()
                setRecentImages()
            } else {
                requestStoragePermission()
            }
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openListFiles()
        } else {
            requestStoragePermission()
        }
    }

    override fun openFileCategory(path: Path) {
        val uri = path.fileProviderUri
        val homeFragment = HomeFragment.newInstance(uri)
        (requireActivity() as MainActivity).startNewFragment(homeFragment)

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
        fun newInstance(param1: String, param2: String) = RecentFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }
    }
}