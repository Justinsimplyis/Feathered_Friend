package com.ice_opscpoe.featheredfriends

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.CameraPosition

class MapActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var lastClickedMarker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map fragment and set up the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocationPermission()
        mMap.uiSettings.isZoomControlsEnabled = true
        showBirdHotspots()

        mMap.setOnMarkerClickListener { marker ->
            if (lastClickedMarker == marker) {
                getDirectionsToDestination(marker.position)
                lastClickedMarker = null // Reset to prevent further navigation on single-click
            } else {
                displayMarkerInfo(marker)
                lastClickedMarker = marker
            }
            true
        }
    }

    private fun getLocationPermission() {
        // Check if permission is already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            enableMyLocation()
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun enableMyLocation() {
        // Ensure permissions are granted before enabling location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            zoomToUserLocation()
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // Check if the location permission was granted
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            Toast.makeText(this, "Location permission is required to show your current location.", Toast.LENGTH_LONG).show()
        }
    }

    private fun zoomToUserLocation() {
        // Check for location permission before accessing last location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    val cameraPosition = CameraPosition.Builder()
                        .target(userLatLng)
                        .zoom(12f)
                        .build()
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                } else {
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showBirdHotspots() {
        val hotspots = listOf(
            Triple(LatLng(-33.958056, 25.600000), "African Penguin", "Small penguin found on coastlines."),
            Triple(LatLng(-33.956521, 25.602222), "Cape Gannet", "Seabird known for spectacular dives."),
            Triple(LatLng(-33.953891, 25.605139), "Kelp Gull", "A scavenger that frequents beaches."),
            Triple(LatLng(-33.950000, 25.610000), "African Fish Eagle", "A powerful eagle with a distinct call."),
            Triple(LatLng(-33.949000, 25.612000), "Greater Flamingo", "A large, pink bird with long legs.")
        )

        for ((location, name, info) in hotspots) {
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(name)
                    .snippet(info)
            )
        }
    }

    private fun displayMarkerInfo(marker: Marker) {
        val birdName = marker.title
        val birdInfo = marker.snippet

        // Show an AlertDialog with bird information
        AlertDialog.Builder(this)
            .setTitle(birdName)
            .setMessage(birdInfo)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun getDirectionsToDestination(destination: LatLng) {
        val gmmIntentUri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(packageManager) != null) {
            startActivity(mapIntent)
        }
    }
}
    //(GeeksforGeeks, 2018)





//REFERENCES

//skyshine (2024). how to get current location in google map android. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/21403496/how-to-get-current-location-in-google-map-android [Accessed 27 Sep. 2024].
// Google for Developers. (2024). Markers. [online] Available at: https://developers.google.com/maps/documentation/android-sdk/marker [Accessed 29 Sep. 2024].
//GeeksforGeeks (2018). Google Maps in Android. [online] GeeksforGeeks. Available at: https://www.geeksforgeeks.org/google-maps-in-android/ [Accessed 30 Sep. 2024].




