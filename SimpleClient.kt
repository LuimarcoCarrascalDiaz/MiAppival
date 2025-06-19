package com.example.miappival2

import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader

fun llamarServidor(callback: (String) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("http://10.0.2.2:8080/usuarios") // <- Ruta del servidor Node.js
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val respuesta = reader.readText()
            reader.close()

            withContext(Dispatchers.Main) {
                callback(respuesta)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                callback("Error: ${e.message}")
            }
        }
    }
}
