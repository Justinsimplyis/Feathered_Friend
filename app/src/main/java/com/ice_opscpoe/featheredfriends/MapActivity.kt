package com.ice_opscpoe.featheredfriends

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.ice_opscpoe.featheredfriends.ApiClient
import com.ice_opscpoe.featheredfriends.BirdHotspot
import com.ice_opscpoe.featheredfriends.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize the map fragment
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Initialize the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        getCurrentLocation()

        //(skyshine, 2024)
    }



    // Implement the onMapReady function
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Optionally enable user's location on the map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        }


    }


    // Get user's current location function
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }


        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                // Fetch nearby bird hotspots once location is available
                getNearbyHotspots(location.latitude, location.longitude)
            }
        }
    }
    //(skyshine, 2024)



    private fun getNearbyHotspots(lat: Double, lng: Double) {
        val apiService = ApiClient.create()


        val apiKey = "s22a442fd3kc" //API key for mibird

        // Call the function and pass the apiKey
        apiService.getNearbyHotspots(lat, lng, apiKey)
            .enqueue(object : Callback<List<BirdHotspot>> {
                override fun onResponse(call: Call<List<BirdHotspot>>, response: Response<List<BirdHotspot>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val hotspots = response.body()
                        if (hotspots != null && hotspots.isNotEmpty()) {
                            for (hotspot in hotspots) {
                                val hotspotLocation = LatLng(hotspot.lat, hotspot.lng)
                                mMap.addMarker(MarkerOptions().position(hotspotLocation).title(hotspot.locName))
                            }
                        } else {
                            Toast.makeText(this@MapActivity, "No bird hotspots found nearby.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MapActivity, "Failed to retrieve bird hotspots.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<BirdHotspot>>, t: Throwable) {
                    Toast.makeText(this@MapActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
    //(GeeksforGeeks, 2018)





//REFERENCES

//skyshine (2024). how to get current location in google map android. [online] Stack Overflow. Available at: https://stackoverflow.com/questions/21403496/how-to-get-current-location-in-google-map-android [Accessed 27 Sep. 2024].
// Google for Developers. (2024). Markers. [online] Available at: https://developers.google.com/maps/documentation/android-sdk/marker [Accessed 29 Sep. 2024].
//GeeksforGeeks (2018). Google Maps in Android. [online] GeeksforGeeks. Available at: https://www.geeksforgeeks.org/google-maps-in-android/ [Accessed 30 Sep. 2024].



}
