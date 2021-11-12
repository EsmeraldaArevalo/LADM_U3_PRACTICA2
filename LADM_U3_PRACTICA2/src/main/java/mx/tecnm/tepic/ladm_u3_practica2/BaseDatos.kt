package mx.tecnm.tepic.ladm_u3_practica2

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE EVENTO(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, LUGAR VARCHAR(300), HORA VARCHAR(7), FECHA VARCHAR(10), CONTENIDO VARCHAR(200))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }
}