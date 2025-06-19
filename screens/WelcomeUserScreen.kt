package com.example.miappival2.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/**
 * Pantalla de bienvenida mostrada después de registrarse.
 *
 * @param navController NavController para gestionar la navegación.
 * @param username      Nombre o correo del usuario para personalizar el saludo.
 */
@Composable
fun WelcomeUserScreen(
    navController: NavController,
    username: String? = null
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Bienvenido" + (username?.let { ", $it" } ?: "") + "!",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("viviendas") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Acceder a viviendas")
            }

            // 🧭 Nuevo botón: Servicios de geolocalización
            Button(
                onClick = { navController.navigate("ubicación") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Servicios de geolocalización")
            }

            Button(
                onClick = { navController.navigate("login") { popUpTo("login") { inclusive = true } } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ir al inicio de sesión")
            }
        }
    }
}
