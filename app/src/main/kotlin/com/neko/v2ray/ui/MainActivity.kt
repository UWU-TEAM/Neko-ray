package com.neko.v2ray.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
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
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.tbruyelle.rxpermissions3.RxPermissions
import com.neko.v2ray.AppConfig
import com.neko.v2ray.R
import com.neko.v2ray.databinding.ActivityMainBinding
import com.neko.v2ray.dto.EConfigType
import com.neko.v2ray.extension.toast
import com.neko.v2ray.helper.SimpleItemTouchHelperCallback
import com.neko.v2ray.nekonet.*
import com.neko.v2ray.service.V2RayServiceManager
import com.neko.v2ray.util.AngConfigManager
import com.neko.v2ray.util.MmkvManager
import com.neko.v2ray.util.Utils
import com.neko.v2ray.viewmodel.MainViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.drakeet.support.toast.ToastCompat
import java.util.concurrent.TimeUnit

import android.content.Context
import android.content.pm.PackageManager
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
import com.neko.nointernet.callbacks.ConnectionCallback
import com.neko.nointernet.dialogs.signal.NoInternetDialogSignal
import com.neko.nekodrawer.NekoDrawerView
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.os.Process
import android.provider.Settings

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
    val TAG = "MainActivity"
    private lateinit var expandableContent: ExpandableView
    private lateinit var expandableConnection: ExpandableView

    override fun onCreate(savedInstanceState: Bundle?) {
        setupPermissions()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        title = getString(R.string.app_title_name)
        setSupportActionBar(binding.toolbar)
        expandableContent = findViewById(R.id.uwu_header_home)
        expandableContent.setExpansion(false)
        expandableConnection = findViewById(R.id.uwu_connection_expanded)
        expandableConnection.setExpansion(false)

        val networkUsage = NetworkManager(this, Util.getSubscriberId(this))
        val handler = Handler(Looper.getMainLooper())
        val runnableCode = object : Runnable {
            override fun run() {
                val now = networkUsage.getUsageNow(NetworkType.ALL)
                val speeds = NetSpeed.calculateSpeed(now.timeTaken, now.downloads, now.uploads)

                binding.apply {
                    totalSpeedTv.text = speeds[0].speed + "\n" + speeds[0].unit
                    upUsagesTv.text = "▲ " + speeds[2].speed + speeds[2].unit
                    downUsagesTv.text = "▼ " + speeds[1].speed + speeds[1].unit
                }
                handler.postDelayed(this, 1000)
            }
        }

        val runnableCode1 = object : Runnable {
            override fun run() {
                val todayM = networkUsage.getUsage(Interval.today, NetworkType.MOBILE)
                val todayW = networkUsage.getUsage(Interval.today, NetworkType.WIFI)
                
                binding.apply {
                    wifiUsagesTv.text = "wifi" + "\n" + Util.formatData(todayW.downloads, todayW.uploads)[2]
                    dataUsagesTv.text = "mobile" + "\n" + Util.formatData(todayM.downloads, todayM.uploads)[2]
                }
                handler.postDelayed(this, 216000)
            }
        }

        fun stoptraffic() {
            handler.removeCallbacks(runnableCode)
            binding.apply {
                totalSpeedTv.text = "0" + "\n" + "kB/s"
                upUsagesTv.text = "▲ " + "0kB/s"
                downUsagesTv.text = "▼ " + "0kB/s"
            }
            handler.removeCallbacks(runnableCode1)
        }

        binding.fab.setOnClickListener {
            if (mainViewModel.isRunning.value == true) {
                Utils.stopVService(this)
                stoptraffic()
            } else if ((MmkvManager.settingsStorage?.decodeString(AppConfig.PREF_MODE) ?: "VPN") == "VPN") {
                val intent = VpnService.prepare(this)
                if (intent == null) {
                    startV2Ray()
                    runnableCode.run()
                    runnableCode1.run()
                } else {
                    requestVpnPermission.launch(intent)
                }
            } else {
                startV2Ray()
                runnableCode.run()
                runnableCode1.run()
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
        binding.recyclerView.adapter = adapter

        val callback = SimpleItemTouchHelperCallback(adapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(binding.recyclerView)


        val toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT)
        binding.navView.setNavigationItemSelectedListener(this)

        initGroupTab()
        setupViewModel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RxPermissions(this)
                .request(Manifest.permission.POST_NOTIFICATIONS)
                .subscribe {
                    if (!it)
                        toast(R.string.toast_permission_denied)
                }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    //super.onBackPressed()
                    onBackPressedDispatcher.onBackPressed()
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

        startNoInternetDialog()

        binding.drawerLayout.setNekoDrawerListener(object : NekoDrawerView.NekoDrawerEvents {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }
        })
        // Show new update with dialog
        val appUpdater = AppUpdater(this).apply {
            setUpdateFrom(UpdateFrom.JSON)
            setUpdateJSON(AppConfig.UWU_UPDAYE_URL)
            setDisplay(Display.DIALOG)
            showAppUpdated(false)
            setCancelable(false)
        }
        appUpdater.start()

        // Show new update with notification
        val appUpdaterNotification = AppUpdater(this).apply {
            setUpdateFrom(UpdateFrom.JSON)
            setUpdateJSON(AppConfig.UWU_UPDAYE_URL)
            setDisplay(Display.NOTIFICATION)
            showAppUpdated(false)
            setCancelable(false)
        }
        appUpdaterNotification.start()
    }

    private fun startNoInternetDialog() {
        NoInternetDialogSignal.Builder(
            this,
            lifecycle
        ).apply {
            dialogProperties.apply {
                connectionCallback = object : ConnectionCallback { // Optional
                    override fun hasActiveConnection(hasActiveConnection: Boolean) {
                        // ...
                    }
                }
                cancelable = false // Optional
                showInternetOnButtons = true // Optional
                showAirplaneModeOffButtons = true // Optional
            }
        }.build()
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
                binding.fab.setImageResource(R.drawable.uwu_ic_service_busy)
                expandableConnection.expand()
                expandableConnection.orientation = ExpandableView.HORIZONTAL
                expandableConnection.setExpansion(true)
                // binding.fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_fab_orange))
                setTestState(getString(R.string.connection_connected))
                binding.layoutTest.isFocusable = true
            } else {
                binding.fab.setImageResource(R.drawable.uwu_ic_service_idle)
                expandableConnection.collapse()
                expandableConnection.orientation = ExpandableView.HORIZONTAL
                expandableConnection.setExpansion(false)
                // binding.fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_fab_grey))
                setTestState(getString(R.string.connection_not_connected))
                binding.layoutTest.isFocusable = false
            }
        }
        mainViewModel.startListenBroadcast()
        mainViewModel.copyAssets(assets)
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
        if (MmkvManager.mainStorage?.decodeString(MmkvManager.KEY_SELECTED_SERVER).isNullOrEmpty()) {
            return
        }
        V2RayServiceManager.startV2Ray(this)
    }

    fun restartV2Ray() {
        if (mainViewModel.isRunning.value == true) {
            Utils.stopVService(this)
        }
        Observable.timer(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                startV2Ray()
            }
        expandableConnection.collapse()
        expandableConnection.orientation = ExpandableView.HORIZONTAL
        expandableConnection.setExpansion(false)
    }

    public override fun onResume() {
        super.onResume()
        mainViewModel.reloadServerList()
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
            if (mainViewModel.isRunning.value == true) {
                uwuVpnIsRun()
            } else if (expandableContent.isExpanded) {
                uwuBannerIsOpen()
            } else {
                SettingsFragmentTheme().show(supportFragmentManager, "Theme Settings")
            }
            true
        }

        R.id.uwu_banner_hide -> {
            uwuBanner()
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

        R.id.import_manually_trojan -> {
            importManually(EConfigType.TROJAN.value)
            true
        }

        R.id.import_manually_wireguard -> {
            importManually(EConfigType.WIREGUARD.value)
            true
        }

        R.id.import_config_custom_clipboard -> {
            importConfigCustomClipboard()
            true
        }

        R.id.import_config_custom_local -> {
            importConfigCustomLocal()
            true
        }

        R.id.import_config_custom_url -> {
            importConfigCustomUrlClipboard()
            true
        }

        R.id.import_config_custom_url_scan -> {
            importQRcode(false)
            true
        }

        R.id.sub_update -> {
            importConfigViaSub()
            true
        }

        R.id.export_all -> {
            binding.pbWaiting.show()
            lifecycleScope.launch(Dispatchers.IO) {
                val ret = mainViewModel.exportAllServer()
                launch(Dispatchers.Main) {
                    if (ret == 0)
                        toast(R.string.toast_success)
                    else
                        toast(R.string.toast_failure)
                    binding.pbWaiting.hide()
                }
            }

            true
        }

        R.id.ping_all -> {
            mainViewModel.testAllTcping()
            true
        }

        R.id.real_ping_all -> {
            mainViewModel.testAllRealPing()
            true
        }

        R.id.service_restart -> {
            restartV2Ray()
            true
        }

        R.id.del_all_config -> {
            MaterialAlertDialogBuilder(this).setMessage(R.string.del_config_comfirm)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    binding.pbWaiting.show()
                    lifecycleScope.launch(Dispatchers.IO) {
                        mainViewModel.removeAllServer()
                        launch(Dispatchers.Main) {
                            mainViewModel.reloadServerList()
                            binding.pbWaiting.hide()
                        }
                    }
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    //do noting
                }
                .show()
            true
        }

        R.id.del_duplicate_config-> {
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
                .setNegativeButton(android.R.string.no) { _, _ ->
                    //do noting
                }
                .show()
            true
        }

        R.id.del_invalid_config -> {
            MaterialAlertDialogBuilder(this).setMessage(R.string.del_invalid_config_comfirm)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    binding.pbWaiting.show()
                    lifecycleScope.launch(Dispatchers.IO) {
                        mainViewModel.removeInvalidServer()
                        launch(Dispatchers.Main) {
                            mainViewModel.reloadServerList()
                            binding.pbWaiting.hide()
                        }
                    }
                }
                .setNegativeButton(android.R.string.no) { _, _ ->
                    //do noting
                }
                .show()
            true
        }

        R.id.sort_by_test_results -> {
            binding.pbWaiting.show()
            lifecycleScope.launch(Dispatchers.IO) {
                mainViewModel.sortByTestResults()
                launch(Dispatchers.Main) {
                    mainViewModel.reloadServerList()
                    binding.pbWaiting.hide()
                }
            }
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
//        try {
//            startActivityForResult(Intent("com.google.zxing.client.android.SCAN")
//                    .addCategory(Intent.CATEGORY_DEFAULT)
//                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP), requestCode)
//        } catch (e: Exception) {
        RxPermissions(this)
            .request(Manifest.permission.CAMERA)
            .subscribe {
                if (it)
                    if (forConfig)
                        scanQRCodeForConfig.launch(Intent(this, ScannerActivity::class.java))
                    else
                        scanQRCodeForUrlToCustomConfig.launch(Intent(this, ScannerActivity::class.java))
                else
                    toast(R.string.toast_permission_denied)
            }
//        }
        return true
    }

    private val scanQRCodeForConfig = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            importBatchConfig(it.data?.getStringExtra("SCAN_RESULT"))
        }
    }

    private val scanQRCodeForUrlToCustomConfig = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            importConfigCustomUrl(it.data?.getStringExtra("SCAN_RESULT"))
        }
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
//        val dialog = MaterialAlertDialogBuilder(this)
//            .setView(LayoutProgressBinding.inflate(layoutInflater).root)
//            .setCancelable(false)
//            .show()
        binding.pbWaiting.show()

        lifecycleScope.launch(Dispatchers.IO) {
            val (count, countSub) = AngConfigManager.importBatchConfig(server, mainViewModel.subscriptionId, true)
            delay(500L)
            launch(Dispatchers.Main) {
                if (count > 0) {
                    toast(R.string.toast_success)
                    mainViewModel.reloadServerList()
                } else if (countSub > 0) {
                    initGroupTab()
                } else {
                    toast(R.string.toast_failure)
                }
                //dialog.dismiss()
                binding.pbWaiting.hide()
            }
        }
    }

    private fun importConfigCustomClipboard()
            : Boolean {
        try {
            val configText = Utils.getClipboard(this)
            if (TextUtils.isEmpty(configText)) {
                toast(R.string.toast_none_data_clipboard)
                return false
            }
            importCustomizeConfig(configText)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * import config from local config file
     */
    private fun importConfigCustomLocal(): Boolean {
        try {
            showFileChooser()
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    private fun importConfigCustomUrlClipboard()
            : Boolean {
        try {
            val url = Utils.getClipboard(this)
            if (TextUtils.isEmpty(url)) {
                toast(R.string.toast_none_data_clipboard)
                return false
            }
            return importConfigCustomUrl(url)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * import config from url
     */
    private fun importConfigCustomUrl(url: String?): Boolean {
        try {
            if (!Utils.isValidUrl(url)) {
                toast(R.string.toast_invalid_url)
                return false
            }
            lifecycleScope.launch(Dispatchers.IO) {
                val configText = try {
                    Utils.getUrlContentWithCustomUserAgent(url)
                } catch (e: Exception) {
                    e.printStackTrace()
                    ""
                }
                launch(Dispatchers.Main) {
                    importCustomizeConfig(configText)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    /**
     * import config from sub
     */
    private fun importConfigViaSub() : Boolean {
//        val dialog = MaterialAlertDialogBuilder(this)
//            .setView(LayoutProgressBinding.inflate(layoutInflater).root)
//            .setCancelable(false)
//            .show()
        binding.pbWaiting.show()

        lifecycleScope.launch(Dispatchers.IO) {
            val count = mainViewModel.updateConfigViaSubAll()
            delay(500L)
            launch(Dispatchers.Main) {
                if (count > 0) {
                    toast(R.string.toast_success)
                    mainViewModel.reloadServerList()
                } else {
                    toast(R.string.toast_failure)
                }
                //dialog.dismiss()
                binding.pbWaiting.hide()
            }
        }
        return true
    }

    /**
     * show file chooser
     */
    private fun showFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        try {
            chooseFileForCustomConfig.launch(Intent.createChooser(intent, getString(R.string.title_file_chooser)))
        } catch (ex: ActivityNotFoundException) {
            toast(R.string.toast_require_file_manager)
        }
    }

    private val chooseFileForCustomConfig = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val uri = it.data?.data
        if (it.resultCode == RESULT_OK && uri != null) {
            readContentFromUri(uri)
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
        RxPermissions(this)
            .request(permission)
            .subscribe {
                if (it) {
                    try {
                        contentResolver.openInputStream(uri).use { input ->
                            importCustomizeConfig(input?.bufferedReader()?.readText())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else
                    toast(R.string.toast_permission_denied)
            }
    }

    private fun keluar() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setMessage(R.string.uwu_exit_message)
            .setCancelable(false) // tidak bisa tekan tombol back
            // jika pilih yes
            .setPositiveButton(R.string.uwu_text_yes) { _, _ ->
                if (mainViewModel.isRunning.value == true) {
                    Utils.stopVService(this)
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

    /**
     * import customize config
     */
    private fun importCustomizeConfig(server: String?) {
        try {
            if (server == null || TextUtils.isEmpty(server)) {
                toast(R.string.toast_none_data)
                return
            }
            if (mainViewModel.appendCustomConfigServer(server)) {
                mainViewModel.reloadServerList()
                toast(R.string.toast_success)
            } else {
                toast(R.string.toast_failure)
            }
            //adapter.notifyItemInserted(mainViewModel.serverList.lastIndex)
        } catch (e: Exception) {
            ToastCompat.makeText(this, "${getString(R.string.toast_malformed_josn)} ${e.cause?.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            return
        }
    }

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

    fun setupPermissions() {
        PermissionGranted()
        if (!checkUsagePermission()) {
            Toast.makeText(this, "Please allow usage access", Toast.LENGTH_SHORT).show()
        }
    }

    fun PermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q && checkSelfPermission("android.permission.READ_PHONE_STATE") != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf("android.permission.READ_PHONE_STATE"), 1)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf("android.permission.READ_PHONE_NUMBERS"), 1)
        }
        return true
    }

    fun checkUsagePermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        var mode: Int = appOps.checkOpNoThrow(
            "android:get_usage_stats", Process.myUid(),
            packageName

        )
        val granted = mode == AppOpsManager.MODE_ALLOWED
        if (!granted) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            return false
        }
        return true
    }

    private fun uwuBanner() {
        if (expandableContent.isExpanded) {
            expandableContent.collapse()
            expandableContent.orientation = ExpandableView.VERTICAL
            expandableContent.setExpansion(false)
            return
        } else {
            expandableContent.expand()
            expandableContent.orientation = ExpandableView.VERTICAL
            expandableContent.setExpansion(true)
        }
    }

    private fun uwuVpnIsRun() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("⚠️ WARNING")
        .setMessage(R.string.uwu_dialog_warn_apply_theme_in_vpn_run)
        .setCancelable(false)
        .setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.cancel()
        }
        .show()
    }

    private fun uwuBannerIsOpen() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("⚠️ WARNING")
        .setMessage(R.string.uwu_dialog_warn_apply_theme_in_banner_open)
        .setCancelable(false)
        .setPositiveButton(R.string.ok) { dialog, _ ->
            dialog.cancel()
        }
        .show()
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

    fun userAssetSetting(view: View) {
        startActivity(Intent(this, UserAssetActivity::class.java))
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

    fun uwuExitApp(view: View) {
        keluar()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.sub_setting -> {
                requestSubSettingActivity.launch(Intent(this, SubSettingActivity::class.java))
            }

            R.id.settings -> {
                startActivity(
                    Intent(this, SettingsActivity::class.java)
                        .putExtra("isRunning", mainViewModel.isRunning.value == true)
                )
            }

            R.id.user_asset_setting -> {
                startActivity(Intent(this, UserAssetActivity::class.java))
            }

            R.id.promotion -> {
                Utils.openUri(this, "${Utils.decode(AppConfig.PromotionUrl)}?t=${System.currentTimeMillis()}")
            }

            R.id.logcat -> {
                startActivity(Intent(this, LogcatActivity::class.java))
            }

            R.id.about -> {
                startActivity(Intent(this, AboutActivity::class.java))
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
