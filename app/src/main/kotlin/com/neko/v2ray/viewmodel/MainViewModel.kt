package com.neko.v2ray.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.neko.v2ray.AngApplication
import com.neko.v2ray.AppConfig
import com.neko.v2ray.AppConfig.ANG_PACKAGE
import com.neko.v2ray.R
import com.neko.v2ray.dto.ProfileItem
import com.neko.v2ray.dto.ServersCache
import com.neko.v2ray.extension.serializable
import com.neko.v2ray.extension.toast
import com.neko.v2ray.fmt.CustomFmt
import com.neko.v2ray.handler.AngConfigManager
import com.neko.v2ray.handler.MmkvManager
import com.neko.v2ray.util.MessageUtil
import com.neko.v2ray.util.SpeedtestUtil
import com.neko.v2ray.util.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Collections

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var serverList = MmkvManager.decodeServerList()
    var subscriptionId: String = MmkvManager.decodeSettingsString(AppConfig.CACHE_SUBSCRIPTION_ID, "").orEmpty()

    //var keywordFilter: String = MmkvManager.MmkvManager.decodeSettingsString(AppConfig.CACHE_KEYWORD_FILTER, "")?:""
    var keywordFilter = ""
    val serversCache = mutableListOf<ServersCache>()
    val isRunning by lazy { MutableLiveData<Boolean>() }
    val updateListAction by lazy { MutableLiveData<Int>() }
    val updateTestResultAction by lazy { MutableLiveData<String>() }
    private val tcpingTestScope by lazy { CoroutineScope(Dispatchers.IO) }

    fun startListenBroadcast() {
        isRunning.value = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getApplication<AngApplication>().registerReceiver(
                mMsgReceiver,
                IntentFilter(AppConfig.BROADCAST_ACTION_ACTIVITY),
                Context.RECEIVER_EXPORTED
            )
        } else {
            getApplication<AngApplication>().registerReceiver(
                mMsgReceiver,
                IntentFilter(AppConfig.BROADCAST_ACTION_ACTIVITY),
                Context.RECEIVER_NOT_EXPORTED
            )
        }
        MessageUtil.sendMsg2Service(getApplication(), AppConfig.MSG_REGISTER_CLIENT, "")
    }

    override fun onCleared() {
        getApplication<AngApplication>().unregisterReceiver(mMsgReceiver)
        tcpingTestScope.coroutineContext[Job]?.cancelChildren()
        SpeedtestUtil.closeAllTcpSockets()
        Log.i(ANG_PACKAGE, "Main ViewModel is cleared")
        super.onCleared()
    }

    fun reloadServerList() {
        serverList = MmkvManager.decodeServerList()
        updateCache()
        updateListAction.value = -1
    }

    fun removeServer(guid: String) {
        serverList.remove(guid)
        MmkvManager.removeServer(guid)
        val index = getPosition(guid)
        if (index >= 0) {
            serversCache.removeAt(index)
        }
    }

    fun appendCustomConfigServer(server: String): Boolean {
        if (server.contains("inbounds")
            && server.contains("outbounds")
            && server.contains("routing")
        ) {
            try {
                val config = CustomFmt.parse(server) ?: return false
                config.subscriptionId = subscriptionId
                val key = MmkvManager.encodeServerConfig("", config)
                MmkvManager.encodeServerRaw(key, server)
                serverList.add(0, key)
//                val profile = ProfileLiteItem(
//                    configType = config.configType,
//                    subscriptionId = config.subscriptionId,
//                    remarks = config.remarks,
//                    server = config.getProxyOutbound()?.getServerAddress(),
//                    serverPort = config.getProxyOutbound()?.getServerPort(),
//                )
                serversCache.add(0, ServersCache(key, config))
                return true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    fun swapServer(fromPosition: Int, toPosition: Int) {
        if (subscriptionId.isEmpty()) {
            Collections.swap(serverList, fromPosition, toPosition)
        } else {
            val fromPosition2 = serverList.indexOf(serversCache[fromPosition].guid)
            val toPosition2 = serverList.indexOf(serversCache[toPosition].guid)
            Collections.swap(serverList, fromPosition2, toPosition2)
        }
        Collections.swap(serversCache, fromPosition, toPosition)
        MmkvManager.encodeServerList(serverList)
    }

    @Synchronized
    fun updateCache() {
        serversCache.clear()
        for (guid in serverList) {
            var profile = MmkvManager.decodeServerConfig(guid) ?: continue
//            var profile = MmkvManager.decodeProfileConfig(guid)
//            if (profile == null) {
//                val config = MmkvManager.decodeServerConfig(guid) ?: continue
//                profile = ProfileLiteItem(
//                    configType = config.configType,
//                    subscriptionId = config.subscriptionId,
//                    remarks = config.remarks,
//                    server = config.getProxyOutbound()?.getServerAddress(),
//                    serverPort = config.getProxyOutbound()?.getServerPort(),
//                )
//                MmkvManager.encodeServerConfig(guid, config)
//            }

            if (subscriptionId.isNotEmpty() && subscriptionId != profile.subscriptionId) {
                continue
            }

            if (keywordFilter.isEmpty() || profile.remarks.contains(keywordFilter)) {
                serversCache.add(ServersCache(guid, profile))
            }
        }
    }

    fun updateConfigViaSubAll(): Int {
        if (subscriptionId.isEmpty()) {
            return AngConfigManager.updateConfigViaSubAll()
        } else {
            val subItem = MmkvManager.decodeSubscription(subscriptionId) ?: return 0
            return AngConfigManager.updateConfigViaSub(Pair(subscriptionId, subItem))
        }
    }

    fun exportAllServer(): Int {
        val serverListCopy =
            if (subscriptionId.isEmpty() && keywordFilter.isEmpty()) {
                serverList
            } else {
                serversCache.map { it.guid }.toList()
            }

        val ret = AngConfigManager.shareNonCustomConfigsToClipboard(
            getApplication<AngApplication>(),
            serverListCopy
        )
        return ret
    }


    fun testAllTcping() {
        tcpingTestScope.coroutineContext[Job]?.cancelChildren()
        SpeedtestUtil.closeAllTcpSockets()
        MmkvManager.clearAllTestDelayResults(serversCache.map { it.guid }.toList())
        //updateListAction.value = -1 // update all

        val serversCopy = serversCache.toList() // Create a copy of the list
        for (item in serversCopy) {
            item.profile.let { outbound ->
                val serverAddress = outbound.server
                val serverPort = outbound.serverPort
                if (serverAddress != null && serverPort != null) {
                    tcpingTestScope.launch {
                        val testResult = SpeedtestUtil.tcping(serverAddress, serverPort.toInt())
                        launch(Dispatchers.Main) {
                            MmkvManager.encodeServerTestDelayMillis(item.guid, testResult)
                            updateListAction.value = getPosition(item.guid)
                        }
                    }
                }
            }
        }
    }

    fun testAllRealPing() {
        MessageUtil.sendMsg2TestService(getApplication(), AppConfig.MSG_MEASURE_CONFIG_CANCEL, "")
        MmkvManager.clearAllTestDelayResults(serversCache.map { it.guid }.toList())
        updateListAction.value = -1 // update all

        val serversCopy = serversCache.toList() // Create a copy of the list
        viewModelScope.launch(Dispatchers.Default) { // without Dispatchers.Default viewModelScope will launch in main thread
            for (item in serversCopy) {
                MessageUtil.sendMsg2TestService(getApplication(), AppConfig.MSG_MEASURE_CONFIG, item.guid)
            }
        }
    }

    fun testCurrentServerRealPing() {
        MessageUtil.sendMsg2Service(getApplication(), AppConfig.MSG_MEASURE_DELAY, "")
    }

    fun subscriptionIdChanged(id: String) {
        if (subscriptionId != id) {
            subscriptionId = id
            MmkvManager.encodeSettings(AppConfig.CACHE_SUBSCRIPTION_ID, subscriptionId)
            reloadServerList()
        }
    }

    fun getSubscriptions(context: Context): Pair<MutableList<String>?, MutableList<String>?> {
        val subscriptions = MmkvManager.decodeSubscriptions()
        if (subscriptionId.isNotEmpty()
            && !subscriptions.map { it.first }.contains(subscriptionId)
        ) {
            subscriptionIdChanged("")
        }
        if (subscriptions.isEmpty()) {
            return null to null
        }
        val listId = subscriptions.map { it.first }.toMutableList()
        listId.add(0, "")
        val listRemarks = subscriptions.map { it.second.remarks }.toMutableList()
        listRemarks.add(0, context.getString(R.string.filter_config_all))

        return listId to listRemarks
    }

    fun getPosition(guid: String): Int {
        serversCache.forEachIndexed { index, it ->
            if (it.guid == guid)
                return index
        }
        return -1
    }

    fun removeDuplicateServer(): Int {
        val serversCacheCopy = mutableListOf<Pair<String, ProfileItem>>()
        for (it in serversCache) {
            val config = MmkvManager.decodeServerConfig(it.guid) ?: continue
            serversCacheCopy.add(Pair(it.guid, config))
        }

        val deleteServer = mutableListOf<String>()
        serversCacheCopy.forEachIndexed { index, it ->
            val outbound = it.second.getKeyProperty()
            serversCacheCopy.forEachIndexed { index2, it2 ->
                if (index2 > index) {
                    val outbound2 = it2.second.getKeyProperty()
                    if (outbound == outbound2 && !deleteServer.contains(it2.first)) {
                        deleteServer.add(it2.first)
                    }
                }
            }
        }
        for (it in deleteServer) {
            MmkvManager.removeServer(it)
        }

        return deleteServer.count()
    }

    fun removeAllServer() {
        if (subscriptionId.isEmpty() && keywordFilter.isEmpty()) {
            MmkvManager.removeAllServer()
        } else {
            val serversCopy = serversCache.toList()
            for (item in serversCopy) {
                MmkvManager.removeServer(item.guid)
            }
        }
    }

    fun removeInvalidServer() {
        if (subscriptionId.isEmpty() && keywordFilter.isEmpty()) {
            MmkvManager.removeInvalidServer("")
        } else {
            val serversCopy = serversCache.toList()
            for (item in serversCopy) {
                MmkvManager.removeInvalidServer(item.guid)
            }
        }
    }

    fun sortByTestResults() {
        data class ServerDelay(var guid: String, var testDelayMillis: Long)

        val serverDelays = mutableListOf<ServerDelay>()
        val serverList = MmkvManager.decodeServerList()
        serverList.forEach { key ->
            val delay = MmkvManager.decodeServerAffiliationInfo(key)?.testDelayMillis ?: 0L
            serverDelays.add(ServerDelay(key, if (delay <= 0L) 999999 else delay))
        }
        serverDelays.sortBy { it.testDelayMillis }

        serverDelays.forEach {
            serverList.remove(it.guid)
            serverList.add(it.guid)
        }

        MmkvManager.encodeServerList(serverList)
    }


    fun copyAssets(assets: AssetManager) {
        val extFolder = Utils.userAssetPath(getApplication<AngApplication>())
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val geo = arrayOf("geosite.dat", "geoip.dat")
                assets.list("")
                    ?.filter { geo.contains(it) }
                    ?.filter { !File(extFolder, it).exists() }
                    ?.forEach {
                        val target = File(extFolder, it)
                        assets.open(it).use { input ->
                            FileOutputStream(target).use { output ->
                                input.copyTo(output)
                            }
                        }
                        Log.i(
                            ANG_PACKAGE,
                            "Copied from apk assets folder to ${target.absolutePath}"
                        )
                    }
            } catch (e: Exception) {
                Log.e(ANG_PACKAGE, "asset copy failed", e)
            }
        }
    }

    fun filterConfig(keyword: String) {
        if (keyword == keywordFilter) {
            return
        }
        keywordFilter = keyword
        MmkvManager.encodeSettings(AppConfig.CACHE_KEYWORD_FILTER, keywordFilter)
        reloadServerList()
    }

    private val mMsgReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when (intent?.getIntExtra("key", 0)) {
                AppConfig.MSG_STATE_RUNNING -> {
                    isRunning.value = true
                }

                AppConfig.MSG_STATE_NOT_RUNNING -> {
                    isRunning.value = false
                }

                AppConfig.MSG_STATE_START_SUCCESS -> {
                    getApplication<AngApplication>().toast(R.string.toast_services_success)
                    isRunning.value = true
                }

                AppConfig.MSG_STATE_START_FAILURE -> {
                    getApplication<AngApplication>().toast(R.string.toast_services_failure)
                    isRunning.value = false
                }

                AppConfig.MSG_STATE_STOP_SUCCESS -> {
                    isRunning.value = false
                }

                AppConfig.MSG_MEASURE_DELAY_SUCCESS -> {
                    updateTestResultAction.value = intent.getStringExtra("content")
                }

                AppConfig.MSG_MEASURE_CONFIG_SUCCESS -> {
                    val resultPair = intent.serializable<Pair<String, Long>>("content") ?: return
                    MmkvManager.encodeServerTestDelayMillis(resultPair.first, resultPair.second)
                    updateListAction.value = getPosition(resultPair.first)
                }
            }
        }
    }
}
