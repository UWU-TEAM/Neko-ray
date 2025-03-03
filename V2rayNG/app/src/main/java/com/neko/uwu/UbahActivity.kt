package com.neko.uwu

import android.content.Context
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.os.Bundle
import android.widget.Button
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import android.widget.Toast
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

class UbahActivity : BaseActivity() {
    private lateinit var etName: TextInputEditText
    private lateinit var etAge: TextInputEditText
    private lateinit var etTgl: TextInputEditText
    private lateinit var etHobi: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var getID: String
    private lateinit var getName: String
    private lateinit var getAge: String
    private lateinit var getTgl: String
    private lateinit var getHobi: String
    private lateinit var getEmail: String
    private lateinit var softInputAssist: SoftInputAssist
    private var getPosisi: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubah)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        softInputAssist = SoftInputAssist(this)

        val terima = intent
        getID = terima.getStringExtra("varID") ?: ""
        getName = terima.getStringExtra("varName") ?: ""
        getAge = terima.getStringExtra("varAge") ?: ""
        getTgl = terima.getStringExtra("varTgl") ?: ""
        getHobi = terima.getStringExtra("varHobi") ?: ""
        getEmail = terima.getStringExtra("varEmail") ?: ""
        getPosisi = terima.getIntExtra("varPosisi", -1)

        etName = findViewById(R.id.et_name)
        etAge = findViewById(R.id.et_age)
        etTgl = findViewById(R.id.et_tgl)
        etHobi = findViewById(R.id.et_hobi)
        etEmail = findViewById(R.id.et_email)

        etName.setText(getName)
        etAge.setText(getAge)
        etTgl.setText(getTgl)
        etHobi.setText(getHobi)
        etEmail.setText(getEmail)

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
    }

    private fun updateDataUwu(): Boolean {
        val txtName = etName.text.toString()
        val txtAge = etAge.text.toString()
        val txtTgl = etTgl.text.toString()
        val txtHobi = etHobi.text.toString()
        val txtEmail = etEmail.text.toString()

        when {
            txtName.trim().isEmpty() -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setMessage(getString(R.string.uwu_name_dialog))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
            }
            txtAge.trim().isEmpty() -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setMessage(getString(R.string.uwu_age_dialog))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
            }
            txtTgl.trim().isEmpty() -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setMessage(getString(R.string.uwu_date_dialog))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
            }
            txtHobi.trim().isEmpty() -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setMessage(getString(R.string.uwu_hobi_dialog))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
            }
            txtEmail.trim().isEmpty() -> {
                val builder = MaterialAlertDialogBuilder(this)
                builder.setMessage(getString(R.string.uwu_email_dialog))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .show()
            }
            else -> {
                val myDB = MyDatabaseHelper(this@UbahActivity)
                val eksekusi = myDB.ubahDatabase(getID, txtName, txtAge, txtTgl, txtHobi, txtEmail)

                if (eksekusi == -1L) {
                    Toast.makeText(this@UbahActivity, "Failed", Toast.LENGTH_SHORT).show()
                    etName.requestFocus()
                } else {
                    Toast.makeText(this@UbahActivity, "Succeed", Toast.LENGTH_SHORT).show()
                    MainActivity.posisiData = getPosisi
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
            updateDataUwu()
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
}
