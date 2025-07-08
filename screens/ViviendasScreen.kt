// ViviendasScreen.kt - corregido y extendido con todos los campos del modelo

package com.example.miappival2.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.miappival2.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// MODELO DE DATOS

data class Vivienda(
    val id: Long = 0,
    val codigo: String?,
    val idPais: Int?,
    val idDepartamento: Int?,
    val idMunicipio: Int?,
    val corregimiento: String?,
    val vereda: String?,
    val localidad: String?,
    val idBarrio: Int?,
    val direccionVivienda: String?,
    val carrera: String?,
    val sector: String?,
    val calle: String?,
    val manzana: String?,
    val lote: String?,
    val puntoReferencia: String?,
    val conjunto: String?,
    val urbanizacion: String?,
    val latitude: String?,
    val longitude: String?,
    val w3w: String?,
    val idUsuario: Int?
)

@Composable
fun ViviendasScreen(navController: NavController) {
    var viviendas by remember { mutableStateOf(listOf<Vivienda>()) }
    var formVisible by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var viviendaActual by remember { mutableStateOf<Vivienda?>(null) }

    val campos = remember {
        mutableStateMapOf(
            "codigo" to "",
            "idPais" to "",
            "idDepartamento" to "",
            "idMunicipio" to "",
            "corregimiento" to "",
            "vereda" to "",
            "localidad" to "",
            "idBarrio" to "",
            "direccionVivienda" to "",
            "carrera" to "",
            "sector" to "",
            "calle" to "",
            "manzana" to "",
            "lote" to "",
            "puntoReferencia" to "",
            "conjunto" to "",
            "urbanizacion" to "",
            "latitude" to "",
            "longitude" to "",
            "w3w" to "",
            "idUsuario" to ""
        )
    }

    LaunchedEffect(Unit) {
        RetrofitClient.api.getViviendas().enqueue(object : Callback<List<Vivienda>> {
            override fun onResponse(call: Call<List<Vivienda>>, response: Response<List<Vivienda>>) {
                if (response.isSuccessful) {
                    viviendas = response.body() ?: emptyList()
                }
            }

            override fun onFailure(call: Call<List<Vivienda>>, t: Throwable) {
                Log.e("API", "Error: ${t.message}")
            }
        })
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Viviendas API") }) },
        floatingActionButton = {
            if (!formVisible) {
                FloatingActionButton(onClick = {
                    formVisible = true
                    isEditing = false
                    viviendaActual = null
                    campos.keys.forEach { campos[it] = "" }
                }) {
                    Text("+")
                }
            }
        }
    ){ padding ->
        Column(modifier = Modifier.padding(padding)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(viviendas) { vivienda ->
                    ViviendaRow(vivienda, onEdit = {
                        formVisible = true
                        isEditing = true
                        viviendaActual = vivienda
                        campos["codigo"] = vivienda.codigo ?: ""
                        campos["direccionVivienda"] = vivienda.direccionVivienda ?: ""
                        campos["localidad"] = vivienda.localidad ?: ""
                        campos["idUsuario"] = vivienda.idUsuario?.toString() ?: ""
                    }, onDelete = {
                        RetrofitClient.api.eliminarVivienda(vivienda.id).enqueue(object : Callback<Void> {
                            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                                viviendas = viviendas.filter { it.id != vivienda.id }
                            }

                            override fun onFailure(call: Call<Void>, t: Throwable) {
                                Log.e("API", "Error al eliminar: ${t.message}")
                            }
                        })
                    })
                }
            }

            if (formVisible) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .background(Color(0xFFECECEC))
                        .verticalScroll(rememberScrollState())
                ) {
                    campos.forEach { (clave, valor) ->
                        OutlinedTextField(
                            value = valor,
                            onValueChange = { campos[clave] = it },
                            label = { Text(clave.replaceFirstChar { it.uppercase() }) },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = { formVisible = false }) { Text("Cancelar") }
                        Button(onClick = {
                            val vivienda = Vivienda(
                                id = viviendaActual?.id ?: 0,
                                codigo = campos["codigo"],
                                idPais = campos["idPais"]?.toIntOrNull(),
                                idDepartamento = campos["idDepartamento"]?.toIntOrNull(),
                                idMunicipio = campos["idMunicipio"]?.toIntOrNull(),
                                corregimiento = campos["corregimiento"],
                                vereda = campos["vereda"],
                                localidad = campos["localidad"],
                                idBarrio = campos["idBarrio"]?.toIntOrNull(),
                                direccionVivienda = campos["direccionVivienda"],
                                carrera = campos["carrera"],
                                sector = campos["sector"],
                                calle = campos["calle"],
                                manzana = campos["manzana"],
                                lote = campos["lote"],
                                puntoReferencia = campos["puntoReferencia"],
                                conjunto = campos["conjunto"],
                                urbanizacion = campos["urbanizacion"],
                                latitude = campos["latitude"],
                                longitude = campos["longitude"],
                                w3w = campos["w3w"],
                                idUsuario = campos["idUsuario"]?.toIntOrNull()
                            )

                            val callback = object : Callback<Vivienda> {
                                override fun onResponse(call: Call<Vivienda>, response: Response<Vivienda>) {
                                    response.body()?.let {
                                        viviendas = if (isEditing) {
                                            viviendas.map { v -> if (v.id == it.id) it else v }
                                        } else {
                                            viviendas + it
                                        }
                                    }
                                    formVisible = false
                                }

                                override fun onFailure(call: Call<Vivienda>, t: Throwable) {
                                    Log.e("API", "Error en operación: ${t.message}")
                                }
                            }

                            if (isEditing) {
                                RetrofitClient.api.actualizarVivienda(vivienda.id, vivienda).enqueue(callback)
                            } else {
                                RetrofitClient.api.crearVivienda(vivienda).enqueue(callback)
                            }
                        }) {
                            Text(if (isEditing) "Guardar" else "Crear")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ViviendaRow(vivienda: Vivienda, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("ID: ${vivienda.id}")
            Text("Código: ${vivienda.codigo ?: ""}")
            Text("Dirección: ${vivienda.direccionVivienda ?: ""}")
            Text("Usuario: ${vivienda.idUsuario ?: ""}")
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Filled.Edit, contentDescription = "Editar", tint = Color(0xFFFFA000))
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar", tint = Color(0xFFD32F2F))
        }
    }
}

