package com.neko.ip.hostchecker

import adrt.ADRTLogCatReader
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.neko.v2ray.ui.BaseActivity
import com.neko.v2ray.util.SoftInputAssist
import com.neko.v2ray.R
import com.google.android.material.appbar.CollapsingToolbarLayout
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL

class HostChecker : BaseActivity() {
    private val TAG = "HostChecker"
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var arrayList: ArrayList<String>
    private lateinit var btnSubmit: Button
    private lateinit var bugHost: EditText
    private lateinit var c: String
    private lateinit var direct: CheckBox
    private lateinit var domain: String
    private lateinit var ipProxy: String
    private lateinit var list: ListView
    private lateinit var method: String
    private lateinit var portProxy: String
    private lateinit var proxy: EditText
    private lateinit var sp: SharedPreferences
    private lateinit var spinner: Spinner
    private lateinit var softInputAssist: SoftInputAssist

    private fun initialize(bundle: Bundle?) {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        softInputAssist = SoftInputAssist(this)
    }

    override fun onPause() {
        softInputAssist.onPause()
        super.onPause()
        Log.v(TAG, "onPause()")
        savePreferences("hostChecker", bugHost.text.toString().trim())
        savePreferences("proxyChecker", proxy.text.toString().trim())
    }

    override fun onStart() {
        charArrayOf(72.toChar(), 111.toChar(), 115.toChar(), 116.toChar(), 32.toChar(), 67.toChar(), 104.toChar(), 101.toChar(), 99.toChar(), 107.toChar(), 101.toChar(), 114.toChar()).toString()
        super.onStart()
    }

    override fun onResume() {
        softInputAssist.onResume()
        super.onResume()
        if (!::sp.isInitialized) {
            sp = PreferenceManager.getDefaultSharedPreferences(this)
        }
        bugHost.setText(sp.getString("hostChecker", ""))
        proxy.setText(sp.getString("proxyChecker", ""))
    }

    override fun onDestroy() {
        softInputAssist.onDestroy()
        super.onDestroy()
    }

    override fun onCreate(bundle: Bundle?) {
        ADRTLogCatReader.onContext(this, "com.neko.v2ray")
        super.onCreate(bundle)
        setContentView(R.layout.uwu_activity_hostchecker)
        initialize(bundle)
        val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        c = defaultSharedPreferences.getString("Rise", "")!!
        list = findViewById<View>(R.id.listLogs) as ListView
        arrayList = ArrayList()
        adapter = ArrayAdapter(applicationContext, R.layout.uwu_logtext, arrayList)
        list.adapter = adapter
        bugHost = findViewById<View>(R.id.editTextUrl) as EditText
        proxy = findViewById<View>(R.id.editTextProxy) as EditText
        proxy.visibility = View.VISIBLE
        spinner = findViewById<View>(R.id.spinnerRequestMethod) as Spinner
        spinner.selectedItem.toString()
        direct = findViewById<View>(R.id.checkBoxDirectRequest) as CheckBox
        direct.setOnClickListener(View.OnClickListener {
            val defaultSharedPreferences2 = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            if (direct.isChecked) {
                val edit = defaultSharedPreferences2.edit()
                edit.putBoolean("Xen", true)
                edit.commit()
                proxy.isEnabled = false
            } else {
                val edit2 = defaultSharedPreferences2.edit()
                edit2.putBoolean("Xen", false)
                edit2.commit()
                proxy.isEnabled = true
            }
        })
        btnSubmit = findViewById<View>(R.id.buttonSearch) as Button
        btnSubmit.setOnClickListener(View.OnClickListener {
            if (bugHost.text.toString() == "") {
                Toast.makeText(applicationContext, "Please Fill The URL", Toast.LENGTH_SHORT).show()
            } else if (direct.isChecked) {
                start()
            } else if (proxy.text.toString() == "") {
                Toast.makeText(applicationContext, "fill the proxy if you want to check or select the direct to check the url", Toast.LENGTH_SHORT).show()
            } else {
                start()
            }
        })
        if (defaultSharedPreferences.getBoolean("Xen", false)) {
            direct.isChecked = true
            proxy.isEnabled = false
        } else {
            direct.isChecked = false
            proxy.isEnabled = true
        }
    }

