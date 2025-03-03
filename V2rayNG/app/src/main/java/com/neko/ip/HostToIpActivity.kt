package com.neko.ip

import android.animation.*
import android.app.*
import android.content.*
import android.content.res.*
import android.graphics.*
import android.graphics.drawable.*
import android.media.*
import android.net.*
import android.os.*
import android.text.*
import android.text.style.*
import android.util.*
import android.view.*
import android.webkit.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.neko.v2ray.R
import com.neko.v2ray.ui.BaseActivity
import com.neko.v2ray.util.SoftInputAssist
import org.json.*

class HostToIpActivity : BaseActivity() {

    private val anichan2 = mutableMapOf<String, Any>()
    private var quote: String = ""
    private var hostIP = mutableMapOf<String, Any>()

    private lateinit var linear1: LinearLayout
    private lateinit var linear7: Button
    private lateinit var linear9: LinearLayout
    private lateinit var linear10: LinearLayout
    private lateinit var edittext1: EditText
    private lateinit var textview2: TextView
    private lateinit var textview1: TextView
    private lateinit var textview9: TextView
    private lateinit var textview10: TextView
    private lateinit var textview11: TextView
    private lateinit var textview12: TextView
    private lateinit var textview13: TextView

    private lateinit var anichan: RequestNetwork
    private lateinit var softInputAssist: SoftInputAssist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.host_to_ip)
        initialize(savedInstanceState)
        softInputAssist = SoftInputAssist(this)
    }

    private fun initialize(savedInstanceState: Bundle?) {
        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val toolbarLayout: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        linear1 = findViewById(R.id.linear1)
        linear7 = findViewById(R.id.linear7)
        linear9 = findViewById(R.id.linear9)
        linear10 = findViewById(R.id.linear10)
        edittext1 = findViewById(R.id.edittext1)
        textview2 = findViewById(R.id.textview2)
        textview1 = findViewById(R.id.textview1)
        textview9 = findViewById(R.id.textview9)
        textview10 = findViewById(R.id.textview10)
        textview11 = findViewById(R.id.textview11)
        textview12 = findViewById(R.id.textview12)
        textview13 = findViewById(R.id.textview13)
        anichan = RequestNetwork(this)

        val anichanRequestListener = object : RequestNetwork.RequestListener {
            override fun onResponse(_param1: String, _param2: String, _param3: HashMap<String, Any>) {
                val _tag = _param1
                val _response = _param2
                val _responseHeaders = _param3
        
                val hostIP: HashMap<String, Any> = Gson().fromJson(_response, object : TypeToken<HashMap<String, Any>>() {}.type)
        
                if (hostIP["status"].toString() == "success") {
                    textview1.text = "IP:      ${hostIP["query"]}"
                    textview9.text = "Country:      ${hostIP["country"]}"
                    textview10.text = "ISP Provider:     ${hostIP["org"]}"
                    textview11.text = "ISP Provider 2:     ${hostIP["isp"]}"
                    textview12.text = "ISP Provider 3:     ${hostIP["as"]}"
                    textview13.text = "City:     ${hostIP["city"]}"
                } else {
                    Toast.makeText(this@HostToIpActivity, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onErrorResponse(_param1: String, _param2: String) {
                val _tag = _param1
                val _message = _param2
                Toast.makeText(this@HostToIpActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        }

        linear7.setOnClickListener {
            if (edittext1.text.toString().isEmpty()) {
                Toast.makeText(this@HostToIpActivity, "Empty", Toast.LENGTH_SHORT).show()
            } else {
                anichan.startRequestNetwork(
                    RequestNetworkController.GET,
                    "http://ip-api.com/json/${edittext1.text}",
                    "A",
                    anichanRequestListener
                )
                Toast.makeText(this@HostToIpActivity, "Pls wait..", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun elevation(view: View, num: Double) {
        view.elevation = num.toFloat()
    }

    fun advancedCorners(view: View, color: String, n1: Double, n2: Double, n3: Double, n4: Double) {
        val gd = GradientDrawable()
        gd.setColor(Color.parseColor(color))
        gd.cornerRadii = floatArrayOf(n1.toFloat(), n1.toFloat(), n2.toFloat(), n2.toFloat(), n4.toFloat(), n4.toFloat(), n3.toFloat(), n3.toFloat())
        view.background = gd
    }

    override fun onResume() {
        softInputAssist.onResume()
        super.onResume()
    }

    override fun onPause() {
        softInputAssist.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        softInputAssist.onDestroy()
        super.onDestroy()
    }
}
