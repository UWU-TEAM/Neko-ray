package com.neko.v2ray.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.neko.v2ray.AppConfig
import com.neko.v2ray.AppConfig.VPN
import com.neko.v2ray.R
import com.neko.v2ray.databinding.ActivityMainBinding
import com.neko.v2ray.dto.EConfigType
import com.neko.v2ray.extension.toast
import com.neko.v2ray.handler.AngConfigManager
import com.neko.v2ray.handler.MigrateManager
import com.neko.v2ray.handler.MmkvManager
import com.neko.v2ray.helper.SimpleItemTouchHelperCallback
import com.neko.v2ray.service.V2RayServiceManager
import com.neko.v2ray.util.Utils
import com.neko.v2ray.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import android.content.Context
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.AttrRes
import android.annotation.SuppressLint
import android.app.AppOpsManager
import androidx.core.app.ActivityCompat
import com.neko.appupdater.AppUpdater
import com.neko.appupdater.enums.Display
import com.neko.appupdater.enums.UpdateFrom
import com.neko.expandable.layout.ExpandableView
import com.neko.themeengine.ThemeChooserDialogBuilder
import com.neko.themeengine.ThemeEngine
import com.neko.tools.NetworkSwitcher
import com.neko.tools.SpeedTestActivity
import com.neko.ip.HostToIpActivity
import com.neko.ip.IpLocation
import com.neko.ip.hostchecker.HostChecker
import android.graphics.Color
import com.google.android.material.card.MaterialCardView
import com.neko.uwu.*
import android.database.Cursor

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val adapter by lazy { MainRecyclerAdapter(this) }
    private val requestVpnPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            startV2Ray()
        }
    }
    private val requestSubSettingActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        initGroupTab()
    }
    private val tabGroupListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            val selectId = tab?.tag.toString()
            if (selectId != mainViewModel.subscriptionId) {
                mainViewModel.subscriptionIdChanged(selectId)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }
    private var mItemTouchHelper: ItemTouchHelper? = null
    val mainViewModel: MainViewModel by viewModels()
    private lateinit var expandableConnection: ExpandableView
    private lateinit var bottomSheetTools: MaterialCardView
    private lateinit var rvDatabase: RecyclerView
    private lateinit var myDB: MyDatabaseHelper
    private lateinit var adapterDatabase: AdapterDatabase
    private lateinit var arrID: ArrayList<String>
    private lateinit var arrName: ArrayList<String>
    private lateinit var arrAge: ArrayList<String>
    private lateinit var arrTgl: ArrayList<String>
    private lateinit var arrHobi: ArrayList<String>
    private lateinit var arrEmail: ArrayList<String>

    companion object {
        var posisiData = 0
    }

    // register activity result for requesting permission
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                when (pendingAction) {
                    Action.IMPORT_QR_CODE_CONFIG ->
                        scanQRCodeForConfig.launch(Intent(this, ScannerActivity::class.java))

//                    Action.IMPORT_QR_CODE_URL ->
//                        scanQRCodeForUrlToCustomConfig.launch(Intent(this, ScannerActivity::class.java))

                    Action.READ_CONTENT_FROM_URI ->
                        chooseFileForCustomConfig.launch(Intent.createChooser(Intent(Intent.ACTION_GET_CONTENT).apply {
                            type = "*/*"
                            addCategory(Intent.CATEGORY_OPENABLE)
                        }, getString(R.string.title_file_chooser)))

                    Action.POST_NOTIFICATIONS -> {}
                    else -> {}
                }
            } else {
                toast(R.string.toast_permission_denied)
            }
            pendingAction = Action.NONE
        }

    private var pendingAction: Action = Action.NONE

    enum class Action {
        NONE,
        IMPORT_QR_CODE_CONFIG,

        //IMPORT_QR_CODE_URL,
        READ_CONTENT_FROM_URI,
        POST_NOTIFICATIONS
    }

    private val chooseFileForCustomConfig = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val uri = it.data?.data
        if (it.resultCode == RESULT_OK && uri != null) {
            readContentFromUri(uri)
        }
    }

    private val scanQRCodeForConfig = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            importBatchConfig(it.data?.getStringExtra("SCAN_RESULT"))
        }
    }

