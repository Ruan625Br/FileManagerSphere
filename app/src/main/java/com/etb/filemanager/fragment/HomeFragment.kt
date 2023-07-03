package com.etb.filemanager.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.etb.filemanager.R
import com.etb.filemanager.activity.MainActivity
import com.etb.filemanager.files.file.common.mime.MimeTypeIcon
import com.etb.filemanager.files.file.common.mime.MimeTypeUtil
import com.etb.filemanager.files.file.properties.*
import com.etb.filemanager.files.util.*
import com.etb.filemanager.interfaces.manager.FileAdapterListenerUtil
import com.etb.filemanager.interfaces.manager.FileListener
import com.etb.filemanager.interfaces.settings.PopupSettingsListener
import com.etb.filemanager.interfaces.settings.util.SelectPreferenceUtils
import com.etb.filemanager.manager.adapter.FileModel
import com.etb.filemanager.manager.adapter.FileModelAdapter
import com.etb.filemanager.manager.adapter.ManagerUtil
import com.etb.filemanager.manager.editor.CodeEditorFragment
import com.etb.filemanager.manager.file.CreateFileAction
import com.etb.filemanager.manager.file.FileAction
import com.etb.filemanager.manager.file.FileOptionAdapter
import com.etb.filemanager.manager.files.filecoroutine.FileCoroutineViewModel
import com.etb.filemanager.manager.files.filelist.*
import com.etb.filemanager.manager.util.FileUtils
import com.etb.filemanager.manager.util.MaterialDialogUtils
import com.etb.filemanager.settings.preference.PopupSettings
import com.etb.filemanager.settings.preference.Preferences
import com.etb.filemanager.ui.view.FabMenu
import com.etb.filemanager.util.file.FileUtil
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.*
import java.io.File
import java.nio.file.*
import java.util.*
import kotlin.streams.toList


private const val ARG_FILE_URI = "fileUri"

class HomeFragment : Fragment(), PopupSettingsListener, androidx.appcompat.view.ActionMode.Callback, FileListener {

    private var fileUri: Uri? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FileModelAdapter
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
    private lateinit var topAppBar: MaterialToolbar

    private lateinit var selectPreferenceUtils: SelectPreferenceUtils
    private lateinit var fileAdapterListenerUtil: FileAdapterListenerUtil

    lateinit var selectionTracker: SelectionTracker<Long>

    private lateinit var standardBottomSheet: FrameLayout
    private lateinit var standardBottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var standardBottomSheetOp: FrameLayout
    private lateinit var standardBehaviorOperation: BottomSheetBehavior<FrameLayout>
    private lateinit var bottomSheetProperties: FrameLayout
    private lateinit var bottomSheetBehaviorProperties: BottomSheetBehavior<FrameLayout>
    private lateinit var bottomSheetRename: FrameLayout
    private lateinit var bottomSheetBehaviorRename: BottomSheetBehavior<FrameLayout>

    private lateinit var settingsViewModel: SettingsViewModel
    private var showHiddenFiles = false

    private lateinit var viewModel: FileListViewModel
    private lateinit var coroutineViewModel: FileCoroutineViewModel

    val propertiesViewModel = PropertiesViewModel()

    private val REQUEST_CODE = 6

