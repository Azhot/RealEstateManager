package fr.azhot.realestatemanager.view.fragment

import android.Manifest
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.FragmentMapBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.utils.*
import fr.azhot.realestatemanager.viewmodel.MapFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.MapFragmentViewModelFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main


class MapFragment : Fragment(), OnMapReadyCallback {


    // variables
    private lateinit var binding: FragmentMapBinding
    private lateinit var navController: NavController
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var deviceLocation: Location? = null
    private val viewModel: MapFragmentViewModel by viewModels {
        MapFragmentViewModelFactory(
            (activity?.application as RealEstateManagerApplication).propertyRepository
        )
    }


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
        observePropertyList()
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

                        Snackbar.make(
                            binding.root,
                            getString(R.string.location_not_available),
                            LENGTH_SHORT
                        ).run {
                            setBackgroundTint(ContextCompat.getColor(context, R.color.primaryColor))
                            show()
                        }
                    }
                }
            }
        }

        activity?.let { activity ->
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
                .apply {
                    requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                }
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

    private fun observePropertyList() {
        viewModel.getPropertyList().observe(viewLifecycleOwner) { propertyList ->
            googleMap.clear()
            val geocoder = Geocoder(context)
            CoroutineScope(IO).launch {
                for (property in propertyList) {
                    runCatching {
                        geocoder.getFromLocationName(property.address.toString(), 1)
                            .run address@{
                                if (this.isNotEmpty()) {
                                    launch(Main) {
                                        addMarker(
                                            property,
                                            LatLng(
                                                this@address[0].latitude,
                                                this@address[0].longitude
                                            ),
                                            context
                                        )
                                    }
                                }
                            }
                    }.run {
                        this.exceptionOrNull()?.let { throwable ->
                            Log.e(
                                this@MapFragment::class.simpleName,
                                "observePropertyList",
                                throwable
                            )
                        }
                    }
                }
            }
        }
    }

    private fun addMarker(property: Property, latLng: LatLng, context: Context?) {
        if (context == null) return
        createBitmapWithGlide(
            Glide.with(context),
            RequestOptions().override(MAP_PHOTO_WIDTH, MAP_PHOTO_HEIGHT).circleCrop(),
            Uri.parse(property.photoList[0].uri)
        ) { bitmap ->
            MarkerOptions().apply {
                title(property.detail.propertyType?.toString() ?: getString(R.string.not_provided))
                snippet(
                    if (property.address.toString().isNotEmpty()) property.address.toString()
                    else getString(R.string.not_provided)
                )
                icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                position(latLng)
                googleMap.addMarker(this)
            }
        }
    }
}