//    private val scanQRCodeForUrlToCustomConfig = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//        if (it.resultCode == RESULT_OK) {
//            importConfigCustomUrl(it.data?.getStringExtra("SCAN_RESULT"))
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bottomSheetTools = findViewById(R.id.uwu_menu_tool)
        bottomSheetTools.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(this@MainActivity)
            val inflater = layoutInflater
            val view = inflater.inflate(R.layout.uwu_menu_tools_bottom_sheet, null)
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()
        }
        title = getString(R.string.app_name)
        setSupportActionBar(binding.toolbar)
        expandableConnection = findViewById(R.id.uwu_connection_expanded)
        expandableConnection.setExpansion(false)

        binding.fab.setOnClickListener {
            if (mainViewModel.isRunning.value == true) {
                V2RayServiceManager.stopVService(this)
            } else if ((MmkvManager.decodeSettingsString(AppConfig.PREF_MODE) ?: VPN) == VPN) {
                val intent = VpnService.prepare(this)
                if (intent == null) {
                    startV2Ray()
                } else {
                    requestVpnPermission.launch(intent)
                }
            } else {
                startV2Ray()
            }
        }
        binding.layoutTest.setOnClickListener {
            if (mainViewModel.isRunning.value == true) {
                setTestState(getString(R.string.connection_test_testing))
                mainViewModel.testCurrentServerRealPing()
            } else {
//                tv_test_state.text = getString(R.string.connection_test_fail)
            }
        }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        addCustomDividerToRecyclerView(binding.recyclerView, this, R.drawable.custom_divider)
        binding.recyclerView.adapter = adapter

        mItemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(adapter))
        mItemTouchHelper?.attachToRecyclerView(binding.recyclerView)

        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)

        initGroupTab()
        setupViewModel()
        migrateLegacy()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                pendingAction = Action.POST_NOTIFICATIONS
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })

        @ColorInt
        fun Context.getColorFromAttr(@AttrRes attrColor: Int): Int {
            val typedArray = obtainStyledAttributes(intArrayOf(attrColor))
            val color = typedArray.getColor(0, 0)
            typedArray.recycle()
            return color
        }

        // Show new update with dialog
        val appUpdater = AppUpdater(this).apply {
            setUpdateFrom(UpdateFrom.JSON)
            setUpdateJSON(AppConfig.UWU_UPDATE_URL)
            setDisplay(Display.DIALOG)
            showAppUpdated(false)
            setCancelable(false)
        }
        appUpdater.start()

        myDB = MyDatabaseHelper(this)
        var usernameuwu = intent.getStringExtra("varName")
        if (usernameuwu == null) {
            usernameuwu = "Neko-Ray"
        }
        binding.uwuUsername.text = getString(R.string.uwu_header_title) + " @" + usernameuwu
    }

    private fun SQLiteToArrayList() {
        val cursor = myDB.bacaSemuaData()
        if (cursor?.count == 0) {
            // Toast.makeText(this, "Tidak ada data", Toast.LENGTH_SHORT).show()
        } else {
            cursor?.use {
                while (it.moveToNext()) {
                    arrID.add(it.getString(0))
                    arrName.add(it.getString(1))
                    arrAge.add(it.getString(2))
                    arrTgl.add(it.getString(3))
                    arrHobi.add(it.getString(4))
                    arrEmail.add(it.getString(5))
                }
            }
        }
    }

    private fun setupViewModel() {
        mainViewModel.updateListAction.observe(this) { index ->
            if (index >= 0) {
                adapter.notifyItemChanged(index)
            } else {
                adapter.notifyDataSetChanged()
            }
        }
        mainViewModel.updateTestResultAction.observe(this) { setTestState(it) }
        mainViewModel.isRunning.observe(this) { isRunning ->
            adapter.isRunning = isRunning
            if (isRunning) {
                binding.fab.setImageResource(R.drawable.ic_stop_24dp)
                expandableConnection.expand()
                expandableConnection.orientation = ExpandableView.HORIZONTAL
                expandableConnection.setExpansion(true)
                // binding.fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_fab_orange))
                setTestState(getString(R.string.connection_connected))
                binding.layoutTest.isFocusable = true
            } else {
                binding.fab.setImageResource(R.drawable.ic_play_24dp)
                expandableConnection.collapse()
                expandableConnection.orientation = ExpandableView.HORIZONTAL
                expandableConnection.setExpansion(false)
                // binding.fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_fab_grey))
                setTestState(getString(R.string.connection_not_connected))
                binding.layoutTest.isFocusable = false
            }
        }
        mainViewModel.startListenBroadcast()
        mainViewModel.initAssets(assets)
    }

    private fun migrateLegacy() {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = MigrateManager.migrateServerConfig2Profile()
            launch(Dispatchers.Main) {
                if (result) {
                    toast(getString(R.string.migration_success))
                    mainViewModel.reloadServerList()
                } else {
                    //toast(getString(R.string.migration_fail))
                }
            }

        }
    }

    private fun initGroupTab() {
        binding.tabGroup.removeOnTabSelectedListener(tabGroupListener)
        binding.tabGroup.removeAllTabs()
        binding.tabGroup.isVisible = false

        val (listId, listRemarks) = mainViewModel.getSubscriptions(this)
        if (listId == null || listRemarks == null) {
            return
        }

        for (it in listRemarks.indices) {
            val tab = binding.tabGroup.newTab()
            tab.text = listRemarks[it]
            tab.tag = listId[it]
            binding.tabGroup.addTab(tab)
        }
        val selectIndex =
            listId.indexOf(mainViewModel.subscriptionId).takeIf { it >= 0 } ?: (listId.count() - 1)
        binding.tabGroup.selectTab(binding.tabGroup.getTabAt(selectIndex))
        binding.tabGroup.addOnTabSelectedListener(tabGroupListener)
        binding.tabGroup.isVisible = true
    }

    fun startV2Ray() {
        if (MmkvManager.getSelectServer().isNullOrEmpty()) {
            toast(R.string.title_file_chooser)
            return
        }
        V2RayServiceManager.startVService(this)
    }

    fun restartV2Ray() {
        if (mainViewModel.isRunning.value == true) {
            V2RayServiceManager.stopVService(this)
        }
        lifecycleScope.launch {
            delay(500)
            startV2Ray()
        }
        expandableConnection.collapse()
        expandableConnection.orientation = ExpandableView.HORIZONTAL
        expandableConnection.setExpansion(false)
    }

    public override fun onResume() {
        super.onResume()
        mainViewModel.reloadServerList()

        arrID = ArrayList()
        arrName = ArrayList()
        arrAge = ArrayList()
        arrTgl = ArrayList()
        arrHobi = ArrayList()
        arrEmail = ArrayList()

        SQLiteToArrayList()

        rvDatabase = findViewById(R.id.rv_database)
        adapterDatabase = AdapterDatabase(this, arrID, arrName, arrAge, arrTgl, arrHobi, arrEmail)
        rvDatabase.adapter = adapterDatabase
        rvDatabase.layoutManager = LinearLayoutManager(this)
        rvDatabase.smoothScrollToPosition(posisiData)
    }

    public override fun onPause() {
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu.findItem(R.id.search_view)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    mainViewModel.filterConfig(newText.orEmpty())
                    return false
                }
            })

            searchView.setOnCloseListener {
                mainViewModel.filterConfig("")
                false
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.theme_settings -> {
            SettingsFragmentTheme().show(supportFragmentManager, "Theme Settings")
            true
        }

        R.id.import_qrcode -> {
            importQRcode(true)
            true
        }

        R.id.import_clipboard -> {
            importClipboard()
            true
        }

        R.id.import_local -> {
            importConfigLocal()
            true
        }

        R.id.import_manually_vmess -> {
            importManually(EConfigType.VMESS.value)
            true
        }

        R.id.import_manually_vless -> {
            importManually(EConfigType.VLESS.value)
            true
        }

        R.id.import_manually_ss -> {
            importManually(EConfigType.SHADOWSOCKS.value)
            true
        }

        R.id.import_manually_socks -> {
            importManually(EConfigType.SOCKS.value)
            true
        }

        R.id.import_manually_http -> {
            importManually(EConfigType.HTTP.value)
            true
        }

        R.id.import_manually_trojan -> {
            importManually(EConfigType.TROJAN.value)
            true
        }

        R.id.import_manually_wireguard -> {
            importManually(EConfigType.WIREGUARD.value)
            true
        }

        R.id.import_manually_hysteria2 -> {
            importManually(EConfigType.HYSTERIA2.value)
            true
        }

//        R.id.import_config_custom_clipboard -> {
//            importConfigCustomClipboard()
//            true
//        }
//
//        R.id.import_config_custom_local -> {
//            importConfigCustomLocal()
//            true
//        }
//
//        R.id.import_config_custom_url -> {
//            importConfigCustomUrlClipboard()
//            true
//        }
//
//        R.id.import_config_custom_url_scan -> {
//            importQRcode(false)
//            true
//        }

        R.id.export_all -> {
            exportAll()
            true
        }

        R.id.ping_all -> {
            toast(getString(R.string.connection_test_testing_count, mainViewModel.serversCache.count()))
            mainViewModel.testAllTcping()
            true
        }

        R.id.real_ping_all -> {
            toast(getString(R.string.connection_test_testing_count, mainViewModel.serversCache.count()))
            mainViewModel.testAllRealPing()
            true
        }

        R.id.service_restart -> {
            restartV2Ray()
            true
        }

        R.id.del_all_config -> {
            delAllConfig()
            true
        }

        R.id.del_duplicate_config -> {
            delDuplicateConfig()
            true
        }

        R.id.del_invalid_config -> {
            delInvalidConfig()
            true
        }

        R.id.sort_by_test_results -> {
            sortByTestResults()
            true
        }

        R.id.sub_update -> {
            importConfigViaSub()
            true
        }


        else -> super.onOptionsItemSelected(item)
    }

    private fun importManually(createConfigType: Int) {
        startActivity(
            Intent()
                .putExtra("createConfigType", createConfigType)
                .putExtra("subscriptionId", mainViewModel.subscriptionId)
                .setClass(this, ServerActivity::class.java)
        )
    }

    /**
     * import config from qrcode
     */
    private fun importQRcode(forConfig: Boolean): Boolean {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            if (forConfig) {
                scanQRCodeForConfig.launch(Intent(this, ScannerActivity::class.java))
            } else {
                //scanQRCodeForUrlToCustomConfig.launch(Intent(this, ScannerActivity::class.java))
            }
        } else {
            pendingAction = Action.IMPORT_QR_CODE_CONFIG//if (forConfig) Action.IMPORT_QR_CODE_CONFIG else Action.IMPORT_QR_CODE_URL
            requestPermissionLauncher.launch(permission)
        }
        return true
    }

    /**
     * import config from clipboard
     */
    private fun importClipboard()
            : Boolean {
        try {
            val clipboard = Utils.getClipboard(this)
            importBatchConfig(clipboard)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun importBatchConfig(server: String?) {
        binding.pbWaiting.show()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val (count, countSub) = AngConfigManager.importBatchConfig(server, mainViewModel.subscriptionId, true)
                delay(500L)
                withContext(Dispatchers.Main) {
                    when {
                        count > 0 -> {
                            toast(getString(R.string.title_import_config_count, count))
                            mainViewModel.reloadServerList()
                        }

                        countSub > 0 -> initGroupTab()
                        else -> toast(R.string.toast_failure)
                    }
                    binding.pbWaiting.hide()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    toast(R.string.toast_failure)
                    binding.pbWaiting.hide()
                }
                e.printStackTrace()
            }
        }
    }

    private fun importConfigLocal(): Boolean {
        try {
            showFileChooser()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }


//    private fun importConfigCustomClipboard()
//            : Boolean {
//        try {
//            val configText = Utils.getClipboard(this)
//            if (TextUtils.isEmpty(configText)) {
//                toast(R.string.toast_none_data_clipboard)
//                return false
//            }
//            importCustomizeConfig(configText)
//            return true
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return false
//        }
//    }

    /**
     * import config from local config file
     */
//    private fun importConfigCustomLocal(): Boolean {
//        try {
//            showFileChooser()
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return false
//        }
//        return true
//    }
//
//    private fun importConfigCustomUrlClipboard()
//            : Boolean {
//        try {
//            val url = Utils.getClipboard(this)
//            if (TextUtils.isEmpty(url)) {
//                toast(R.string.toast_none_data_clipboard)
//                return false
//            }
//            return importConfigCustomUrl(url)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return false
//        }
//    }

    /**
     * import config from url
     */
//    private fun importConfigCustomUrl(url: String?): Boolean {
//        try {
//            if (!Utils.isValidUrl(url)) {
//                toast(R.string.toast_invalid_url)
//                return false
//            }
//            lifecycleScope.launch(Dispatchers.IO) {
//                val configText = try {
//                    HttpUtil.getUrlContentWithUserAgent(url)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    ""
//                }
//                launch(Dispatchers.Main) {
//                    importCustomizeConfig(configText)
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            return false
//        }
//        return true
//    }

    /**
     * import config from sub
     */
    private fun importConfigViaSub(): Boolean {
        binding.pbWaiting.show()

        lifecycleScope.launch(Dispatchers.IO) {
            val count = mainViewModel.updateConfigViaSubAll()
            delay(500L)
            launch(Dispatchers.Main) {
                if (count > 0) {
                    toast(getString(R.string.title_update_config_count, count))
                    mainViewModel.reloadServerList()
                } else {
                    toast(R.string.toast_failure)
                }
                binding.pbWaiting.hide()
            }
        }
        return true
    }

    private fun exportAll() {
        binding.pbWaiting.show()
        lifecycleScope.launch(Dispatchers.IO) {
            val ret = mainViewModel.exportAllServer()
            launch(Dispatchers.Main) {
                if (ret > 0)
                    toast(getString(R.string.title_export_config_count, ret))
                else
                    toast(R.string.toast_failure)
                binding.pbWaiting.hide()
            }
        }
    }

    private fun delAllConfig() {
        MaterialAlertDialogBuilder(this).setMessage(R.string.del_config_comfirm)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                binding.pbWaiting.show()
                lifecycleScope.launch(Dispatchers.IO) {
                    val ret = mainViewModel.removeAllServer()
                    launch(Dispatchers.Main) {
                        mainViewModel.reloadServerList()
                        toast(getString(R.string.title_del_config_count, ret))
                        binding.pbWaiting.hide()
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                //do noting
            }
            .show()
    }

    private fun delDuplicateConfig() {
        MaterialAlertDialogBuilder(this).setMessage(R.string.del_config_comfirm)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                binding.pbWaiting.show()
                lifecycleScope.launch(Dispatchers.IO) {
                    val ret = mainViewModel.removeDuplicateServer()
                    launch(Dispatchers.Main) {
                        mainViewModel.reloadServerList()
                        toast(getString(R.string.title_del_duplicate_config_count, ret))
                        binding.pbWaiting.hide()
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                //do noting
            }
            .show()
    }

    private fun delInvalidConfig() {
        MaterialAlertDialogBuilder(this).setMessage(R.string.del_invalid_config_comfirm)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                binding.pbWaiting.show()
                lifecycleScope.launch(Dispatchers.IO) {
                    val ret = mainViewModel.removeInvalidServer()
                    launch(Dispatchers.Main) {
                        mainViewModel.reloadServerList()
                        toast(getString(R.string.title_del_config_count, ret))
                        binding.pbWaiting.hide()
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                //do noting
            }
            .show()
    }

    private fun sortByTestResults() {
        binding.pbWaiting.show()
        lifecycleScope.launch(Dispatchers.IO) {
            mainViewModel.sortByTestResults()
            launch(Dispatchers.Main) {
                mainViewModel.reloadServerList()
                binding.pbWaiting.hide()
            }
        }
    }

    /**
     * show file chooser
     */
    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            pendingAction = Action.READ_CONTENT_FROM_URI
            chooseFileForCustomConfig.launch(Intent.createChooser(intent, getString(R.string.title_file_chooser)))
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    /**
     * read content from uri
     */
    private fun readContentFromUri(uri: Uri) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            try {
                contentResolver.openInputStream(uri).use { input ->
                    importBatchConfig(input?.bufferedReader()?.readText())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun keluar() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setMessage(R.string.uwu_exit_message)
            .setCancelable(false) // tidak bisa tekan tombol back
            // jika pilih yes
            .setPositiveButton(R.string.uwu_text_yes) { _, _ ->
                if (mainViewModel.isRunning.value == true) {
                    V2RayServiceManager.stopVService(this)
                }
                this@MainActivity.finish()
            }
            // jika pilih no
            .setNegativeButton(R.string.uwu_text_no) { dialog, _ ->
                dialog.cancel()
            }
            // Run in background
            .setNeutralButton("Minimize") { _, _ ->
                this@MainActivity.finishAffinity()
            }.show()
    }

//    /**
//     * import customize config
//     */
//    private fun importCustomizeConfig(server: String?) {
//        try {
//            if (server == null || TextUtils.isEmpty(server)) {
//                toast(R.string.toast_none_data)
//                return
//            }
//            if (mainViewModel.appendCustomConfigServer(server)) {
//                mainViewModel.reloadServerList()
//                toast(R.string.toast_success)
//            } else {
//                toast(R.string.toast_failure)
//            }
//            //adapter.notifyItemInserted(mainViewModel.serverList.lastIndex)
//        } catch (e: Exception) {
//            ToastCompat.makeText(this, "${getString(R.string.toast_malformed_josn)} ${e.cause?.message}", Toast.LENGTH_LONG).show()
//            e.printStackTrace()
//            return
//        }
//    }

    private fun setTestState(content: String?) {
        binding.tvTestState.text = content
    }

//    val mConnection = object : ServiceConnection {
//        override fun onServiceDisconnected(name: ComponentName?) {
//        }
//
//        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
//            sendMsg(AppConfig.MSG_REGISTER_CLIENT, "")
//        }
//    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_BUTTON_B) {
            moveTaskToBack(false)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun settingsExtra(): Boolean {
        startActivity(Intent(this, SettingsActivity::class.java).putExtra("isRunning", mainViewModel.isRunning.value == true))
        return true
    }

    fun logcat(view: View) {
        startActivity(Intent(this, LogcatActivity::class.java))
    }

    fun settings(view: View) {
        settingsExtra()
    }

    fun subSetting(view: View) {
        requestSubSettingActivity.launch(Intent(this,SubSettingActivity::class.java))
    }

    fun UwuAbout(view: View) {
        startActivity(Intent(this, NekoAboutActivity::class.java))
    }

    fun uwuSpeedTest(view: View) {
        startActivity(Intent(this, SpeedTestActivity::class.java))
    }

    fun uwuNetworkSwitcher(view: View) {
        startActivity(Intent(this, NetworkSwitcher::class.java))
    }

    fun uwuHostToIp(view: View) {
        startActivity(Intent(this, HostToIpActivity::class.java))
    }

    fun uwuHostCeker(view: View) {
        startActivity(Intent(this, HostChecker::class.java))
    }

    fun uwuIpLocation(view: View) {
        startActivity(Intent(this, IpLocation::class.java))
    }

    fun uwuBackupConfig(view: View) {
        startActivity(Intent(this, NekoBackupActivity::class.java))
    }

    fun uwuReportIssue(view: View) {
        Utils.openUri(this, AppConfig.v2rayNGIssues)
    }

    fun uwuRoutingSetting(view: View) {
        requestSubSettingActivity.launch(Intent(this, RoutingSettingActivity::class.java))
    }

    fun uwuExitApp(view: View) {
        keluar()
    }

    fun uwuEditProfile(view: View) {
        val cursor: Cursor? = myDB.bacaSemuaData()
        if (cursor == null || cursor.count == 0) {
            startActivity(Intent(this, TambahActivity::class.java))
        } else {
            try {
                val db = myDB.readableDatabase
                val query = "SELECT * FROM nekoray"
                val rs: Cursor = db.rawQuery(query, null)
            
                if (rs.moveToFirst()) {
                    val arrID = rs.getString(rs.getColumnIndexOrThrow("id"))
                    val name = rs.getString(rs.getColumnIndexOrThrow("name"))
                    val age = rs.getString(rs.getColumnIndexOrThrow("age"))
                    val tgl = rs.getString(rs.getColumnIndexOrThrow("tgl"))
                    val hobi = rs.getString(rs.getColumnIndexOrThrow("hobi"))
                    val email = rs.getString(rs.getColumnIndexOrThrow("email"))
                    val posisi = 1

                    val intent = Intent(this, UbahActivity::class.java).apply {
                        putExtra("varID", arrID)
                        putExtra("varName", name)
                        putExtra("varAge", age)
                        putExtra("varTgl", tgl)
                        putExtra("varHobi", hobi)
                        putExtra("varEmail", email)
                        putExtra("varPosisi", posisi)
                    }
                    startActivity(intent)
                }
                rs.close()
            } finally {
                cursor?.close()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        // when (item.itemId) {
            // R.id.sub_setting -> requestSubSettingActivity.launch(Intent(this, SubSettingActivity::class.java))
            // R.id.settings -> startActivity(
                // Intent(this, SettingsActivity::class.java)
                    // .putExtra("isRunning", mainViewModel.isRunning.value == true)
            // )

            // R.id.per_app_proxy_settings -> startActivity(Intent(this, PerAppProxyActivity::class.java))
            // R.id.routing_setting -> requestSubSettingActivity.launch(Intent(this, RoutingSettingActivity::class.java))
            // R.id.promotion -> Utils.openUri(this, "${Utils.decode(AppConfig.PromotionUrl)}?t=${System.currentTimeMillis()}")
            // R.id.logcat -> startActivity(Intent(this, LogcatActivity::class.java))
            // R.id.about -> startActivity(Intent(this, AboutActivity::class.java))
        // }

        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}