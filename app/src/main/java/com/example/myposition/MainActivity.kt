@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myposition

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myposition.ui.theme.MyPositionTheme
import com.example.myposition.ui.theme.fonts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the ViewModel
        val locationViewModel: LocationViewModel by viewModels()

        // Start location updates
        locationViewModel.startLocationUpdates(this, fusedLocationClient)

        // Request location permissions
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    getLastLocation(locationViewModel)
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    getLastLocation(locationViewModel)
                }
                else -> {
                    // No location access granted.
                }
            }
        }

        // Launch the permission request
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        val mapViewModel: MapViewModel by viewModels()

        setContent {
            MyPositionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "login_screen") {
                        composable("login_screen") { LoginScreen(navController) }
                        composable("map_screen") { MapScreen(navController, mapViewModel, fusedLocationClient) }
                        composable("markerList") { MarkerListScreen(mapViewModel.markers.value, onBack = { navController.popBackStack() }) }
                    }
                }
            }
        }
    }

    private fun getLastLocation(locationViewModel: LocationViewModel) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle permission request
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    locationViewModel._location.value = it
                }
            }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    var login by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter=  painterResource(id = R.drawable.theme),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    Spacer(modifier = Modifier.height(36.dp))
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .background(Color(255, 255, 255, 0))
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.Absolute.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 36.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    ),
            ){
                Text(
                    text = "My Location",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        fontSize = 36.sp,
                        color = Color(15, 33, 68),
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = login,
                onValueChange = { login = it },
                label = { Text("Enter Login") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.login),
                        contentDescription = null
                    )
                },
                shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                ),
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Enter Password") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                trailingIcon = {
                    val image = if (passwordVisibility) {
                        painterResource(id = R.drawable.pass_eye_off)
                    } else {
                        painterResource(id = R.drawable.pass_eye)
                    }
                    IconButton(onClick = {
                        passwordVisibility = !passwordVisibility
                    }) {
                        Icon(painter = image, contentDescription = null)
                    }
                },
                leadingIcon = {
                    Icon(painter = painterResource(id = R.drawable.lock), contentDescription = null)
                },
                shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                ),
            )
            Spacer(modifier = Modifier.height(72.dp))
            FloatingButton(onClick = { navController.navigate("map_screen") }, login, password)
            Text(
                text = "Login ",
                modifier = Modifier
                    .fillMaxSize(),
                style = TextStyle(
                    textAlign = TextAlign.Justify,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color(15, 33, 68),

                    )
            )
        }
    }
}
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
@Composable
fun MarkerListScreen(markers: List<MapMarker>, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Marker List",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp),
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = Color(15, 33, 68)
            )
        )

        if (markers.isEmpty()) {
            Text(
                text = "No markers added yet",
                modifier = Modifier
                    .padding(16.dp),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(15, 33, 68)
                )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                FloatingActionButton(
                    onClick = onBack,
                    containerColor = Color(0xFFFFFFFF),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = null
                    )
                }
            }
        } else {
            LazyColumn {
                items(markers) { mapMarker ->
                    Text(
                        text = "${mapMarker.title}:    latitude: ${mapMarker.latitude}, longitude: ${mapMarker.longitude}",
                        modifier = Modifier
                            .padding(16.dp),
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(15, 33, 68)
                        )
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
                    onClick = onBack,
                    containerColor = Color(0xFFFFFFFF),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_left),
                        contentDescription = null
                    )
                }
            }
        }
    }
}
@SuppressLint("MissingPermission")
fun showAddMarkerDialog(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    googleMapRef: GoogleMap?,
    onMarkerAdded: (MapMarker) -> Unit
) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle("Add Marker")

    val input = EditText(context)
    builder.setView(input)

    builder.setPositiveButton("OK") { dialog, _ ->
        val description = input.text.toString()

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                val marker = googleMapRef?.addMarker(
                    MarkerOptions()
                        .position(currentLatLng)
                        .title(description)
                )
                marker?.let {
                    val mapMarker = MapMarker(
                        title = description,
                        latitude = it.position.latitude,
                        longitude = it.position.longitude
                    )
                    onMarkerAdded(mapMarker)
                }
            }
        }
        dialog.dismiss()
    }

    builder.setNegativeButton("Cancel") { dialog, _ ->
        dialog.cancel()
    }

    builder.show()
}
@SuppressLint("MissingPermission")
fun centerMapAtCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    googleMapRef: GoogleMap?
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            val currentLatLng = LatLng(it.latitude, it.longitude)
            googleMapRef?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
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

fun Validate(login: String, password: String): Boolean{
    if (login.toLowerCase() == "admin" && password == "admin"){
        return true
    }else{
        return false
    }
}
@Composable
fun FloatingButton(onClick: () -> Unit, login: String, password: String) {
    var buttonColor by remember { mutableStateOf(Color(0xFFFFFFFF)) }
    FloatingActionButton(
        onClick = {
            if (Validate(login, password)) {
                onClick()
            }
        },
        containerColor = buttonColor
    ){
        Icon(painter = painterResource(id = R.drawable.phone), contentDescription = null)
    }
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