package com.example.miappival2.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.preference.PreferenceManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import androidx.navigation.NavController

@Composable
fun MyOSMMapScreen(navController: NavController) {
    val context = LocalContext.current
    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }
    val mapView = remember { MapView(context) }
    var location by remember { mutableStateOf<Location?>(null) }
    var hasPermission by remember { mutableStateOf(false) }

    // Launcher para solicitar permiso
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted) {
            Log.d("DEBUG", "Permiso concedido")
        } else {
            Log.w("DEBUG", "Permiso denegado por el usuario")
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))

        val fineGranted = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted) {
            hasPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Obtener ubicación si se tiene el permiso
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    Log.d("DEBUG", "Ubicación obtenida: $loc")
                    location = loc
                }
                .addOnFailureListener {
                    Log.e("DEBUG", "No se pudo obtener la ubicación", it)
                }
        }
    }

    AndroidView(factory = { mapView }) {
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        location?.let {
            val startPoint = GeoPoint(it.latitude, it.longitude)
            mapView.controller.setZoom(18.0)
            mapView.controller.setCenter(startPoint)

            val marker = Marker(mapView)
            marker.position = startPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "Aquí estoy"
            mapView.overlays.add(marker)
        }
    }
}
