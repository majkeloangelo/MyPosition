package com.example.myposition

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MapViewModel : ViewModel() {
    private val _markers = mutableStateOf<List<MapMarker>>(emptyList())
    val markers = _markers

    fun addMarker(marker: MapMarker) {
        _markers.value += marker
    }

    fun clearMarkers() {
        _markers.value = emptyList()
    }
}