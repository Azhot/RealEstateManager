package fr.azhot.realestatemanager.view.fragment

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.FragmentMapBinding
import fr.azhot.realestatemanager.utils.*


class MapFragment : Fragment(), OnMapReadyCallback {


    // variables
    private lateinit var binding: FragmentMapBinding
    private lateinit var navController: NavController
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var deviceLocation: Location? = null


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        checkLocationPermission()
        startMap()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if (googleMap == null) return
        this.googleMap = googleMap
        this.googleMap.apply {
            setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.mapstyle))
            moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(CENTER_FRANCE_LAT, CENTER_FRANCE_LONG),
                    INIT_ZOOM
                )
            )
            checkLocationPermission()
        }
        getDeviceLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    // functions
    private fun checkLocationPermission() {
        if (!checkPermissions(
                requireContext(),
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        ) {
            navController.navigateUp()
        }
    }

    private fun startMap() {
        (childFragmentManager.findFragmentById(binding.mapFragment.id) as SupportMapFragment)
            .getMapAsync(this)
    }

    private fun getDeviceLocationUpdates() {
        val locationRequest = LocationRequest().apply {
            interval = DEFAULT_INTERVAL
            fastestInterval = FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (locationCallback == null) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult?) {
                    super.onLocationResult(result)
                    result?.lastLocation?.let { location ->
                        if (deviceLocation == null) {
                            moveCameraTo(location)
                            deviceLocation = location
                        }
                    }
                }

                override fun onLocationAvailability(availability: LocationAvailability?) {
                    super.onLocationAvailability(availability)
                    checkLocationPermission()

                    if (availability?.isLocationAvailable == true) {
                        googleMap.isMyLocationEnabled = true
                    } else {
                        googleMap.isMyLocationEnabled = false
                        deviceLocation = null
                        Toast.makeText(
                            context,
                            "Device Location is not available. Please check GPS settings.", // todo: string resource
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity()).apply {
                requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
            }
    }

    private fun moveCameraTo(location: Location) {
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location.latitude, location.longitude),
                DEFAULT_ZOOM
            )
        )
    }
}