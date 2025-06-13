package com.example.miappival2.backend

import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject

fun registrarUsuario(nombre: String, correo: String, clave: String, callback: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Crear JSON correctamente formateado
            val jsonObject = JSONObject().apply {
                put("nombre", nombre)
                put("correo", correo)
                put("clave", clave)
            }
            val json = jsonObject.toString()
            println("📤 Enviando JSON: $json")

            val url = URL("http://10.0.2.2:8080/usuarios")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

            connection.outputStream.use { it.write(json.toByteArray(Charsets.UTF_8)) }

            val response = connection.inputStream.bufferedReader().readText()
            println("✅ Respuesta del servidor: $response")

            withContext(Dispatchers.Main) {
                callback("ok")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                callback("Error: ${e.message}")
            }
        }
    }
}
