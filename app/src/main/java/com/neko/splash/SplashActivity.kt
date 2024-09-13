package com.neko.splash

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.neko.v2ray.R
import com.neko.v2ray.ui.BaseActivity
import com.neko.v2ray.ui.MainActivity
import com.neko.uwu.*

class SplashActivity : BaseActivity() {

    private lateinit var myDB: MyDatabaseHelper
    private lateinit var arrID: ArrayList<String>
    private lateinit var arrUsername: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.uwu_activity_splash)
        myDB = MyDatabaseHelper(this)
        
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        Handler(Looper.getMainLooper()).postDelayed({
            val cursor: Cursor? = myDB.bacaSemuaData()
            if (cursor == null || cursor.count == 0) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                try {
                    val db = myDB.readableDatabase
                    val query = "SELECT * FROM nekoray"
                    val rs: Cursor = db.rawQuery(query, null)

                    if (rs.moveToFirst()) {
                        val arrID = rs.getString(rs.getColumnIndexOrThrow("id"))
                        val username = rs.getString(rs.getColumnIndexOrThrow("username"))
                        val posisi = 1

                        val intent = Intent(this@SplashActivity, MainActivity::class.java).apply {
                            putExtra("varID", arrID)
                            putExtra("varUsername", username)
                            putExtra("varPosisi", posisi)
                        }
                        startActivity(intent)
                    }
                    rs.close()
                } finally {
                    cursor?.close()
                }
            }
            finish()
        }, 1000)
    }
}
