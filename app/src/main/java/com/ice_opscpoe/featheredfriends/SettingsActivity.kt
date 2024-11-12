package com.ice_opscpoe.featheredfriends

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SettingsActivity : AppCompatActivity() {
    private lateinit var navHome: LinearLayout
    private lateinit var logoutButton: Button
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var themeSwitch: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var saveLoginSwitch: Switch
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var notificationSwitch: Switch

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
        themeSwitch = findViewById(R.id.themeSwitch)
        saveLoginSwitch = findViewById(R.id.saveLoginSwitch)
        notificationSwitch = findViewById(R.id.notificationSwitch)

        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)

        themeSwitch.isChecked = UserSettings.isDarkMode(this)

        loadSettings()

        // Load the saved notification preference
        val isNotificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", true)
        notificationSwitch.isChecked = isNotificationsEnabled

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("notificationsEnabled", isChecked)
            editor.apply()
        }
        navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        logoutButton.setOnClickListener {
            logout()
        }
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            UserSettings.saveThemePreference(this, isChecked)
            applyTheme(isChecked)
        }

        saveLoginSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Login info will be saved on next login", Toast.LENGTH_SHORT).show()
            } else {
                // This clears the login info if the user chooses not to save
                clearLoginInfo()
            }
        }
    }

    private fun applyTheme(isDarkMode: Boolean) {
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
        recreate()
    }

    private fun loadSettings() {
        val isLoginSaved = sharedPreferences.getBoolean("isLoginSaved", false)
        saveLoginSwitch.isChecked = isLoginSaved
    }

    private fun saveLoginInfo(username: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putBoolean("isLoginSaved", true)
        editor.apply()
        Toast.makeText(this, "Login info saved", Toast.LENGTH_SHORT).show()
    }

    private fun clearLoginInfo() {
        val editor = sharedPreferences.edit()
        editor.remove("username")
        editor.remove("password")
        editor.putBoolean("isLoginSaved", false)
        editor.apply()
        Toast.makeText(this, "Login info cleared", Toast.LENGTH_SHORT).show()
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        if (!saveLoginSwitch.isChecked) {

            clearLoginInfo()
            Toast.makeText(this, "All data cleared on logout", Toast.LENGTH_SHORT).show()
        } else {

            editor.putBoolean("isLoggedOut", true)
            Toast.makeText(this, "Logged out successfully, login info is saved", Toast.LENGTH_SHORT).show()
        }
        editor.apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
//Reference List
//Coding Meet. 2023. How to Handle Light Mode or Dark Mode Theme app in Android Studio Kotlin. [Youtube] https://www.youtube.com/watch?v=UVeki-FKH8c. [Accessed on 28 September 2024]
//Tutorials Point. 2024. Android - Session Management. [Online] https://www.tutorialspoint.com/android/android_session_management.htm. [Accessed on 28 September 2024]
//Android Developers. 2024. Save simple data with SharedPreferences. [Online]. Available at: https://developer.android.com/training/data-storage/shared-preferences. [Accesesed at 1 November 2024]
//100bit Coding. 2023. Notifications in Android [Kotlin] | Android Studio. [Youtube] Available at: https://youtu.be/Kan_5OeSBN0. [Accessed 1 November 2024]
//Android Developers. 2024. Notification runtime permission. [Online] Available at: https://developer.android.com/develop/ui/views/notifications/notification-permission. [Accessed 1 November 2024]