package com.example.myposition

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.myposition.ui.theme.fonts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@SuppressLint("MissingPermission")
@Composable
fun MapScreen(navController: NavHostController, mapViewModel: MapViewModel, locationViewModel: FusedLocationProviderClient) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var screenCenterY by remember { mutableStateOf(0.0) }
    var screenCenterX by remember { mutableStateOf(0.0) }
    var centerX by remember { mutableStateOf(0f) }
    var centerY by remember { mutableStateOf(0f) }
    var centerYall by remember { mutableStateOf(0f) }
    var mapView = rememberMapViewWithLifecycle()
    var condition by remember { mutableStateOf(2) }
    var condition1 by remember { mutableStateOf(2) }
    var googleMapRef by remember { mutableStateOf<GoogleMap?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var expanded by remember { mutableStateOf(false) }
    var markers by remember { mutableStateOf(listOf<MapMarker>()) }
    var markersState by mapViewModel.markers
    var isSatelliteView by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                centerYall = coordinates.size.height.toFloat()
            }
    ) {
        Image(
            painter=  painterResource(id = R.drawable.theme),
            contentDescription = null,
            modifier = Modifier.matchParentSize())
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                centerYall = coordinates.size.height.toFloat()
            }
    ) {
        AndroidView(
            factory = { context ->
                mapView.apply {
                    getMapAsync { googleMap ->
                        googleMapRef = googleMap

                        googleMap.mapType = if (isSatelliteView) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_NORMAL

                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                val currentLatLng = LatLng(it.latitude, it.longitude)
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                            }
                        }

                        googleMap.setOnCameraIdleListener {
                            val projection = googleMap.projection
                            val centerLatLng = projection.visibleRegion.latLngBounds.center
                            screenCenterX = centerLatLng.latitude
                            screenCenterY = centerLatLng.longitude
                        }
                    }
                }
            },

            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    centerX = coordinates.size.width / 2f
                    centerY = coordinates.size.height / 2f
                }
                .border(
                    width = 1.dp,
                    color = Color(15, 33, 68),
                    shape = RoundedCornerShape(5.dp)
                )
        )
        mapView.onSaveInstanceState(Bundle())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            OutlinedTextField(
                value = screenCenterX.toString(),
                onValueChange = {},
                label = {
                    Text(
                        text = "Current X coordinate",
                        style = TextStyle(
                            fontFamily = fonts
                        )
                    )
                },
                enabled = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                ),
                modifier = Modifier
                    .width((screenWidth / 2) - 16.dp)
            )
            OutlinedTextField(
                value = screenCenterY.toString(),
                onValueChange = {},
                label = {
                    Text(
                        text = "Current Y coordinate",
                        style = TextStyle(
                            fontFamily = fonts
                        ),
                    )
                },
                enabled = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                ),
                modifier = Modifier
                    .width((screenWidth / 2) - 16.dp)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp,16.dp,16.dp,30.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { expanded = !expanded },
            containerColor = Color(0xFFFFFFFF),
        ) {
            Icon(painter = painterResource(id = R.drawable.settings), contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                condition1 = condition1 + 1
                if (condition1 % 2 == 0) expanded = true else expanded = false
            },
            offset = DpOffset(x = 0.dp, y = (-48).dp)
        ) {
            DropdownMenuItem(
                onClick = {
                    googleMapRef?.clear()
                    markers = emptyList()
                    mapViewModel.clearMarkers()
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.trash), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Clear markers")
                    }
                }
            )
            DropdownMenuItem(
                onClick = {
                    condition = condition + 1
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.target), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (condition % 2 == 0) "Disable crosshair" else " Enable crosshair")
                    }
                }
            )
            DropdownMenuItem(
                onClick = {
                    showAddMarkerDialog(context, fusedLocationClient, googleMapRef) { mapMarker ->
                        mapViewModel.addMarker(mapMarker)
                    }
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.add_square), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add marker")
                    }
                }
            )
            DropdownMenuItem(
                onClick = {
                    centerMapAtCurrentLocation(fusedLocationClient, googleMapRef)
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.gps_fixed), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Center on location")
                    }
                }
            )
            DropdownMenuItem(
                onClick = {
                    navController.navigate("markerList") {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.pin_alt), contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Show markers")
                    }
                }
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp,16.dp,16.dp,30.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        FloatingActionButton(
            onClick = {
                isSatelliteView = !isSatelliteView
                googleMapRef?.mapType = if (isSatelliteView) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_NORMAL },
            containerColor = Color(0xFFFFFFFF),
        ) {
            if(isSatelliteView) {
                Icon(
                    painter = painterResource(id = R.drawable.def),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }else{
                Icon(
                    painter = painterResource(id = R.drawable.satelita),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    }
    DrawCrosshair(centerX, centerY, centerYall, condition)
    DrawMarkers(googleMapRef, markersState)
}
fun DrawMarkers(googleMap: GoogleMap?, markers: List<MapMarker>) {
    googleMap?.let { map ->
        map.clear()
        markers.forEach { marker ->
            val markerOptions = MarkerOptions()
                .position(LatLng(marker.latitude, marker.longitude))
                .title(marker.title)
            map.addMarker(markerOptions)
        }
    }
}
@Composable
fun DrawCrosshair(centerX: Float, centerY: Float, centerYall: Float, condition: Int) {
    if(condition%2 == 0){
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            drawCircle(
                center = Offset(x = centerX, y = centerYall-centerY),
                color = Color.Black,
                radius = 20f,
                style = Stroke(width = 5f)
            )
            drawCircle(
                center = Offset(x = centerX, y = centerYall-centerY),
                color = Color.Black,
                radius = 2f,
                style = Stroke(width = 5f)
            )
        }
    }
}
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    return remember {
        MapView(context).apply {
            onCreate(Bundle())
        }
    }
}
