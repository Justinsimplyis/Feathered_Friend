package com.ice_opscpoe.featheredfriends

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ObservationsActivity : AppCompatActivity() {
    private lateinit var navHome : LinearLayout
    private lateinit var navMap : LinearLayout
    private lateinit var navSettings : LinearLayout
    private lateinit var addObservationButton: Button
    private lateinit var observationsListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_observations)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navHome = findViewById(R.id.navHome)
        navMap = findViewById(R.id.navMap)
        navSettings = findViewById(R.id.navSettings)
        addObservationButton = findViewById(R.id.addObservationButton)
        observationsListView = findViewById(R.id.observationsListView)

        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        navMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        navSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        loadObservations()

    }

    private fun loadObservations() {
        TODO("Not yet implemented")
    }
}