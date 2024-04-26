package com.example.myposition

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
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
import com.example.myposition.ui.theme.fonts
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
            .padding(16.dp)
            .onGloballyPositioned { coordinates ->
                centerYall = coordinates.size.height.toFloat()
            },
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(8.dp))
        Text(
            text = "My Position",
            modifier = Modifier.height(56.dp).fillMaxSize(),
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
                .fillMaxWidth()
                .height(100.dp),
            horizontalArrangement = Arrangement.Absolute.Center
        ) {
            OutlinedTextField(
                value = xCoordiante,
                onValueChange = {
                    xCoordiante = it
                    isXValid = coordinateXIsValid(xCoordiante)
                                },
                label = { Text(
                    text = "Enter X coordinate",
                    style=TextStyle(
                        fontFamily = fonts
                    ))},
                singleLine = true,
                isError = !isXValid,
                supportingText = {
                                 if(!isXValid){
                                     Text(
                                         text = "Valid range between -90.0° and 90.0° and value separated by dot",
                                         modifier = Modifier.height(36.dp),
                                         fontSize = 12.sp,
                                         style = TextStyle(
                                             fontFamily = fonts,
                                             textAlign = TextAlign.Left
                                         )
                                     )
                                 }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.width((screenWidth / 2)-16.dp)
            )
            OutlinedTextField(
                value = yCoordiante,
                onValueChange = {
                    yCoordiante = it
                    isYValid = coordinateYIsValid(yCoordiante)      },
                label = { Text(
                    text = "Enter Y coordinate",
                    style=TextStyle(
                        fontFamily = fonts
                    ))},
                singleLine = true,
                isError = !isYValid,
                supportingText = {
                    if(!isYValid){
                        Text(
                            text = "Valid range between -180.0° and 180.0° and value separated by dot",
                            modifier = Modifier.height(36.dp),
                            fontSize = 12.sp,
                            style = TextStyle(
                                fontFamily = fonts,
                                textAlign = TextAlign.Left
                            )
                        )
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
                modifier = Modifier
                    .width((screenWidth / 2)-16.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = screenCenterX.toString(),
                onValueChange = {},
                label = { Text(
                    text = "Current X coordinate",
                        style= TextStyle(
                            fontFamily = fonts
                        ))},
                enabled = false,
                modifier = Modifier.width((screenWidth / 2)-16.dp)
            )
            OutlinedTextField(
                value = screenCenterY.toString(),
                onValueChange = {},
                label = { Text(
                    text = "Current Y coordinate",
                    style=TextStyle(
                        fontFamily = fonts
                    ))},

                enabled = false,
                modifier = Modifier.width((screenWidth / 2)-16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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