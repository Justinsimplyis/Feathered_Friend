package com.ice_opscpoe.featheredfriends

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    private lateinit var navObservations: LinearLayout
    private lateinit var navMap : LinearLayout
    private lateinit var navSettings : LinearLayout



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navObservations = findViewById(R.id.navObservations)
        navMap = findViewById(R.id.navMap)
        navSettings = findViewById(R.id.navSettings)

        navObservations.setOnClickListener {
            val intent = Intent(this, ObservationsActivity::class.java)
            startActivity(intent)
        }

        navMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        navSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }//(GeeksForGeeks, 2024)
    }
}
//Reference List
//GeeksForGeeks. 2024. Bottom Navigation Bar in Android. [Online] Available at: https://www.geeksforgeeks.org/bottom-navigation-bar-in-android/. [Accessed 18 August 2024]