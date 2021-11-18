package mx.tecnm.tepic.ladm_u3_practica1

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class BaseDatos(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) { //LA INFORMACION SE GUARDA DE MANERA LOCAL
    //context: El ACTIVITY que llamara la conexion
    //name : El nombre del ARCHIVO de la BASE DE DATOS
    //factory : objeto cursor para navegar entre tuplas resultado ACTUALMENTE OBSOLETO
    //Version: la version de la BASE DE DATOS ID, NOMBRE, DOM = Iversion
    //                      mas adelante agregar EDAD = 2versiones

    override fun onCreate(db: SQLiteDatabase) {//SQliteDatabase (SIRVE PARA REALIZAR TRANSACCIONES)
        //AMBOS METODOS SIRVEN PARA CONSTRUIR LA ESTRUCTURA
        db.execSQL("CREATE TABLE EVENTO(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, LUGAR VARCHAR(300), HORA VARCHAR(7), FECHA VARCHAR(10), DESCRIPCION VARCHAR(200))")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //TAMBIEN SIRVE PARA CONSTRUIR ESTRUCTURA DE BD, PERO CONCRETAMENTE ACTUALIZACIONES
        //UPDATE = ACTUALIZACION MENOR = CAMBIO EN DATOS ALMACENADOS
        //UPGRADE = ACTUALIZACION MAYOR = CAMBIOS EN ESTRUCTURA DE TABLAS
    }
}