    fun showMessage(str: String) {
        if (applicationContext != null) {
            Toast.makeText(applicationContext, Html.fromHtml(str), Toast.LENGTH_LONG).show()
        }
    }

    private fun savePreferences(str: String, str2: String) {
        if (!::sp.isInitialized) {
            sp = PreferenceManager.getDefaultSharedPreferences(this)
        }
        val edit = sp.edit()
        edit.putString(str, str2)
        edit.commit()
    }

    fun start() {
        val editable = bugHost.text.toString()
        val obj = spinner.selectedItem.toString()
        btnSubmit.isEnabled = false
        val trim = proxy.text.toString().trim()
        if (trim.contains(":")) {
            val split = trim.split(":".toRegex()).toTypedArray()
            ipProxy = split[0]
            portProxy = split[1]
        } else {
            ipProxy = trim
            portProxy = "80"
        }
        if (direct.isChecked) {
            arrayList.add(StringBuffer().append(StringBuffer().append(StringBuffer().append(obj).append(" - ").toString()).append("URL: http://").toString()).append(editable).toString())
            arrayList.add(StringBuffer().append(StringBuffer().append(editable).append(" - ").toString()).append("Direct").toString())
            adapter.notifyDataSetChanged()
        } else {
            arrayList.add(StringBuffer().append(StringBuffer().append(StringBuffer().append(obj).append(" - ").toString()).append("URL: http://").toString()).append(editable).toString())
            arrayList.add(StringBuffer().append(StringBuffer().append(editable).append(" - ").toString()).append(ipProxy).toString())
            adapter.notifyDataSetChanged()
        }
        Worker2(object : WorkerAction {
            private var a: String? = null
            private var b: String? = null
            private var c: String? = null
            private var conn: HttpURLConnection? = null
            private var d: String? = null
            private var e: String? = null
            private var f: String? = null
            private var g: String? = null
            private var h: String? = null
            private var i: String? = null
            private var j: String? = null
            private var k: String? = null
            var response = ""
            override fun runLast() {
                Log.d(TAG, "DONE")
                if (response.contains("\n")) {
                    for (str in response.split("\n".toRegex()).toTypedArray()) {
                        arrayList.add(str)
                        adapter.notifyDataSetChanged()
                    }
                    arrayList.add("")
                    arrayList.add("Stopped")
                    adapter.notifyDataSetChanged()
                    showMessage("Success")
                    btnSubmit.isEnabled = true
                } else {
                    arrayList.add("")
                    arrayList.add("Stopped")
                    arrayList.add("Please make sure that your connected to (Mobile data)")
                    adapter.notifyDataSetChanged()
                    showMessage("<u><#ffffff>Please connect to internet</#ffffff></u>")
                    btnSubmit.isEnabled = true
                }
            }

            override fun runFirst() {
                try {
                    val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(this@HostChecker.ipProxy, this@HostChecker.portProxy.toInt()))
                    domain = bugHost.text.toString()
                    method = spinner.selectedItem.toString()
                    conn = if (direct.isChecked) {
                        URL(StringBuffer().append("http://").append(domain).toString()).openConnection() as HttpURLConnection
                    } else {
                        URL(StringBuffer().append("http://").append(domain).toString()).openConnection(proxy) as HttpURLConnection
                    }
                    conn!!.requestMethod = method
                    conn!!.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/44.0.2403.130 Safari/537.36")
                    conn!!.readTimeout = 2000
                    conn!!.connectTimeout = 3000
                    conn!!.doInput = true
                    conn!!.connect()
                    for ((key, value) in conn!!.headerFields) {
                        response = StringBuffer().append(response).append(StringBuffer().append(value.toString().replace("[", "").replace("]", "")).append("\n").toString()).toString()
                    }
                } catch (e: Exception) {
                }
            }
        }, this).execute(*arrayOfNulls<Void>(0))
    }
}
