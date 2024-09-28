package com.ice_opscpoe.featheredfriends

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var navHome: LinearLayout
    private lateinit var logoutButton: Button


    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        navHome = findViewById(R.id.navHome)
        logoutButton = findViewById(R.id.logoutButton)

        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)

        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        logoutButton.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear() // Clears all saved login information
            editor.apply()

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Navigate to LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}