package mx.tecnm.tepic.ladm_u3_practica2

import android.content.ContentValues
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.text.format.DateFormat
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var baseDatos = BaseDatos(this,"basedatos1",null,1)
    var listaID = ArrayList<String>()
    var idSeleccionadoEnLista = -1

    var baseRemota = FirebaseFirestore.getInstance();
    var datos = java.util.ArrayList<String>()
    var ListaID2 = java.util.ArrayList<String>()

    //VARIABLES PARA FECHA
    val d = Date()
    val s: CharSequence = DateFormat.format("dd/MM/yyyy", d.getTime())
    val di:CharSequence = DateFormat.format("dd", d.getTime())
    val me:CharSequence = DateFormat.format("MM", d.getTime())
    val añ:CharSequence = DateFormat.format("yyyy", d.getTime())

    val dia = di.toString().toInt()
    val mes = me.toString().toInt()
    val año = añ.toString().toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            insertar()
        }
        button2.setOnClickListener {
            consultar()
        }

        button8.setOnClickListener {
            eliminarporLugar()
        }

        button3.setOnClickListener {
            sincronizar()
        }
        cargarContactos()
    }

    private fun eliminarporLugar() {

        if (titulo.text.toString() != "") {
            try {

                var tran = baseDatos.readableDatabase

                var resultados = tran.query(
                    "EVENTO",
                    arrayOf("ID", "LUGAR", "HORA", "FECHA", "DESCRIPCION"),
                    "LUGAR=?",
                    arrayOf(titulo.text.toString()),
                    null,
                    null,
                    null
                )

                if (resultados.moveToFirst()) {

                    if (titulo.text.toString() == resultados.getString(1)) {
                        try {
                            var trans = baseDatos.writableDatabase

                            var resultado = trans.delete("EVENTO", "TITULO=?",
                                arrayOf(titulo.text.toString()))

                            if(resultado == 0){
                                mensaje("NO FUE POSIBLE ELIMINAR")
                            }else{
                                mensaje("Se logro eliminar con exito el Evento con titulo en: ${titulo.text}")
                            }
                            trans.close()
                            cargarContactos()
                        }catch (e: SQLiteException){
                            mensaje(e.message!!)
                        }
                    }
                } else {
                    mensaje("NO SE ENCONTRARON EVENTOS CON ESE TITULO")
                }
                tran.close()
            } catch (e: SQLiteException) {

            }
        }

    }

    private fun sincronizar() {
        datos.clear()
        baseRemota.collection("evento").addSnapshotListener {
                querySnapshot, firebaseFirestoreException ->
                    if(firebaseFirestoreException !=null)
                    {
                        mensaje("NO FUE POSIBLE RECUPERAR DE LA NUBE")
                        return@addSnapshotListener
                    }

                    var cadena= ""
                    for(registro in querySnapshot!!)
                    {
                        cadena=registro.id
                        datos.add(cadena)
                    }

                try {
                    var trans=baseDatos.readableDatabase
                    var respuesta=trans.query("EVENTO", arrayOf("*"),null,null,null,null,null)
                    if(respuesta.moveToFirst())
                    {
                        do {
                            if(datos.contains(respuesta.getString(0)))
                            {
                                baseRemota.collection("evento")
                                    .document(respuesta!!.getString(0))
                                    .update(
                                        "LUGAR",respuesta!!.getString(1),
                                        "HORA",respuesta!!.getString(2),
                                        "FECHA",respuesta!!.getString(3),
                                        "DESCRIPCION", respuesta!!.getString(4)
                                    )
                                    .addOnSuccessListener {
                                    }.addOnFailureListener {
                                        AlertDialog.Builder(this)
                                            .setTitle("ERROR")
                                            .setMessage("FALLA EN ACTUALIZACION DE DATOS\n${it.message!!}")
                                            .setPositiveButton("OK"){d,i->}
                                            .show()
                                    }
                            }
                            else
                            {
                                var datosInsertar = hashMapOf(
                                    "LUGAR" to respuesta!!.getString(1),
                                    "HORA" to respuesta!!.getString(2),
                                    "FECHA" to respuesta!!.getString(3),
                                    "CONTENIDO" to respuesta!!.getString(4)
                                )
                                baseRemota.collection("evento").document("${respuesta!!.getString(0)}")
                                    .set(datosInsertar as Any)
                                    .addOnSuccessListener {

                                    }
                                    .addOnFailureListener{
                                        mensaje("ERROR DE CONEXION\n${it.message!!}")
                                    }
                            }
                        }while(respuesta.moveToNext())
                    }
                    else{
                        datos.add("NO HAY EVENTOS A INSERTAR")
                    }
                    trans.close()
                } catch (e: SQLiteException) {
                    mensaje("ERROR DE SINCRONIZACION: " + e.message!!)
                }
            eliminandoR()
        }
        mensaje("SINCRONIZACIÓN EXITOSA")
    }


    private fun eliminandoR() {
        var eliminadoR= datos.subtract(listaID)
        if(eliminadoR.isEmpty())
        {

        }
        else
        {
            eliminadoR.forEach(){
                baseRemota.collection("evento")
                    .document(it)
                    .delete()
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener{
                        mensaje("FALLO DE SINCRONIZACIÓN EN ELIMINADO\n${it.message!!}")
                    }
            }
        }
    }

    private fun consultar(){
        mensaje("LA BUSQUEDA SE REALIZARA CON LO PRIMERO QUE SE INSERTE (ID,TITULO O DESCRIPCIÓN")

        var  opcion = ""

        if (idp.text.toString() == ""){
            if (titulo.text.toString() == ""){
                if (contenido.text.toString() == ""){
                    mensaje("NO SE ENCONTRARON RESULTADOS PARA REALIZAR LA CONSULTA, INGRESE TITULO O CONTENIDO PARA LA BUSQUEDA")
                }
                else if (contenido.text.toString() != "") {

                    try {

                        var tran = baseDatos.readableDatabase

                        var resultados = tran.query(
                            "EVENTO",
                            arrayOf("ID", "LUGAR", "HORA", "FECHA", "DESCRIPCION"),
                            "DESCRIPCION=?",
                            arrayOf(contenido.text.toString()),
                            null,
                            null,
                            null
                        )//REALIZAR UN SELECT

                        if (resultados.moveToFirst()) {
                            //TERMINO DE CLASE
                            var cadena =
                                "ID: " + resultados.getInt(0) + "\nTITULO: " + resultados.getString(1) + "\nHORA: " + resultados.getString(
                                    2
                                ) + "\nFECHA: " + resultados.getString(3) + "\nCONTENIDO: " + resultados.getString(
                                    4
                                )

                            mensaje(cadena)
                        } else {
                            mensaje("NO SE ENCONTRARON RESULTADOS")
                        }
                        tran.close()
                    } catch (e: SQLiteException) {

                    }
                }
            }
            else if (titulo.text.toString() != "") {
                try {

                    var tran = baseDatos.readableDatabase

                    var resultados = tran.query(
                        "EVENTO",
                        arrayOf("ID", "LUGAR", "HORA", "FECHA", "DESCRIPCION"),
                        "LUGAR=?",
                        arrayOf(titulo.text.toString()),
                        null,
                        null,
                        null
                    )

                    if (resultados.moveToFirst()) {

                        var cadena =
                            "ID: " + resultados.getInt(0) + "\nTITULO: " + resultados.getString(1) + "\nHORA: " + resultados.getString(
                                2
                            ) + "\nFECHA: " + resultados.getString(3) + "\nCONTENIDO: " + resultados.getString(
                                4
                            )

                        mensaje(cadena)
                    } else {
                        mensaje("NO SE ENCONTRARON RESULTADOS")
                    }
                    tran.close()
                } catch (e: SQLiteException) {

                }
            }
        }
        else if (idp.text.toString() != "") {
            try {

                var tran = baseDatos.readableDatabase

                var resultados = tran.query(
                    "EVENTO",
                    arrayOf("ID", "LUGAR", "HORA", "FECHA", "DESCRIPCION"),
                    "ID=?",
                    arrayOf(idp.text.toString()),
                    null,
                    null,
                    null
                )

                if (resultados.moveToFirst()) {

                    var cadena =
                        "ID: " + resultados.getInt(0) + "\nTITULO: " + resultados.getString(1) + "\nHORA: " + resultados.getString(
                            2
                        ) + "\nFECHA: " + resultados.getString(3) + "\nCONTENIDO: " + resultados.getString(
                            4
                        )

                    mensaje(cadena)
                } else {
                    mensaje("NO SE ENCONTRARON RESULTADOS")
                }
                tran.close()
            } catch (e: SQLiteException) {

            }
        }
    }

    private fun insertar(){

        if (titulo.text.toString() == ""){
            mensaje("NO HA INGRESADO TITULO")
        }
        if (hora.text.toString() == ""){
            mensaje("NO HA INGRESADO HORA")
        }
        if (fecha.text.toString() == "" ){
            mensaje("NO HA INGRESADO FECHA")
        }
        if (contenido.text.toString() == ""){
            mensaje("NO HA INGRESADO CONTENIDO")
        }

        else if(titulo.text.toString() != "" || fecha.text.toString() != "" || contenido.text.toString() != "" || hora.text.toString() != "" ){
            try {

                var trans = baseDatos.writableDatabase
                var variables = ContentValues()

                variables.put("LUGAR", titulo.text.toString())
                variables.put("HORA", hora.text.toString())
                variables.put("FECHA", fecha.text.toString())
                variables.put("DESCRIPCION", contenido.text.toString())


                var respuesta = trans.insert("EVENTO", null, variables)
                if (respuesta == -1L) {
                    mensaje("NO FUE POSIBLE INSERTAR")
                } else {
                    mensaje("SE INSERTO CON EXITO")
                    LimpiarCampos()
                }
                trans.close()
            } catch (e: SQLiteException) {
                mensaje(e.message!!)
            }
            cargarContactos()
        }

    }
    private fun cargarContactos(){
        try {
            var trans = baseDatos.readableDatabase
            var eventos = ArrayList<String>()

            var respuesta = trans.query("EVENTO", arrayOf("*"),null,
                    null,null,null,null)

            listaID.clear()

            if(respuesta.moveToFirst()){
                do{
                    var concatenacion = "ID: ${respuesta.getInt(0)}\nTITULO:" + " ${respuesta.getString(1)}\nHORA:" + " ${respuesta.getString(2)}\nFECHA:" + "${respuesta.getString(3)}\nCONTENIDO:" +" ${respuesta.getString(4)}"
                    eventos.add(concatenacion)
                    listaID.add(respuesta.getInt(0).toString())

                }while (respuesta.moveToNext())

            }else{
                eventos.add("NO EXISTEN EVENTOS")
            }

            lista.adapter = ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1, eventos)

            this.registerForContextMenu(lista)

            lista.setOnItemClickListener { parent, view, i, id ->
                idSeleccionadoEnLista = i
                Toast.makeText(this, "Se seleccionó elemento", Toast.LENGTH_SHORT).show()
            }
            trans.close()

        }catch (e: SQLiteException){
            mensaje("ERROR : "+e.message!!)
        }
    }
    private fun LimpiarCampos(){
        idp.setText("")
        titulo.setText("")
        hora.setText("")
        fecha.setText("")
        contenido.setText("")

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

    override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        var inflaterOB = menuInflater
        inflaterOB.inflate(R.menu.menu,menu!!)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        if (idSeleccionadoEnLista == -1){
            mensaje("Es necesario abrir un item para ACTUALIZAR/BORRAR(POR FECHA EN ESPECIFICO)")
            when(item.itemId){
                R.id.itemeliminarfpasadas ->{
                    val fechaCompleta = dia.toString()+ s.toString()
                    AlertDialog.Builder(this)
                        .setTitle("ATENCION")
                        .setMessage("SE ELEMINARAN TODOS LOS EVENTOS CON FECHAS PASADAS A HOY: "+fechaCompleta+"")
                        .setPositiveButton("ELIMINAR"){d,i->
                            eliminartodasfechaspasadas()
                        }
                        .setNeutralButton("NO"){ d,i-> }
                        .show()
                }
                R.id.itemconsultarporfecha ->{
                    var  itent = Intent(this,MainActivity3::class.java)
                    startActivity(itent)
                    cargarContactos()
                }
                R.id.itemsalir ->{

                }
            }
            return true
        }
        when(item.itemId){
            R.id.itemactualizar ->{
                var  itent = Intent(this,MainActivity2::class.java)

                itent.putExtra("idactualizar",listaID.get(idSeleccionadoEnLista))
                startActivity(itent)
                cargarContactos()
            }
        }
        idSeleccionadoEnLista = -1
        return true
    }

    private fun  eliminartodasfechaspasadas(){

        fecha.setText(dia.toString()+ s.toString())

        val fechaCompleta = dia.toString()+ s.toString()

                try {
                    var tran = baseDatos.readableDatabase

                    var resultados = tran.query("EVENTO", arrayOf("*"),null,
                        null,null,null,null)

                    if (resultados.moveToFirst()) {

                        do{

                            fecha.setText(resultados.getString(3).toString())
                            val fechaderesgistro = resultados.getString(3).toString()
                            val sCadena1 = fechaderesgistro
                            val sSubCadenaDIA1 = sCadena1.substring(0,1)
                            val sSubCadenaDIA2 = sCadena1.substring(1,2)
                            val diacompleto = sSubCadenaDIA1+sSubCadenaDIA2
                            fecha.setText(diacompleto)

                            if(dia > diacompleto.toInt()){
                                try {
                                    var trans = baseDatos.writableDatabase

                                    var resultado = trans.delete("EVENTO", "FECHA=?",
                                        arrayOf(fecha.text.toString()))

                                    if(resultado == 0){
                                        mensaje("NO FUE POSIBLE ELIMINAR")
                                        fecha.setText(diacompleto)

                                    }
                                    else{
                                        mensaje("Se logro eliminar con exito todos los Eventos con Fecha anterior a en: ${fechaCompleta}")
                                    }
                                    trans.close()
                                    cargarContactos()
                                }catch (e: SQLiteException){
                                    mensaje(e.message!!)
                                }
                            }else{
                                mensaje("NO SE ENCONTRARON EVENTOS CON FECHAS VENCIDAS")
                            }

                        }while (resultados.moveToNext())
                    } else {
                        mensaje("NO SE ENCONTRARON EVENTOS CON FECHAS VENCIDAS")
                    }
                    tran.close()
                } catch (e: SQLiteException) {

                }
    }
}