package ru.hse.online.client.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.hse.online.client.repository.storage.LocationRepository
import ru.hse.online.client.services.location.LocationService
import ru.hse.online.client.services.pedometer.ContextProvider

class LocationViewModel(private val contextProvider: ContextProvider, private val repository: LocationRepository) : ViewModel() {
    private val TAG: String = "APP_LOCATION_VIEW_MODEL"
    
    private var _routePoints: MutableStateFlow<List<LatLng>> =
        MutableStateFlow<List<LatLng>>(listOf())
    val routePoints: StateFlow<List<LatLng>> = _routePoints.asStateFlow()

    private var _locationState: MutableStateFlow<LatLng> = MutableStateFlow<LatLng>(LatLng(0.0,0.0));
    val location: StateFlow<LatLng> = _locationState.asStateFlow()

    private val _isOnline = MutableStateFlow(false)
    private val _isPaused = MutableStateFlow(false)

    init {
        repository.locationState
            .onEach { state ->
                Log.i(TAG, "Updating location")
                when (state) {
                    is LocationRepository.LocationState.Available -> {
                        val newPoint = state.location.let {
                            LatLng(it.latitude, it.longitude)
                        }
                        _locationState.value = newPoint
                        if (_isOnline.value) {
                            _routePoints.value += newPoint
                        }
                        Log.i(TAG, "New location: $newPoint")
                    }
                    is LocationRepository.LocationState.Error -> {
                        Log.i(TAG, state.message)
                    }
                    else -> {}
                }
            }
            .launchIn(viewModelScope)
        startService()
    }

    private fun startService() {
        Log.i(TAG, "Starting location service")
        LocationService.startService(contextProvider.getContext())
    }

    override fun onCleared() {
        LocationService.stopService(contextProvider.getContext())
        super.onCleared()
    }

    fun goOnLine() {
        _isOnline.value = true
    }

    fun pauseOnline() {
        if (_isOnline.value) {
            _isPaused.value = true
        }
    }

    fun resumeOnline() {
        if (_isOnline.value) {
            _isPaused.value = false
        }
    }

    fun goOffLine() {
        _isOnline.value = false
    }
}
