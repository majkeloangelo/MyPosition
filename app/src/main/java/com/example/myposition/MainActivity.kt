package com.example.myposition

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LifecycleOwner
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

        setContent {
            MyPositionTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "login_screen") {
                        composable("login_screen") { LoginScreen(navController) }
                        composable("map_screen") { MapScreen(navController, locationViewModel) }
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
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My Location",
                modifier = Modifier
                    .height(56.dp)
                    .fillMaxSize(),
                style = TextStyle(
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = Color(15, 33, 68),
                )
            )
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
                }
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
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box {
                Row(
                    modifier = Modifier
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Absolute.Center
                ) {
                    if (Validate(login, password)) {
                        Text(
                            text = "Click to show info about device",
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth(),
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(15, 33, 68),
                            )
                        )
                    } else {
                        Text(
                            text = "Enter validate login and password",
                            modifier = Modifier
                                .height(16.dp)
                                .fillMaxWidth(),
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color(15, 33, 68),
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            FloatingButton(onClick = { navController.navigate("map_screen") }, login, password)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "",
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
@Composable
fun MapScreen(navController: NavHostController, locationViewModel: LocationViewModel) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var xCoord by remember { mutableStateOf(51.77679067918483) }
    var yCoord by remember { mutableStateOf(19.489166381256819) }
    var screenCenterY by remember { mutableStateOf(0.0) }
    var screenCenterX by remember { mutableStateOf(0.0) }
    var centerX by remember { mutableStateOf(0f) }
    var centerY by remember { mutableStateOf(0f) }
    var centerYall by remember { mutableStateOf(0f) }
    var mapView = rememberMapViewWithLifecycle()
    var condition by remember { mutableStateOf(2) }
    var condition1 by remember { mutableStateOf(2) }
    val location by locationViewModel.location.observeAsState()
    var googleMapRef by remember { mutableStateOf<GoogleMap?>(null) }
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                centerYall = coordinates.size.height.toFloat()
            }
    ) {
    }
    /*Column(
modifier = Modifier
    .fillMaxSize()
    .background(Color(220, 206, 192, 0))
    .onGloballyPositioned { coordinates ->
        centerYall = coordinates.size.height.toFloat()
    },
verticalArrangement = Arrangement.SpaceBetween,
horizontalAlignment = Alignment.CenterHorizontally
) {
Spacer(modifier = Modifier.height(8.dp))
Text(
    text = "My Location",
    modifier = Modifier
        .height(56.dp)
        .fillMaxSize(),
    style = TextStyle(
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        fontFamily = fonts,
        color = Color(15, 33, 68),
    )
)
Row(
    modifier = Modifier
        .fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
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
Spacer(modifier = Modifier.height(16.dp))
Row(
    modifier = Modifier
        .fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    OutlinedButton(
        modifier = Modifier.width((screenWidth / 2) - 16.dp),
        onClick = {
            mapView.apply {
                getMapAsync { googleMap ->
                    googleMap.clear()
                }
            }
        },
        border = BorderStroke(1.dp, Color(15, 33, 68)),
        shape = RoundedCornerShape(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(15, 33, 68),
        ),
        enabled = true
    ) {
        Text(
            text = "Clear markers",
            style = TextStyle(
                fontFamily = fonts,
                color = Color(15, 33, 68)
            )
        )
    }
    OutlinedButton(
        modifier = Modifier.width((screenWidth / 2) - 16.dp),
        onClick = {
            condition = condition + 1
        },
        border = BorderStroke(1.dp, Color(15, 33, 68)),
        shape = RoundedCornerShape(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White,
            contentColor = Color(15, 33, 68),
        ),
        enabled = true
    ) {
        val buttonText = if (condition % 2 == 0) "Disable crosshair" else "Enable crosshair"
        Text(
            text = buttonText,
            style = TextStyle(
                fontFamily = fonts,
                color = Color(15, 33, 68)
            )
        )
    }
}
Spacer(modifier = Modifier.height(16.dp))*/
    Box(
        modifier = Modifier.fillMaxSize()
            .onGloballyPositioned { coordinates ->
                centerYall = coordinates.size.height.toFloat()
            }
    ) {
        AndroidView(
            factory = { context ->
                mapView.apply {
                    getMapAsync { googleMap ->
                        googleMapRef = googleMap
                        val location = LatLng(xCoord, yCoord)
                        val marker = googleMap.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title("Your Position")
                        )
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))

                        googleMap.setOnCameraIdleListener {
                            val projection = googleMap.projection
                            val centerLatLng = projection.visibleRegion.latLngBounds.center
                            screenCenterX = centerLatLng.latitude
                            screenCenterY = centerLatLng.longitude
                        }
                        locationViewModel.location.observe(context as LifecycleOwner) { newLocation ->
                            newLocation?.let {
                                val newLatLng = LatLng(it.latitude, it.longitude)
                                marker?.position = newLatLng
                                googleMap.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        newLatLng,
                                        16f
                                    )
                                )
                            }
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
            .padding(16.dp),
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
            }
        ) {
            DropdownMenuItem(
                onClick = {
                    googleMapRef?.clear()
                    //expanded = false
                },
                text = { Text("Clear markers") }
            )
            DropdownMenuItem(
                onClick = {
                    condition = condition + 1
                    //expanded = false
                },
                text = { Text(if (condition % 2 == 0) "Disable crosshair" else "Enable crosshair") }
            )
            DropdownMenuItem(
                onClick = {
                    addMarkerAtCurrentLocation(fusedLocationClient, googleMapRef)
                   //expanded = false
                },
                text = { Text("Add marker") }
            )
            DropdownMenuItem(
                onClick = {
                    centerMapAtCurrentLocation(fusedLocationClient, googleMapRef)
                    //expanded = false
                },
                text = { Text("Center on location") }
            )
        }
    }
    DrawCrosshair(centerX, centerY, centerYall, condition)
}
@SuppressLint("MissingPermission")
fun addMarkerAtCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    googleMapRef: GoogleMap?
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        location?.let {
            val currentLatLng = LatLng(it.latitude, it.longitude)
            googleMapRef?.addMarker(
                MarkerOptions()
                    .position(currentLatLng)
                    .title("Current Location")
            )
            googleMapRef?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
        }
    }
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
) {
Icon(painter = painterResource(id = R.drawable.phone), contentDescription = null)
}
}
