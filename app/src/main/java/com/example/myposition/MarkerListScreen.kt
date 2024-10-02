package com.example.myposition

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.widget.EditText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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