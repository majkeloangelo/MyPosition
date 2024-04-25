package com.example.myposition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                MapScreen()
        }
    }
}
@Composable
fun MapScreen() {
    val keyboardController = LocalSoftwareKeyboardController.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    var xCoordiante by remember { mutableStateOf("0.0") }
    var yCoordiante by remember { mutableStateOf("0.0") }
    var xCoord by remember { mutableStateOf(51.77679067918483) }
    var yCoord by remember { mutableStateOf(19.489166381256819) }
    var screenCenterY by remember { mutableStateOf(0.0) }
    var screenCenterX by remember { mutableStateOf(0.0) }
    var centerX by remember { mutableStateOf(0f) }
    var centerY by remember { mutableStateOf(0f) }
    var centerYall by remember { mutableStateOf(0f) }
    var mapView = rememberMapViewWithLifecycle()
    var isXValid by remember { mutableStateOf(true) }
    var isYValid by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                centerYall = coordinates.size.height.toFloat()
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "MyPosition",
            style = TextStyle(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = Color(15, 33, 68),
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth()

        ) {
            OutlinedTextField(
                value = xCoordiante,
                onValueChange = {
                    xCoordiante = it
                    isXValid = coordinateXIsValid(xCoordiante)
                                },
                label = { Text(text = "Enter X coordinate") },
                singleLine = true,
                isError = !isXValid,
                supportingText = {
                                 if(!isXValid){
                                     Text(text="Valid X coordinate is beetwen -90.0 and 90.0 and separated by dot")
                                 }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.width(screenWidth / 2)
            )

            OutlinedTextField(
                value = yCoordiante,
                onValueChange = {
                    yCoordiante = it
                    isYValid = coordinateYIsValid(yCoordiante)      },
                label = { Text(text = "Enter Y coordinate") },
                singleLine = true,
                isError = !isYValid,
                supportingText = {
                    if(!isYValid){
                        Text(text="Valid Y coordinate is beetwen -180.0 and 180.0 and separated by dot")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if(isYValid && isXValid){
                            keyboardController?.hide()
                            xCoord = xCoordiante.toDouble()
                            yCoord = yCoordiante.toDouble()
                            mapView.apply {
                                getMapAsync { googleMap ->
                                    val location = LatLng(xCoord, yCoord)
                                    googleMap.addMarker(
                                        MarkerOptions()
                                            .position(location)
                                            .title("Your Position")
                                    )
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
                                }
                            }
                        }
                    }
                ),
                modifier = Modifier.width(screenWidth / 2)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = screenCenterX.toString(),
                onValueChange = {},
                label = { Text(text = "Current X coordinate") },
                enabled = false,
                modifier = Modifier.width(screenWidth / 2)
            )
            OutlinedTextField(
                value = screenCenterY.toString(),
                onValueChange = {},
                label = { Text(text = "Current Y coordinate") },
                enabled = false,
                modifier = Modifier.width(screenWidth / 2)
            )
        }
            AndroidView(
                factory = { context ->
                    mapView.apply {
                        getMapAsync { googleMap ->
                            val location = LatLng(xCoord, yCoord)
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(location)
                                    .title("Your Position")
                            )
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))

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
            )
            mapView.onSaveInstanceState(Bundle())
        }
    DrawCrosshair(centerX, centerY, centerYall)
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
fun DrawCrosshair(centerX: Float, centerY: Float, centerYall: Float) {
    Canvas(
        modifier = Modifier.fillMaxSize()
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
fun coordinateXIsValid(x:String): Boolean {
    val regexPattern = """^-?(90(\.0+)?|[0-8]?\d(\.\d+)?)$""".toRegex()
    return regexPattern.matches(x)
}
fun coordinateYIsValid(y:String): Boolean{
    val regexPattern = """^-?(180(\.0+)?|1[0-7]\d(\.\d+)?|\d{1,2}(\.\d+)?)$""".toRegex()
    return regexPattern.matches(y)
}