package com.ice_opscpoe.featheredfriends

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var registerPrompt: TextView

    private lateinit var dbHelper: DBHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        registerPrompt = findViewById(R.id.registerPrompt)

        dbHelper = DBHelper(this)
        sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        // This checks if login details are saved
        checkSavedLogin()

        loginButton.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()

            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                if (dbHelper.checkUser(usernameText, passwordText)) {
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    showSaveLoginDialog(usernameText, passwordText)
                } else {
                    Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerPrompt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun checkSavedLogin() {
        val savedUsername = sharedPreferences.getString("username", null)
        val savedPassword = sharedPreferences.getString("password", null)

        if (savedUsername != null && savedPassword != null) {
            username.setText(savedUsername)
            password.setText(savedPassword)
        }
    }//(Android Knowledge. 2023), (DigitalOcean. 2022)

    private fun showSaveLoginDialog(username: String, password: String) {
        val hasChosenToSaveLogin = sharedPreferences.getBoolean("hasChosenToSaveLogin", false)

        if (!hasChosenToSaveLogin) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_save_login, null)
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setView(dialogView)
                .setCancelable(false)

            val alertDialog = dialogBuilder.create()
            alertDialog.setTitle("Save Login Info")

            dialogView.findViewById<Button>(R.id.dialogYesButton).setOnClickListener {
                saveLoginInfo(username, password)
                markChoiceMade(true)
                alertDialog.dismiss()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }

            dialogView.findViewById<Button>(R.id.dialogNoButton).setOnClickListener {
                markChoiceMade(false) // Save user's choice not to save login
                alertDialog.dismiss()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }

            alertDialog.show()
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }//(CodePath)

    private fun saveLoginInfo(username: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putBoolean("isLoginSaved", true)
        editor.apply()
        Toast.makeText(this, "Login info saved", Toast.LENGTH_SHORT).show()
    }//(Android Knowledge. 2023)

    private fun markChoiceMade(choice: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("hasChosenToSaveLogin", choice)
            apply()
        }
    }//(DigitalOcean. 2022)
}
//Reference List
//Android Knowledge. 2023. Login and Sign-Up using SQLite in Android Studio| Kotlin .[Youtube]https://www.youtube.com/watch?v=zz659HPTe6M. [Accessed on 13 September 2024]
//Android Knowledge. 2023. Notes App - CRUD SQLite Database in Android Studio using Kotlin| Create Read Update Delete Data. [Youtube] https://www.youtube.com/watch?v=BVAslimaGSk.[Accessed on 14 Septemeber 2024]
//CodePath. nd. Using DialogFragment. [Online] https://guides.codepath.com/android/Using-DialogFragment. [Accessed on 27 September 2024]
//DigitalOcean. 2022. Android Shared Preferences Example Tutorial .[Online]https://www.digitalocean.com/community/tutorials/android-shared-preferences-example-tutorial. [ Accessed on 27 September 2024]