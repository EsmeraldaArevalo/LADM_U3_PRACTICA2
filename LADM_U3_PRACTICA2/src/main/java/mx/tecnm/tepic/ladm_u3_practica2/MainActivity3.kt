package mx.tecnm.tepic.ladm_u3_practica2

import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main3.button
import kotlinx.android.synthetic.main.activity_main3.button2
import kotlinx.android.synthetic.main.activity_main3.button3
import kotlinx.android.synthetic.main.activity_main3.button8
import kotlinx.android.synthetic.main.activity_main3.lista
import java.util.*

class MainActivity3 : AppCompatActivity() {

    var baseDatos =BaseDatos(this,"basedatos1",null,1)
    var id = ""

    var listaID = ArrayList<String>()
    var idSeleccionadoEnLista = -1

    //VARIABLES PARA FECHA
    val d = Date()
    val s: CharSequence = DateFormat.format("dd/MM/yyyy", d.getTime())
    val di:CharSequence = DateFormat.format("dd", d.getTime())
    val me:CharSequence = DateFormat.format("MM", d.getTime())
    val añ:CharSequence = DateFormat.format("yyyy", d.getTime())
    val fechahoy: CharSequence = DateFormat.format("dd/MM/yyyy", d.getTime())

    val dia = di.toString().toInt() + 1
    val mes = me.toString().toInt()
    val año = añ.toString().toInt()

    val fechamañana = dia.toString()+ s
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        button.setOnClickListener {
            try {
                var tran = baseDatos.readableDatabase
                var eventos = ArrayList<String>()

                var resultados = tran.query(
                    "EVENTO",
                    arrayOf("ID", "LUGAR", "HORA", "FECHA", "DESCRIPCION"),
                    "FECHA=?",
                    arrayOf(fechahoy.toString()),
                    null,
                    null,
                    null
                )

                listaID.clear()

                if(resultados.moveToFirst()){
                    do{
                        var concatenacion = "ID: ${resultados.getInt(0)}\nTITULO:" + " ${resultados.getString(1)}\nHORA:" + " ${resultados.getString(2)}\nFECHA:" + "${resultados.getString(3)}\nCONTENIDO:" +" ${resultados.getString(4)}"
                        eventos.add(concatenacion)
                        listaID.add(resultados.getInt(0).toString())

                    }while (resultados.moveToNext())

                }else{
                    eventos.add("NO EXISTEN NOTAS INSERTADOS CON ESA FECHA")
                }

                lista.adapter = ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1, eventos)

                this.registerForContextMenu(lista)

                lista.setOnItemClickListener { parent, view, i, id ->
                    idSeleccionadoEnLista = i
                    Toast.makeText(this, "Se seleccionó elemento", Toast.LENGTH_SHORT).show()
                }
                tran.close()

            }catch (e: SQLiteException){
                mensaje("ERROR : "+e.message!!)
            }

        }

        button8.setOnClickListener {
            try {
                var tran = baseDatos.readableDatabase
                var eventos = ArrayList<String>()

                var resultados = tran.query(
                    "EVENTO",
                    arrayOf("ID", "LUGAR", "HORA", "FECHA", "DESCRIPCION"),
                    "FECHA=?",
                    arrayOf(fechamañana),
                    null,
                    null,
                    null
                )

                listaID.clear()

                if(resultados.moveToFirst()){
                    do{
                        var concatenacion = "ID: ${resultados.getInt(0)}\nTITULO:" + " ${resultados.getString(1)}\nHORA:" + " ${resultados.getString(2)}\nFECHA:" + "${resultados.getString(3)}\nCONTENIDO:" +" ${resultados.getString(4)}"
                        eventos.add(concatenacion)
                        listaID.add(resultados.getInt(0).toString())

                    }while (resultados.moveToNext())

                }else{
                    eventos.add("NO HAY EVENTOS INSERTADOS CON ESA FECHA")
                }

                lista.adapter = ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1, eventos)

                this.registerForContextMenu(lista)

                lista.setOnItemClickListener { parent, view, i, id ->
                    idSeleccionadoEnLista = i
                    Toast.makeText(this, "Se seleccionó elemento", Toast.LENGTH_SHORT).show()
                }
                tran.close()

            }catch (e: SQLiteException){
                mensaje("ERROR : "+e.message!!)
            }
        }

        button2.setOnClickListener {
            FFinal.visibility
            FInicial.visibility
        }
        button3.setOnClickListener {
            finish()
            val intento1 = Intent(this, MainActivity::class.java)
            startActivity(intento1)

        }
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){
                    d,i-> d.dismiss()
            }
            .show()

    }
}