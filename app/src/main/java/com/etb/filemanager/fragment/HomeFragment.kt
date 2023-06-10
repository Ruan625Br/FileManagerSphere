package com.etb.filemanager.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.etb.filemanager.R
import com.etb.filemanager.activity.MainActivity
import com.etb.filemanager.interfaces.manager.FileAdapterListener
import com.etb.filemanager.interfaces.manager.FileAdapterListenerUtil
import com.etb.filemanager.interfaces.settings.PopupSettingsListener
import com.etb.filemanager.interfaces.settings.util.SelectPreferenceUtils
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.adapter.FileModelAdapter
import com.etb.filemanager.manager.adapter.ManagerUtil
import com.etb.filemanager.manager.bar.adapter.FolderBarModel
import com.etb.filemanager.manager.bar.adapter.FolderBarModelAdapter
import com.etb.filemanager.manager.selection.FileItemDetailsLookup
import com.etb.filemanager.manager.selection.FileItemKeyProvider
import com.etb.filemanager.manager.util.FileUtils
import com.etb.filemanager.manager.util.MaterialDialogUtils
import com.etb.filemanager.settings.preference.PopupSettings
import com.etb.filemanager.ui.view.anim.FabMenu
import com.etb.filemanager.util.file.FileUtil
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.nio.file.*
import java.util.*
import kotlin.streams.toList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment(), PopupSettingsListener, androidx.appcompat.view.ActionMode.Callback,
    FileAdapterListener {

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewFolderBar: RecyclerView
    private var folderBarModel = ArrayList<FolderBarModel>()
    private lateinit var adapter: FileModelAdapter
    private lateinit var barAdapter: FolderBarModelAdapter
    private var itemSelectedSort: Int = 0
    private lateinit var popupSettings: PopupSettings
    private lateinit var fileUtils: FileUtils
    private val fileUtil = FileUtil()


    private var mCurrentPath = "/storage/emulated/0"
    private val BASE_PATH = "/storage/emulated/0"
    private lateinit var materialDialogUtils: MaterialDialogUtils
    private var fileModel = mutableListOf<FileModel>()

    private val coroutineScope = lifecycleScope

    private lateinit var managerUtil: ManagerUtil


    private var actionMode: androidx.appcompat.view.ActionMode? = null
    private var isActionMode = false
    private val selectedItems = mutableListOf<FileModel>()
    private lateinit var topAppBar: MaterialToolbar

    private lateinit var selectPreferenceUtils: SelectPreferenceUtils
    private lateinit var fileAdapterListenerUtil: FileAdapterListenerUtil

    lateinit var selectionTracker: SelectionTracker<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        topAppBar = view.findViewById(R.id.topAppBar)
        initToolbar()

        recyclerView = view.findViewById(R.id.recyclerView)

        popupSettings = PopupSettings(requireContext())
        fileUtils = FileUtils()
        managerUtil = ManagerUtil()
        materialDialogUtils = MaterialDialogUtils()



        initFabClick()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStoragePermission()
        }


        selectPreferenceUtils = SelectPreferenceUtils.getInstance()
        fileAdapterListenerUtil = FileAdapterListenerUtil.getInstance()
        fileAdapterListenerUtil.setListener(this, requireContext())
        selectPreferenceUtils.setListener(this, requireContext())

        listFilesAndFoldersInBackground(BASE_PATH)


    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onItemSelectedActionSort(itemSelected: Int, itemSelectedFolderFirst: Boolean) {
        itemSelectedSort = itemSelected
        if (::adapter.isInitialized) {
            selectPreferenceUtils.sortFilesAuto(fileModel, requireContext())
            refreshAdapter()
        }
    }

    override fun onItemClick(item: FileModel, path: String, isDirectory: Boolean) {
        if (isActionMode) {

        } else {
            if (isDirectory) {
                mCurrentPath = path
                coroutineScope.launch {
                    managerUtil.addToPathStack(path)
                    listFilesAndFoldersInBackground(path)

                }
                //monitorDirectory(path)
                /*    folderBarModel.add(FolderBarModel(path))
                    initFolderBar(folderBarModel)*/
                Log.e("HOMEE CURRENTPATH", "PATH $path")

            }
        }

    }

    override fun onLongClickListener(item: FileModel, isActionMode: Boolean) {
        this.isActionMode = isActionMode
        if (isActionMode) {
            startActionMode()
            onItemClicked(item)
        }

    }

    override fun onFileInfoReceived(currentPath: String) {
        // mCurrentPath = currentPath
        Log.e("HOMEE CURRENTPATH", "PATH $currentPath")

    }

    private fun initToolbar() {

        (requireActivity() as AppCompatActivity).setSupportActionBar(topAppBar)
    }


    @OptIn(DelicateCoroutinesApi::class)
    fun listFilesAndFoldersInBackground(mPath: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val rootPath: Path = Paths.get(mPath)
            try {
                val fileEntries = Files.list(rootPath).use { it.toList() }
                launch(Dispatchers.Main) {

                    updateData(fileEntries)

                }
            } catch (e: Exception) {
                Log.e("ERRO AO LISTAR OS ARQUIVOS", "ERRO: $e")
            }
        }

    }

    fun updateData(fileEntries: List<Path>) {


        fileModel.clear()
        val fileUtil = FileUtil()

        for (path in fileEntries) {
            val fileName = path.fileName.toString()
            val filePath = path.toAbsolutePath().toString()
            val isDirectory = Files.isDirectory(path)
            val fileExtension = fileUtil.getFileExtension(path)
            val fileLength = fileUtil.getFileSize(path)
            val file = path.toFile()

            fileModel.add(
                FileModel(
                    UUID.randomUUID().mostSignificantBits,
                    fileName,
                    filePath,
                    isDirectory,
                    fileExtension,
                    fileLength,
                    file
                )
            )
        }
        selectPreferenceUtils.sortFilesAuto(fileModel, requireContext())

        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        adapter = FileModelAdapter(fileModel, requireContext())
        recyclerView.adapter = adapter
        setRecyclerViewAnimation()
    }

    private fun initFabClick() {
        val mFab: FloatingActionButton = requireView().findViewById(R.id.mfab)
        val mFabCreateFile: FloatingActionButton = requireView().findViewById(R.id.fab_create_file)
        val mFabCreateFolder: FloatingActionButton = requireView().findViewById(R.id.fab_create_folder)
        val fabMenu: FabMenu = FabMenu(requireContext(), mFab, mFabCreateFile, mFabCreateFolder)

        mFab.setOnClickListener { fabMenu.toggle() }

        mFabCreateFile.setOnClickListener {
            val title = requireContext().getString(R.string.dialog_new_file)
            val text = requireContext().getString(R.string.dialog_name_file)
            val textPositiveButton = requireContext().getString(R.string.fab_menu_action_create_file)

            materialDialogUtils.createBasicMaterial(title, text, textPositiveButton, requireContext()) { dialogResult ->
                val isConfirmed = dialogResult.confirmed
                val enteredText = dialogResult.text
                if (isConfirmed) {
                    if (fileUtils.createFileAndFolder(mCurrentPath, enteredText, FileUtils.CreationOption.FILE)) {
                        addNewItemAdapter("$mCurrentPath/$enteredText")
                        Toast.makeText(requireContext(), "Criado $enteredText com sucesso", Toast.LENGTH_LONG).show()

                    }
                }
            }
        }

        mFabCreateFolder.setOnClickListener {
            fabMenu.toggle()
            val title = requireContext().getString(R.string.dialog_new_folder)
            val text = requireContext().getString(R.string.dialog_name_folder)
            val textPositiveButton = requireContext().getString(R.string.fab_menu_action_create_folder)

            materialDialogUtils.createBasicMaterial(title, text, textPositiveButton, requireContext()) { dialogResult ->
                val isConfirmed = dialogResult.confirmed
                val enteredText = dialogResult.text
                if (isConfirmed) {
                    if (fileUtil.createFolder(mCurrentPath, enteredText)) {
                        Toast.makeText(requireContext(), "Criado pasta $enteredText com sucesso", Toast.LENGTH_LONG)
                            .show()
                        addNewItemAdapter("$mCurrentPath/$enteredText")

                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestStoragePermission() {
        val permission = Manifest.permission.MANAGE_EXTERNAL_STORAGE
        val requestCode = 1

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                // A permissão já foi concedida
            } else {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf<String>(
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ), 1
            )
        }


    }


    @SuppressLint("NotifyDataSetChanged")
    fun refreshAdapter() {
        val controller = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_file_fade_in_anim)
        recyclerView.layoutAnimation = controller
        adapter.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()

    }

    private fun setRecyclerViewAnimation() {

        val controller = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_file_fade_in_anim)
        recyclerView.layoutAnimation = controller
        recyclerView.scheduleLayoutAnimation()
    }

    fun monitorDirectory(path: String) {
        val diretoryPath = Paths.get(path)

        try {
            val watchService = FileSystems.getDefault().newWatchService()
            diretoryPath.register(
                watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE
            )

            while (true) {
                val key = watchService.take()
                for (event in key.pollEvents()) {
                    when (event.kind()) {
                        StandardWatchEventKinds.ENTRY_CREATE -> {
                            val file = event.context() as Path
                            val fileName = file.toString()
                            addNewItemAdapter("$path/$fileName")
                        }

                        StandardWatchEventKinds.ENTRY_DELETE -> {
                            val fileName = event.context() as Path
                            adapter.removeFile(fileName.toString())
                        }
                    }
                }
                key.reset()
            }
        } catch (e: Exception) {
            Log.e("MONITOR", "ERRO $e")
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewItemAdapter(path: String) {
        val itemFile: Path = Paths.get(path)

        val fileName = itemFile.fileName.toString()
        val filePath = itemFile.toAbsolutePath().toString()
        val isDirectory = Files.isDirectory(itemFile)
        val fileExtension = fileUtil.getFileExtension(itemFile)
        val fileLength = fileUtil.getFileSize(itemFile)
        val file = itemFile.toFile()

        val newItem = FileModel(
            UUID.randomUUID().mostSignificantBits,
            fileName,
            filePath,
            isDirectory,
            fileExtension,
            fileLength,
            file
        )
        fileModel.add(newItem)
        refreshAdapter()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                val mPreviousPath = managerUtil.getPreviousPath()
                if (mPreviousPath.equals(BASE_PATH)) {
                    val recentFragment = RecentFragment()
                    (requireActivity() as MainActivity).starNewFragment(recentFragment)

                } else {
                    listFilesAndFoldersInBackground(managerUtil.getPreviousPath())

                }

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this, callback
        )
    }


    private fun updateActionModeTitle(selectedCount: Int) {

        val title = "$selectedCount selecionado(s)"
        actionMode?.title = title

    }

    private fun onItemClicked(item: FileModel) {
        if (isActionMode) {


            /*  if (selectedItems.contains(item)) {
                  selectedItems.remove(item)
                 // adapter.removeSelectedItem(item)
                 // updateActionModeTitle(selectedCount - 1)
              } else {
                  selectedItems.add(item)
                  adapter.setSelectedItem(item)
                  //updateSelectedItems(item)
                //  updateActionModeTitle(selectedCount + 1)
              }*/
            adapter.setSelectedItem(item)
            val selectedCount = adapter.getSizeItemSelected()


            updateActionModeTitle(selectedCount)
            Log.i("HOMEFRAGMENT", "Size $selectedCount")


        } else {
            // Handle non-action mode behavior
        }
    }

    private fun setupSelectionTracker(recyclerView: RecyclerView) {



    }


    private fun updateSelectedItems(item: FileModel) {
        selectedItems.add(item)

    }

    private fun initSelectionTracker(){

        selectionTracker = SelectionTracker.Builder<Long>(
            "selection-files",
            recyclerView,
            FileItemKeyProvider(fileModel),
            FileItemDetailsLookup(recyclerView),
            StorageStrategy.createLongStorage()
        ).build()



        (recyclerView.adapter as FileModelAdapter).selectionTracker = selectionTracker

        startActionMode()
        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>(){
            override fun onItemStateChanged(key: Long, selected: Boolean) {
                super.onItemStateChanged(key, selected)
                val selectedItemCount = selectionTracker.selection.size() ?: 0



                updateActionModeTitle(selectedItemCount)
            }
        })
    }

    private fun startActionMode() {
        isActionMode = true
        actionMode?.finish()
        actionMode = (activity as AppCompatActivity?)!!.startSupportActionMode(this)
        updateActionModeTitle(0)
    }

    private fun finishActionMode() {
        actionMode?.finish()
        actionMode = null
      selectionTracker.clearSelection()
        isActionMode = false
    }

    private fun clearItemsSelecteds() {
        selectedItems.clear()
        adapter.clearAllItemSelectedItem()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_file_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        val itemSortByName = menu.findItem(R.id.action_sort_by_name)
        val itemSortByType = menu.findItem(R.id.action_sort_by_type)
        val itemSortBySize = menu.findItem(R.id.action_sort_by_size)
        val itemSortByLastModified = menu.findItem(R.id.action_sort_by_last_modified)
        val itemSortDirectoriesFirst = menu.findItem(R.id.action_sort_directories_first)
        val itemShowHiddenFiles = menu.findItem(R.id.action_show_hidden_files)

        itemSortDirectoriesFirst.isChecked = popupSettings.getActionSortFolderFirst()
        itemShowHiddenFiles.isChecked = popupSettings.getActionShowHiddenFiles()

        val menuItems: MutableList<MenuItem> = ArrayList()
        itemSortByName?.let { menuItems.add(it) }
        itemSortByType?.let { menuItems.add(it) }
        itemSortBySize?.let { menuItems.add(it) }
        menuItems.add(itemSortByLastModified)

        for (position in menuItems.indices) {
            val menuItem = menuItems[position]
            val test = popupSettings.itemIsSelectedActionSort(position)
            if (test) {
                menuItem.isChecked = true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_by_name -> {
                popupSettings.setItemSelectedActionSort(0)
            }

            R.id.action_sort_by_type -> {
                popupSettings.setItemSelectedActionSort(1)
            }

            R.id.action_sort_by_size -> {
                popupSettings.setItemSelectedActionSort(2)
            }

            R.id.action_sort_by_last_modified -> {
                popupSettings.setItemSelectedActionSort(3)
            }

            R.id.action_sort_order_ascending -> {}
            R.id.action_sort_directories_first -> {
                popupSettings.setSelectedActionSortFolderFirst()
            }

            R.id.action_sort_path_specific -> {}
            R.id.action_refresh -> {}
            R.id.action_select_all -> {}
            R.id.action_navigate_to -> {
                initSelectionTracker()
            }
            R.id.action_show_hidden_files -> {
                popupSettings.setSelectedActionShowHiddenFiles()
            }

            R.id.action_share -> {}
            R.id.action_copy_path -> {}
            else -> return super.onOptionsItemSelected(item)
        }
        activity?.invalidateOptionsMenu()
        return true
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = HomeFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, param1)
                putString(ARG_PARAM2, param2)
            }
        }


    }

    override fun onCreateActionMode(mode: androidx.appcompat.view.ActionMode?, menu: Menu?): Boolean {

        val inflater = mode?.menuInflater
        inflater?.inflate(R.menu.menu_file_lis_select, menu)
        return true
    }

    override fun onPrepareActionMode(mode: androidx.appcompat.view.ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: androidx.appcompat.view.ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_cut -> {}
            R.id.action_copy -> {}
            R.id.action_delete -> {}
            R.id.action_archive -> {}
            R.id.action_share -> {}
            R.id.action_select_all -> {}
            else -> return super.onOptionsItemSelected(item!!)
        }
        return true
    }

    override fun onDestroyActionMode(mode: androidx.appcompat.view.ActionMode?) {
        finishActionMode()
    }

}