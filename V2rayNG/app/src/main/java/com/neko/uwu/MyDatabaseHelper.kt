package com.neko.uwu

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val ctx: Context = context

    companion object {
        private const val DATABASE_NAME = "neko.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "nekoray"
        private const val FIELD_ID = "id"
        private const val FIELD_NAME = "name"
        private const val FIELD_AGE = "age"
        private const val FIELD_TGL = "tgl"
        private const val FIELD_HOBI = "hobi"
        private const val FIELD_EMAIL = "email"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val query = "CREATE TABLE $TABLE_NAME (" +
                "$FIELD_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$FIELD_NAME TEXT, " +
                "$FIELD_AGE TEXT, " +
                "$FIELD_TGL TEXT, " +
                "$FIELD_HOBI TEXT, " +
                "$FIELD_EMAIL TEXT ); "

        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun tambahDatabase(name: String, age: String, tgl: String, hobi: String, email: String): Long {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(FIELD_NAME, name)
        cv.put(FIELD_AGE, age)
        cv.put(FIELD_TGL, tgl)
        cv.put(FIELD_HOBI, hobi)
        cv.put(FIELD_EMAIL, email)

        return db.insert(TABLE_NAME, null, cv)
    }

    fun ubahDatabase(id: String, name: String, age: String, tgl: String, hobi: String, email: String): Long {
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(FIELD_NAME, name)
        cv.put(FIELD_AGE, age)
        cv.put(FIELD_TGL, tgl)
        cv.put(FIELD_HOBI, hobi)
        cv.put(FIELD_EMAIL, email)

        return db.update(TABLE_NAME, cv, "id = ?", arrayOf(id)).toLong()
    }

    fun hapusDatabase(id: String): Long {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "id = ?", arrayOf(id)).toLong()
    }

    fun bacaSemuaData(): Cursor? {
        val query = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase

        return db?.rawQuery(query, null)
    }
}
