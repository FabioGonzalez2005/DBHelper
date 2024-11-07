package org.iesharia.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory? = null) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NAME_COL + " TEXT," +
                AGE_COL + " TEXT" + ")")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addUser(name: String, age: String) {
        val values = ContentValues().apply {
            put(NAME_COL, name)
            put(AGE_COL, age)
        }
        writableDatabase.insert(TABLE_NAME, null, values).also { close() }
    }

    fun deleteUser(name: String, age: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$NAME_COL = ? AND $AGE_COL = ?", arrayOf(name, age)).also { db.close() }
    }

    fun updateUser(id: Int, newName: String, newAge: String): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(NAME_COL, newName)
            put(AGE_COL, newAge)
        }
        return db.update(TABLE_NAME, values, "$ID_COL = ?", arrayOf(id.toString())).also { db.close() }
    }

    fun getUsers(): List<Triple<Int, String, String>> {
        val users = mutableListOf<Triple<Int, String, String>>()
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_COL))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(NAME_COL))
                val age = cursor.getString(cursor.getColumnIndexOrThrow(AGE_COL))
                users.add(Triple(id, name, age))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return users
    }

    companion object {
        const val DATABASE_NAME = "nombres"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "name_table"
        const val ID_COL = "id"
        const val NAME_COL = "nombre"
        const val AGE_COL = "edad"
    }
}
