package com.ice_opscpoe.featheredfriends

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class UserSettings : Application() {

    companion object {
        const val PREFERENCES = "preferences"
        const val CUSTOM_THEME = "customTheme"
        const val LIGHT_THEME = "lightTheme"
        const val DARK_THEME = "darkTheme"

        fun initializeTheme(context: Context) {
            val sharedPrefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            val isDarkMode = sharedPrefs.getString(CUSTOM_THEME, LIGHT_THEME) == DARK_THEME
            val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            AppCompatDelegate.setDefaultNightMode(mode)
        }

        fun saveThemePreference(context: Context, isDarkMode: Boolean) {
            val editor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit()
            val theme = if (isDarkMode) DARK_THEME else LIGHT_THEME
            editor.putString(CUSTOM_THEME, theme)
            editor.apply()
        }

        fun isDarkMode(context: Context): Boolean {
            val sharedPrefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
            return sharedPrefs.getString(CUSTOM_THEME, LIGHT_THEME) == DARK_THEME
        }

    }
    override fun onCreate() {
        super.onCreate()
        initializeTheme(this)
    }
}