    private var progressDialog: AlertDialog? = null
    private var isProgressDialogShowing = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            fileUri = it.getParcelable(ARG_FILE_URI)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStoragePermission()
        } else {
            requestFilesPermission()
        }
        initAllBottomSheet()


        settingsViewModel = SettingsViewModel(requireContext())
        viewModel = ViewModelProvider(this)[FileListViewModel::class.java]
        coroutineViewModel = ViewModelProvider(this)[FileCoroutineViewModel::class.java]


        showHiddenFiles = settingsViewModel.getActionShowHiddenFiles()
        topAppBar = view.findViewById(R.id.topAppBar)
        initToolbar()

        recyclerView = view.findViewById(R.id.recyclerView)

        popupSettings = PopupSettings(requireContext())
        fileUtils = FileUtils()
        managerUtil = ManagerUtil.getInstance()
        materialDialogUtils = MaterialDialogUtils()


        observeSettings()
        observeViewModel()
        observeOperationViewModel()

        initFabClick()




        selectPreferenceUtils = SelectPreferenceUtils.getInstance()
        fileAdapterListenerUtil = FileAdapterListenerUtil.getInstance()
        selectPreferenceUtils.setListener(this, requireContext())

        val mFile = fileUri?.path?.let { File(it) }
        val path = fileUri?.let { fileUtil.getFilePathFromUri(requireContext(), it) }
        if (mFile != null) {
            // listFilesAndFoldersInBackground(if (mFile.isDirectory) path.toString() else BASE_PATH)
        } else {
            // listFilesAndFoldersInBackground(BASE_PATH)

        }
        recyclerView.layoutManager = GridLayoutManager(requireActivity(), 1)
        adapter = FileModelAdapter(requireContext(), this)
        recyclerView.adapter = adapter


    }

    private fun observeViewModel() {
        viewModel.currentPathLiveData.observe(viewLifecycleOwner) { onCurrentPathChanged(it) }
        viewModel.selectedFilesLiveData.observe(viewLifecycleOwner) { onSelectedFilesChanged(it) }
        viewModel.fileListLiveData.observe(viewLifecycleOwner) { onFileListChanged(it) }
        viewModel.resetTo(Paths.get(Preferences.Behavior.defaultFolder))
        viewModel.showHiddenFilesLiveData.observe(viewLifecycleOwner){onShowHiddenFilesChanged()}
        viewModel.sortOptionsLiveData.observe(viewLifecycleOwner) { onSortOptionsChanged() }


    }


    private fun observeOperationViewModel() {

        coroutineViewModel.operationInfo.observe(viewLifecycleOwner) { info ->
            if (info.progress != null) {
                updateProgress(info.title, info.message, info.message.toInt())
            } else {
                progressDialog?.cancel()
            }
        }

    }

    fun onNewIntent(uri: Uri) {
        navigateTo(fileUtil.getFilePathFromUri(requireContext(), uri).toString())

    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onItemSelectedActionSort(itemSelected: Int, itemSelectedFolderFirst: Boolean) {
        itemSelectedSort = itemSelected
        if (::adapter.isInitialized) {
            selectPreferenceUtils.sortFilesAuto(fileModel, requireContext())
            refreshAdapter()
        }
    }


    override fun onFileInfoReceived(currentPath: String) {
        // mCurrentPath = currentPath
        Log.e("HOMEE CURRENTPATH", "PATH $currentPath")

    }

    private fun initToolbar() {

        (requireActivity() as AppCompatActivity).setSupportActionBar(topAppBar)
    }


    @SuppressLint("NewApi")
    @OptIn(DelicateCoroutinesApi::class)
    fun listFilesAndFoldersInBackground(mPath: String) {

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val fileEntries = Files.list(Paths.get(mPath)).use { it.toList() }
                mCurrentPath = mPath
                launch(Dispatchers.Main) {
                }
            } catch (e: Exception) {
                if (e is AccessDeniedException) {
                    try {
                        launch(Dispatchers.Main) {
                            createDialgRestriction()

                        }
                    } catch (ec: Exception) {
                        Log.e("Dialog", "ERRO: $e")

                    }

                }
                Log.e("ERRO AO LISTAR OS ARQUIVOS", "ERRO: $e")

            }
            //como printar


        }

    }

    private fun createDialgRestriction() {
        val title = requireContext().getString(R.string.restriction_folder)
        val text = requireContext().getString(R.string.e_restriction_folder)
        val textPositiveButton = requireContext().getString(R.string.dialog_ok)

        materialDialogUtils.createDialogInfo(
            title, text, textPositiveButton, "", requireContext(), false
        ) { dialogResult ->
            val isConfirmed = dialogResult.confirmed
            if (isConfirmed) {
                //managerUtil.getPreviousPath()

            }
        }
    }

  fun onShowHiddenFilesChanged(){
      updateAdapterFileList()
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
        } else {

        }


    }

    private fun requestFilesPermission() {
        val READ_WRITE_PERMISSION_REQUEST_CODE = 1

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Se as permissões não estão concedidas, solicite-as
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                READ_WRITE_PERMISSION_REQUEST_CODE
            )
        } else {
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    fun refreshAdapter() {
        val controller = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_file_fade_in_anim)
        //    recyclerView.layoutAnimation = controller
        adapter.notifyDataSetChanged()
        // recyclerView.scheduleLayoutAnimation()

    }

    private fun setRecyclerViewAnimation() {

        val controller = AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_file_fade_in_anim)
        recyclerView.layoutAnimation = controller
        recyclerView.scheduleLayoutAnimation()
    }

    private fun onFileListChanged(stateful: Stateful<List<FileModel>>) {
        val files = if (stateful is Failure) null else stateful.value
        when {
            stateful is Failure -> topAppBar.subtitle = getString(R.string.error)
            stateful is Loading -> topAppBar.subtitle = getString(R.string.loading)
            else -> topAppBar.subtitle = getSubtitle(files!!)
        }
        val hasFiles = !files.isNullOrEmpty()
        val throwable = (stateful as? Failure)?.throwable

        if (throwable != null) {
            val error = throwable.toString()
            if (hasFiles) {
                createDialgRestriction()

            } else {
                createDialgRestriction()

            }
        }
        if (files != null) {
            updateAdapterFileList()
        } else {
            adapter.clear()
        }
        if (stateful is Success) {
            viewModel.pendingState
                ?.let { recyclerView.layoutManager!!.onRestoreInstanceState(it) }
        }

    }

    private fun onCurrentPathChanged(path: Path) {
        updateActionMode()
    }


    private fun updateAdapterFileList() {
        var files = viewModel.fileListStateful.value ?: return
        if (!Preferences.Popup.showHiddenFiles){
            files = files.filterNot { it.isHidden }
        }
        adapter.replaceList(files)
    }

    private fun updateActionMode() {
        startActionMode()
        val files = viewModel.selectedFiles
        if (files.isEmpty()) {
            finishActionMode()
            return
        }
        actionMode?.title = getQuantityString(R.plurals.file_list_selected_count_format, files.size, files.size)

    }

    private fun getSubtitle(files: List<FileModel>): String {
        val directoryCount = files.count { it.isDirectory }
        val fileCount = files.size - directoryCount
        val directoryCountText = if (directoryCount > 0) {
            getQuantityString(
                R.plurals.file_list_subtitle_directory_count_format, directoryCount, directoryCount
            )
        } else {
            null
        }
        val fileCountText = if (fileCount > 0) {
            getQuantityString(
                R.plurals.file_list_subtitle_file_count_format, fileCount, fileCount
            )
        } else {
            null
        }
        return when {
            !directoryCountText.isNullOrEmpty() && !fileCountText.isNullOrEmpty() ->
                (directoryCountText + getString(R.string.file_list_subtitle_separator)
                        + fileCountText)

            !directoryCountText.isNullOrEmpty() -> directoryCountText
            !fileCountText.isNullOrEmpty() -> fileCountText
            else -> getString(R.string.empty)
        }
    }


    private fun startActionMode() {
        if (isActionMode || !::adapter.isInitialized) {
            return
        }

        isActionMode = true
        actionMode = (activity as AppCompatActivity?)!!.startSupportActionMode(this)
    }

    fun onSortOptionsChanged() {
        val fileSortOptions = FileSortOptions(
            Preferences.Popup.sortBy,
            Preferences.Popup.orderFiles,
            Preferences.Popup.isDirectoriesFirst
        )
        adapter.comparator = fileSortOptions.createComparator()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun finishActionMode() {

        clearSelectedFiles()
        if (::adapter.isInitialized) adapter.clearItemSelection()
        actionMode?.finish()
        isActionMode = false


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
        val itemSortOrderAscending = menu.findItem(R.id.action_sort_order_ascending)
        val itemSortDirectoriesFirst = menu.findItem(R.id.action_sort_directories_first)
        val itemShowHiddenFiles = menu.findItem(R.id.action_show_hidden_files)

        itemSortOrderAscending.isChecked = (Preferences.Popup.orderFiles == FileSortOptions.Order.ASCENDING)
        itemSortDirectoriesFirst.isChecked = Preferences.Popup.isDirectoriesFirst
        itemShowHiddenFiles.isChecked = Preferences.Popup.showHiddenFiles

        val menuItems: MutableList<MenuItem> = ArrayList()
        itemSortByName?.let { menuItems.add(it) }
        itemSortByType?.let { menuItems.add(it) }
        itemSortBySize?.let { menuItems.add(it) }
        menuItems.add(itemSortByLastModified)

        val checkedSortByItem = when (Preferences.Popup.sortBy) {
            FileSortOptions.SortBy.NAME -> itemSortByName
            FileSortOptions.SortBy.TYPE -> itemSortByType
            FileSortOptions.SortBy.SIZE -> itemSortBySize
            FileSortOptions.SortBy.LAST_MODIFIED -> itemSortByLastModified
        }
        checkedSortByItem.isChecked = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_by_name -> {
                viewModel.setSortBy(FileSortOptions.SortBy.NAME)
            true
            }

            R.id.action_sort_by_type -> {
                viewModel.setSortBy(FileSortOptions.SortBy.TYPE)
                true
            }

            R.id.action_sort_by_size -> {
               viewModel.setSortBy(FileSortOptions.SortBy.SIZE)
            }

            R.id.action_sort_by_last_modified -> {
                    viewModel.setSortBy(FileSortOptions.SortBy.LAST_MODIFIED)
            }

            R.id.action_sort_order_ascending -> {
                viewModel.setOrderFiles()
            }
            R.id.action_sort_directories_first -> {
                viewModel.setDirectoriesFirst()
            }

            R.id.action_sort_path_specific -> {}
            R.id.action_refresh -> {
                refresh()
            }

            R.id.action_select_all -> {
                adapter.selectAllFiles()
            }

            R.id.action_navigate_to -> {
                createDialogNavigateTo()
            }

            R.id.action_show_hidden_files -> {
                viewModel.setShowHiddenFiles(!Preferences.Popup.showHiddenFiles)
            }

            R.id.action_share -> {}
            R.id.action_copy_path -> {
                fileUtil.copyTextToClipboard(requireContext(), viewModel.currentPathLiveData.value.toString(), true)
            }

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
         * @param fileUri File uri.
         * @return A new instance of fragment HomeFragment.
         */
        @JvmStatic
        fun newInstance(fileUri: Uri) = HomeFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_FILE_URI, fileUri)
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestAllFilesPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.addCategory("android.intent.category.DEFAULT")
        intent.data = Uri.fromParts("package", requireContext().packageName, null)
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                data.data?.let { treeUri ->
                    requireContext().contentResolver.takePersistableUriPermission(
                        treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                    val path = getFolderPathFromUri(treeUri)
                    if (path != null) {
                        navigateTo(path)
                    } else {
                        // Tratar o caso em que não foi possível obter o caminho da URI da árvore
                    }
                }
            }
        }


    }


    private fun getFolderPathFromUri(uri: Uri): String? {
        val docUri = DocumentsContract.buildDocumentUriUsingTree(uri, DocumentsContract.getTreeDocumentId(uri))
        val resolver = requireContext().contentResolver

        resolver.query(docUri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val pathIndex = cursor.getColumnIndex(DocumentsContract.Root.COLUMN_DOCUMENT_ID)
                val fullPath = cursor.getString(pathIndex)
                return fullPath?.removePrefix("tree:")
            }
        }

        return null
    }


    private fun listFiles(folder: DocumentFile): List<Uri> {
        return if (folder.isDirectory) {
            folder.listFiles().mapNotNull { file ->
                if (file.name != null) file.uri else null
            }
        } else {
            emptyList()
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
            R.id.action_cut -> {
                cutFile(fileModel.get(0))
                finishActionMode()
            }

            R.id.action_copy -> {
                finishActionMode()
            }

            R.id.action_delete -> {
                // confirmDeleteFile()
                finishActionMode()
            }

            R.id.action_archive -> {
                finishActionMode()
            }

            R.id.action_share -> {
                finishActionMode()
            }

            R.id.action_rename -> {
                val selectedFile = viewModel.selectedFiles.toMutableList()
                if (viewModel.selectedFiles.size == 1) showRenameFileDialog(selectedFile[0]) else showBottomSheetRenameMultipleFiles()
                finishActionMode()
            }

            R.id.action_select_all -> {
                adapter.selectAllFiles()
            }

            else -> return super.onOptionsItemSelected(item!!)
        }
        return true
    }

    override fun onDestroyActionMode(mode: androidx.appcompat.view.ActionMode?) {
        finishActionMode()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!viewModel.navigateUp()) {
                    val recentFragment = RecentFragment()
                    (requireActivity() as MainActivity).startNewFragment(recentFragment)

                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            this, callback
        )
    }

    fun refresh() {
        viewModel.reload()
    }

    fun navigateTo(path: String) {
        //  managerUtil.addToPathStack(path)
        val state = recyclerView.layoutManager!!.onSaveInstanceState()
        viewModel.navigateTo(state!!, Paths.get(path))

    }


    fun createDialogNavigateTo() {
        val title = requireContext().getString(R.string.file_list_action_navigate_to)
        val text = mCurrentPath
        val textPositiveButton = requireContext().getString(R.string.dialog_ok)

        materialDialogUtils.createBasicMaterial(title, text, textPositiveButton, requireContext()) { dialogResult ->
            val isConfirmed = dialogResult.confirmed
            val enteredText = dialogResult.text
            if (isConfirmed && enteredText != mCurrentPath) {
                navigateTo(enteredText)
            }
        }

    }

    private fun onSelectedFilesChanged(files: FileItemSet) {
        updateActionMode()
        if (::adapter.isInitialized) adapter.replaceSelectedFiles(files)
    }


    private fun getFileItemByKey(key: Long): FileModel? {
        for (item in fileModel) {
            if (item.id == key) {
                return item
            }
        }
        return null
    }


    override fun clearSelectedFiles() {
        viewModel.clearSelectedFiles()
    }

    override fun selectFile(file: FileModel, selected: Boolean) {
        viewModel.selectFile(file, selected)
    }

    override fun selectFiles(files: FileItemSet, selected: Boolean) {
        viewModel.selectFiles(files, selected)
    }

    override fun openFile(file: FileModel) {
        val pickOptions = viewModel.pickOptions;
        if (file.isDirectory) {
            navigateTo(file.filePath)
        }
    }

    override fun openFileWith(file: FileModel) {
        val path = file.filePath
        if (!file.isDirectory && MimeTypeUtil().isSpecificFileType(
                fileUtil.getFileMimeType(path).toString(),
                MimeTypeIcon.CODE
            )
        ) {
            val fileUri = Uri.fromFile(File(path))

            val options = CodeEditorFragment.Options.Builder()
                .setUri(fileUri)
                .setTitle(requireContext().getString(R.string.code_editor))
                .setSubtitle(file.fileName)
                .setEnableSharing(true)
                .setJavaSmaliToggle(true)
                .setReadOnly(false)
                .build()
            val fragment = CodeEditorFragment()
            val args = Bundle()
            args.putParcelable(CodeEditorFragment.ARG_OPTIONS, options)
            fragment.arguments = args
            (requireActivity() as MainActivity).startNewFragment(fragment)


        }
    }

    override fun cutFile(file: FileModel) {
        createBottomSheetOperation(TypeOperation.CUT)
        finishActionMode()
    }

    override fun copyFile(file: FileModel) {
        TODO("Not yet implemented")
    }

    override fun confirmDeleteFile(file: FileItemSet) {
        val title = getString(R.string.delete)
        val text = getQuantityString(R.plurals.file_list_delete_count_format, file.size, file.size)
        val textPositiveButton = requireContext().getString(R.string.dialog_ok)

        materialDialogUtils.createDialogInfo(
            title,
            text,
            textPositiveButton,
            "",
            requireContext(),
            true
        ) { dialogResult ->
            val isConfirmed = dialogResult.confirmed
            if (isConfirmed) {
                // delete()
            }
        }

    }

    override fun showRenameFileDialog(file: FileModel) {
        val title = requireContext().getString(R.string.rename)
        val text = file.fileName
        val textPositiveButton = requireContext().getString(R.string.dialog_ok)

        materialDialogUtils.createBasicMaterial(title, text, textPositiveButton, requireContext()) { dialogResult ->
            val isConfirmed = dialogResult.confirmed
            val enteredText = dialogResult.text
            if (isConfirmed) {
                fileUtil.renameFile(file.filePath, enteredText)
            }
        }
    }

    override fun extractFile(file: FileModel) {
        TODO("Not yet implemented")
    }

    override fun showCreateArchiveDialog(file: FileModel) {
        TODO("Not yet implemented")
    }

    override fun shareFile(file: FileModel) {
        TODO("Not yet implemented")
    }

    override fun copyPath(file: FileModel) {
        TODO("Not yet implemented")
    }

    override fun addBookmark(file: FileModel) {
        TODO("Not yet implemented")
    }

    override fun createShortcut(file: FileModel) {
        TODO("Not yet implemented")
    }

    override fun showPropertiesDialog(file: FileModel) {
        TODO("Not yet implemented")
    }

    override fun showBottomSheet(file: FileModel) {
        showBottomSheetMoreActionFile(file)
    }

    override fun onClickFileAction(file: FileModel, action: CreateFileAction) {

        when (action) {

            CreateFileAction.OPEN_WITH -> {
                fileUtil.actionOpenWith(file.filePath, requireContext())
            }

            CreateFileAction.SELECT -> {
                selectFile(file, true)
            }

            CreateFileAction.RENAME -> {
                showRenameFileDialog(file)
            }

            CreateFileAction.DELETE -> {
                confirmDeleteFile(viewModel.selectedFiles)
            }

            CreateFileAction.SHARE -> {
                fileUtil.shareFile(file.filePath, requireContext())

            }

            CreateFileAction.PROPERTIES -> {
                showBottomSheetProperties(file)
            }

            else -> {}
        }
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN


    }


    private fun showBottomSheetMoreActionFile(fileItem: FileModel) {

        val fileOption = mutableListOf<FileAction>().apply {
            add(
                FileAction(
                    R.drawable.ic_open_with_24,
                    requireContext().getString(R.string.file_item_action_open_with),
                    CreateFileAction.OPEN_WITH
                )
            )
            add(
                FileAction(
                    R.drawable.ic_check,
                    requireContext().getString(R.string.action_bottom_select),
                    CreateFileAction.SELECT
                )
            )
            add(FileAction(R.drawable.ic_cut_24, requireContext().getString(R.string.cut), CreateFileAction.CUT))
            add(
                FileAction(
                    R.drawable.ic_copy_24, requireContext().getString(R.string.copy), CreateFileAction.RENAME
                )
            )
            add(
                FileAction(
                    R.drawable.ic_share24, requireContext().getString(R.string.share), CreateFileAction.SHARE
                )
            )
            add(
                FileAction(
                    R.drawable.ic_edit_24, requireContext().getString(R.string.rename), CreateFileAction.RENAME
                )
            )
            add(
                FileAction(
                    R.drawable.ic_trash_24, requireContext().getString(R.string.delete), CreateFileAction.DELETE
                )
            )

            add(
                FileAction(
                    R.drawable.ic_info_24,
                    requireContext().getString(R.string.file_item_action_properties),
                    CreateFileAction.PROPERTIES
                )
            )
        }


        val rvAction = requireView().findViewById<RecyclerView>(R.id.recyclerView2)
        val tvItemTitle = requireView().findViewById<TextView>(R.id.tv_title)

        tvItemTitle.text = fileItem.fileName
        rvAction.layoutManager = LinearLayoutManager(requireActivity())
        val actionAdapter = FileOptionAdapter(this, fileItem, fileOption)
        rvAction.adapter = actionAdapter

        standardBottomSheetBehavior.peekHeight = 800
        standardBottomSheetBehavior.maxHeight = 800
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    @SuppressLint("SetTextI18n")
    private fun createBottomSheetOperation(typeOperation: TypeOperation) {

        val ivCloseOp = requireView().findViewById<ImageView>(R.id.iv_close_op)
        val ivStartOp = requireView().findViewById<ImageView>(R.id.iv_start_op)
        val tvTitleOp = requireView().findViewById<TextView>(R.id.tv_title_op)

        val sourceFiles = viewModel.selectedFiles.map { it.file }


        standardBehaviorOperation.peekHeight = 300
        standardBehaviorOperation.maxHeight = 300
        standardBehaviorOperation.state = BottomSheetBehavior.STATE_EXPANDED

        tvTitleOp.text = "Movendo ${selectionTracker.selection.size()}"

        ivStartOp.setOnClickListener {
            val destinationDir = File(mCurrentPath)
            // viewModel.initOperation(typeOperation, sourceFiles, destinationDir, requireContext())
            standardBehaviorOperation.state = BottomSheetBehavior.STATE_HIDDEN
        }

        ivCloseOp.setOnClickListener {
            standardBehaviorOperation.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun initAllBottomSheet() {
        standardBottomSheet = requireView().findViewById(R.id.standard_bottom_sheet)
        standardBottomSheetBehavior = BottomSheetBehavior.from(standardBottomSheet)
        standardBottomSheetOp = requireView().findViewById(R.id.standard_bottom_operations)
        standardBehaviorOperation = BottomSheetBehavior.from(standardBottomSheetOp)
        bottomSheetProperties = requireView().findViewById(R.id.standard_bottom_properties)
        bottomSheetBehaviorProperties = BottomSheetBehavior.from(bottomSheetProperties)
        bottomSheetRename = requireView().findViewById(R.id.bottomSheetRename)
        bottomSheetBehaviorRename = BottomSheetBehavior.from(bottomSheetRename)

        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        standardBehaviorOperation.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehaviorProperties.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehaviorRename.state = BottomSheetBehavior.STATE_HIDDEN


    }

    @SuppressLint("SetTextI18n")
    private fun showBottomSheetProperties(fileItem: FileModel) {

        val tvFileItemTitle = requireView().findViewById<TextView>(R.id.tvItemTitle)

        tvFileItemTitle.text = " \"${fileItem.fileName}\""


        val fm: FragmentManager = requireActivity().supportFragmentManager
        val sa = ViewStateAdapter(fm, lifecycle, fileItem)
        val pa: ViewPager2 = requireView().findViewById(R.id.pager)
        pa.adapter = sa

        val tabLayout = requireView().findViewById<TabLayout>(R.id.tabLayout)

        tabLayout.addTab(tabLayout.newTab().setText("Básico"))
        tabLayout.addTab(tabLayout.newTab().setText("Extra"))


        bottomSheetBehaviorProperties.peekHeight = 1000
        bottomSheetBehaviorProperties.maxHeight = 1000
        bottomSheetBehaviorProperties.state = BottomSheetBehavior.STATE_EXPANDED


        bottomSheetBehaviorProperties.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                    tabLayout.clearOnTabSelectedListeners()
                    tabLayout.removeAllTabs()

                    pa.adapter = null


                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })


    }

    private fun showBottomSheetRenameMultipleFiles() {

        val listInputEditText = mutableListOf<TextInputEditText>()
        val selectedFiles = mutableListOf<FileModel>()
        val paths = mutableListOf<Path>()
        val layout = requireView().findViewById<LinearLayout>(R.id.linearLayout)
        val tvTitle = requireView().findViewById<TextView>(R.id.tv_title_rename)


        val btnRename = requireView().findViewById<Button>(R.id.btn_rename)


        for (fileItem in viewModel.selectedFiles) {
            if (!selectedFiles.contains(fileItem)) {
                selectedFiles.add(fileItem)
            }
        }

        tvTitle.text = resources.getQuantityString(R.plurals.renamingItems, 1, selectedFiles.size)
        for (file in selectedFiles) {
            val inflater = LayoutInflater.from(requireContext())

            val view = inflater.inflate(R.layout.layout_basic_dialog, null)
            val textInputLayout = view.findViewById<TextInputLayout>(R.id.eInputLayout)
            val textInputEditText = view.findViewById<TextInputEditText>(R.id.eInputEditText)

            textInputEditText.setText(file.fileName)
            layout.addView(view)
            listInputEditText.add(textInputEditText)
            paths.add(Paths.get(file.filePath))
        }

        btnRename.setOnClickListener {
            bottomSheetBehaviorRename.state = BottomSheetBehavior.STATE_HIDDEN
            val newNames = listInputEditText.map { it.text.toString() }
            coroutineViewModel.rename(paths.toList(), newNames.toList(), requireContext())
            /*
            CoroutineScope(Dispatchers.IO).launch {
                for ((index, file) in listPath.withIndex()) {
                    val newName = listInputEditText[index].text.toString()
                    FileUtil().renameFile(file, newName)
                    val mFile = File(file)

                    completedFiles++
                    val progress = (completedFiles.toFloat() / totalFiles.toFloat()) * 100

                    withContext(Dispatchers.Main) {
                        val title = resources.getQuantityString(R.plurals.renamingItems, 1, totalFiles)
                        val msg = "Renomeando \"${mFile.name}\" para \"$newName\""
                        updateProgress(title, msg, progress.toInt())
                    }

                }


                withContext(Dispatchers.Main) {
                    progressDialog?.cancel()
                    refresh()
                }
            }
*/
        }

        bottomSheetBehaviorRename.peekHeight = 1000
        bottomSheetBehaviorRename.maxHeight = 1000
        bottomSheetBehaviorRename.state = BottomSheetBehavior.STATE_EXPANDED

        bottomSheetBehaviorRename.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {

                    finishActionMode()
                    selectedFiles.clear()
                    layout.removeAllViews()


                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })


    }


    private fun observeSettings() {
        settingsViewModel.settingsState.observe(viewLifecycleOwner) { settingsState ->

            showHiddenFiles = settingsState.showHiddenFiles

            refresh()
        }

    }

    private fun delete(files: FileItemSet) {

    }


    fun updateProgress(title: String, msg: String, progress: Int) {
        if (!isProgressDialogShowing) {
            val inflater = LayoutInflater.from(context)
            val dialogView = inflater.inflate(R.layout.basic_dialog_progress, null)
            val lProgress = dialogView.findViewById<LinearProgressIndicator>(R.id.progressindicator)

            lProgress.progress = progress

            val builder = MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setTitle(title)
                .setMessage(msg)
                .setCancelable(false)

            progressDialog = builder.create()
            progressDialog?.show()
            isProgressDialogShowing = true
        } else {
            progressDialog?.setMessage(msg)
            val lProgress = progressDialog?.findViewById<LinearProgressIndicator>(R.id.progressindicator)
            lProgress?.progress = progress
        }
    }


}




