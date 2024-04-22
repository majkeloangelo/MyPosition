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
import androidx.compose.material3.TextField
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
    var xCoordiante by remember { mutableStateOf("") }
    var yCoordiante by remember { mutableStateOf("") }
    var screenCenterY by remember { mutableStateOf(0.0) }
    var screenCenterX by remember { mutableStateOf(0.0) }
    var centerX by remember { mutableStateOf(0f) }
    var centerY by remember { mutableStateOf(0f) }
    var centerYall by remember { mutableStateOf(0f) }

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
            text="MyPosition",
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
                onValueChange = { xCoordiante = it },
                label = { Text(text = "Enter X coordinate") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier.width(screenWidth / 2)
            )
            OutlinedTextField(
                value = yCoordiante,
                onValueChange = { yCoordiante = it },
                label = { Text(text = "Enter Y coordinate") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier.width(screenWidth / 2)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
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
        val mapView = rememberMapViewWithLifecycle()

        AndroidView(
            factory = { context ->
                mapView.apply {
                    getMapAsync { googleMap ->
                        val location = LatLng(37.7749, -122.4194)
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(location)
                                .title("Marker in San Francisco")
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