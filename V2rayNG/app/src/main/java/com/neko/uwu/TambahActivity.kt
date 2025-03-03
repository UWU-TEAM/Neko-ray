package com.neko.uwu

import android.content.Intent
import android.database.Cursor
import android.view.Menu
import android.view.MenuItem
import android.os.Bundle
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.neko.v2ray.R
import com.neko.v2ray.ui.BaseActivity
import com.neko.v2ray.ui.MainActivity
import com.neko.v2ray.util.SoftInputAssist

import com.neko.imageslider.ImageSlider
import com.neko.imageslider.constants.ActionTypes
import com.neko.imageslider.constants.AnimationTypes
import com.neko.imageslider.constants.ScaleTypes
import com.neko.imageslider.interfaces.ItemChangeListener
import com.neko.imageslider.interfaces.ItemClickListener
import com.neko.imageslider.interfaces.TouchListener
import com.neko.imageslider.models.SlideModel

class TambahActivity : BaseActivity() {
    private lateinit var etName: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var etTgl: TextInputEditText
    private lateinit var etHobi: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var arrID: ArrayList<String>
    private lateinit var softInputAssist: SoftInputAssist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        softInputAssist = SoftInputAssist(this)

        etName = findViewById(R.id.et_name)
        etAge = findViewById(R.id.et_age)
        etTgl = findViewById(R.id.et_tgl)
        etHobi = findViewById(R.id.et_hobi)
        etEmail = findViewById(R.id.et_email)

        val imageSlider = findViewById<ImageSlider>(R.id.image_slider) // init imageSlider
        val imageList = ArrayList<SlideModel>() // Create image list
        imageList.add(SlideModel(R.drawable.uwu_banner_image_3, ""))
        imageList.add(SlideModel(R.drawable.uwu_banner_image_5, ""))
        imageList.add(SlideModel(R.drawable.uwu_banner_image_6, ""))
        imageList.add(SlideModel(R.drawable.uwu_banner_image_7, ""))
        imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)
        imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT)
        imageSlider.setItemClickListener(object : ItemClickListener {
            override fun onItemSelected(position: Int) {
                // You can listen here.
                println("normal")
            }
            override fun doubleClick(position: Int) {
                // Do not use onItemSelected if you are using a double click listener at the same time.
                // Its just added for specific cases.
                // Listen for clicks under 250 milliseconds.
                println("its double")
            }
        })
        imageSlider.setItemChangeListener(object : ItemChangeListener {
            override fun onItemChanged(position: Int) {
                //println("Pos: " + position)
            }
        })
        imageSlider.setTouchListener(object : TouchListener {
            override fun onTouched(touched: ActionTypes, position: Int) {
                if (touched == ActionTypes.DOWN){
                    imageSlider.stopSliding()
                } else if (touched == ActionTypes.UP ) {
                    imageSlider.startSliding(3000)
                }
            }
        })
        infoPendaftaran()
    }

    private fun saveDataUwu(): Boolean {
        val getName = etName.text.toString()
        val getAge = etAge.text.toString()
        val getTgl = etTgl.text.toString()
        val getHobi = etHobi.text.toString()
        val getEmail = etEmail.text.toString()

        when {
            getName.trim().isEmpty() -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setMessage(getString(R.string.uwu_name_dialog))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
            }
            getAge.trim().isEmpty() -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setMessage(getString(R.string.uwu_age_dialog))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
            }
            getTgl.trim().isEmpty() -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setMessage(getString(R.string.uwu_date_dialog))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
            }
            getHobi.trim().isEmpty() -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setMessage(getString(R.string.uwu_hobi_dialog))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
            }
            getEmail.trim().isEmpty() -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setMessage(getString(R.string.uwu_email_dialog))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
            }
            else -> {
                val myDB = MyDatabaseHelper(this@TambahActivity)
                val eksekusi = myDB.tambahDatabase(getName, getAge, getTgl, getHobi, getEmail)
                val cursor: Cursor? = myDB.bacaSemuaData()

                if (eksekusi == -1L) {
                    Toast.makeText(this@TambahActivity, "Failed", Toast.LENGTH_SHORT).show()
                    etName.requestFocus()
                } else if (cursor == null || cursor.count == 0) {
                    // null
                } else {
                    try {
                        val db = myDB.readableDatabase
                        val query = "SELECT * FROM nekoray"
                        val rs: Cursor = db.rawQuery(query, null)

                        if (rs.moveToFirst()) {
                            val arrID = rs.getString(rs.getColumnIndexOrThrow("id"))
                            val name = rs.getString(rs.getColumnIndexOrThrow("name"))
                            val posisi = 1

                            val intent = Intent(this@TambahActivity, MainActivity::class.java).apply {
                                putExtra("varID", arrID)
                                putExtra("varName", name)
                                putExtra("varPosisi", posisi)
                            }
                            startActivity(intent)
                        }
                        rs.close()
                    } finally {
                        cursor?.close()
                    }
                    Toast.makeText(this@TambahActivity, "Succeed", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_data_uwu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.save_data_uwu -> {
            saveDataUwu()
            true
        }
        else -> super.onOptionsItemSelected(item)
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

    private fun infoPendaftaran() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setMessage(R.string.uwu_daftar_message)
            .setCancelable(false)
            .setPositiveButton(R.string.ok, null)
            .show()
    }
}
