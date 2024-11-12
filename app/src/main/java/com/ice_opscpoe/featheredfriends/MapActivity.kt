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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the map fragment and set up the map (Google for Developers, 2024)
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
        // check to see if wheterh permission is granted (Google for Developers, 2024)
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
            Triple(LatLng(-33.958056, 25.600000), "Downey Woodpacker", "Small penguin found on coastlines.Despite its small size, the Downy Woodpecker can peck up to 16 times per second! It also has special feathers around its nostrils to keep out wood dust."),
            Triple(LatLng(-33.837793, 25.521485), "Cape Gannet", "Seabird known for spectacular dives.These seabirds can dive from heights of up to 30 meters (98 feet), reaching speeds of up to 100 km/h (62 mph) before hitting the water to catch fish!"),
            Triple(LatLng(-33.981884, 25.587458), "Kelp Gull", "A scavenger that frequents beaches.Kelp Gulls are expert foragers and can open shellfish by dropping them from great heights onto rocks to crack them open.\n" +
                    "\n"),
            Triple(LatLng(-34.010859, 25.665177), "African Fish Eagle", "A powerful eagle with a distinct call. Known as the \"Voice of Africa,\" its call is so distinct that it’s often used as a symbol for African wilderness in movies and media."),
            Triple(LatLng(-33.949000, 25.612000), "Greater Flamingo", "A large, pink bird with long legs.The pink color of flamingos comes from the pigments in the algae and crustaceans they eat, and the more they eat, the pinker they become!"),
            Triple(LatLng(-33.938810,  25.552216), "flying pecker", "A large, pink bird with long legs.Woodpeckers have incredibly strong neck muscles that protect them from brain injury when they peck – they can withstand forces up to 1,000 times that of gravity!")

              //  (Google for Developers, 2024)

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

        // Show an Dialog with bird information
        AlertDialog.Builder(this)
            .setTitle(birdName)
            .setMessage(birdInfo)
            .setPositiveButton("OK", null)
            .show()
    }
   // creating a function to ge direction from the users location to marker (Google for Developers, 2024)
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

//Google for Developers. (2024). Add a map. [online] Available at: https://developers.google.com/maps/documentation/android-sdk/map [Accessed 12 Nov. 2024].
//




