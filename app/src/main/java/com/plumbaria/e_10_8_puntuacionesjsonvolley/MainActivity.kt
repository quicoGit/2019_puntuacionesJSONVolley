package com.plumbaria.e_10_8_puntuacionesjsonvolley

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {


    var btnVerPuntuaciones: Button? = null
    var btnCrearPuntuacion: Button? = null
    var puntos: TextView? = null

    private val urlObtener = "http://proves.iesperemaria.com/asteroides/puntuaciones/"
    private val urlGrabar = "http://proves.iesperemaria.com/asteroides/puntuaciones/nueva/"

    var jsonObject: JSONObject? = null
    var jsonArray: JSONArray? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnVerPuntuaciones = findViewById(R.id.btnVerPuntuaciones)
        btnCrearPuntuacion = findViewById(R.id.btnCrearPuntuacion)
        puntos = findViewById(R.id.puntos)

        btnVerPuntuaciones!!.setOnClickListener(this)
        btnCrearPuntuacion!!.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.btnCrearPuntuacion -> crearPuntuacion()
            R.id.btnVerPuntuaciones -> mostrarPuntuaciones()
        }
    }

    private fun mostrarPuntuaciones() {
        val colaPeticiones = Volley.newRequestQueue(this)
        val mostrarManager = MostrarManager()
        val peticion = StringRequest(Request.Method.GET,
            urlObtener,
            mostrarManager,
            mostrarManager)
        colaPeticiones.add(peticion)

    }

    private fun crearPuntuacion() {
        val puntos = Math.abs(Random().nextInt(99999))
        val fecha = System.currentTimeMillis()
        val colaPeticiones = Volley.newRequestQueue(this)
        val crearManager = CrearManager()
        val peticion = object : StringRequest(Request.Method.POST,
            urlGrabar,
            crearManager,
            crearManager) {
            override fun getParams(): Map<String, String> {
                val parametros = HashMap<String, String>()
                parametros["puntos"] = puntos!!.toString()
                parametros["nombre"] = "Quico Orozco"
                parametros["fecha"] = fecha.toString()
                return parametros
            }
        }
        colaPeticiones.add(peticion)

    }

    internal inner class MostrarManager : Response.Listener<String>, Response.ErrorListener {
        override fun onErrorResponse(error: VolleyError) {
            puntos!!.setText("Se ha producido un error\n" + error.toString())
        }

        override fun onResponse(response: String) {
            val salida = StringBuilder()
            try {
                jsonObject = JSONObject(response)
                jsonArray = jsonObject!!.getJSONArray("puntuaciones")
                for (i in 0 until jsonArray!!.length()) {
                    val nodo = jsonArray!!.getJSONObject(i)
                    salida.append(nodo.getString("puntos") + " "
                            + nodo.getString("nombre") + "\n")
                }
                puntos!!.setText(salida.toString())
            } catch (e: JSONException) {
                Toast.makeText(applicationContext,
                    "Error accediendo al servicio",
                    Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
    internal inner class CrearManager : Response.Listener<String>, Response.ErrorListener {

        override fun onErrorResponse(error: VolleyError) {
            puntos!!.setText("Se ha producido un error\n" + error.toString())
        }

        override fun onResponse(response: String) {
            val salida = StringBuilder()
            try {
                jsonObject = JSONObject(response)
                salida.append(jsonObject!!.getString("id") + " " +
                        jsonObject!!.getString("puntos") + " " +
                        jsonObject!!.getString("nombre"))
                puntos!!.setText(salida.toString())
            } catch (e: JSONException) {
                Toast.makeText(applicationContext,
                    "Error accediendo al servicio",
                    Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }

        }
    }

}
