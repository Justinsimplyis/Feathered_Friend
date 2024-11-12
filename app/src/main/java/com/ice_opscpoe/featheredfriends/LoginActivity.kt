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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var registerPrompt: TextView

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbHelper: DBHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()
        dbHelper = DBHelper(this)
        sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        registerPrompt = findViewById(R.id.registerPrompt)

        val sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        val isLoggedOut = sharedPreferences.getBoolean("isLoggedOut", false)
        val isLoginSaved = sharedPreferences.getBoolean("isLoginSaved", false)

        if (!isLoggedOut && isLoginSaved) {
            val savedUsername = sharedPreferences.getString("username", null)
            val savedPassword = sharedPreferences.getString("password", null)
            if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
                loginWithFirebase(savedUsername, savedPassword)
            }
        }
        loginButton.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()

            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (passwordText.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            } else {
                loginWithFirebase(usernameText, passwordText)
            }
        }

        registerPrompt.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun loginWithFirebase(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val firebaseUserId = user?.uid ?: ""
                    val usernameText = email

                    if (dbHelper.checkUser(usernameText)) {
                        dbHelper.updateFirebaseUid(usernameText, firebaseUserId)
                    } else {
                        dbHelper.addUser(usernameText, firebaseUserId)
                    }

                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                    // Save login info if the save login option was enabled
                    if (sharedPreferences.getBoolean("isLoginSaved", false)) {
                        saveLoginInfo(email, password)
                    }

                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveLoginInfo(username: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putBoolean("isLoginSaved", true)
        editor.apply()
    }
}


//Reference List
//Android Knowledge. 2023. Login and Sign-Up using SQLite in Android Studio| Kotlin .[Youtube]https://www.youtube.com/watch?v=zz659HPTe6M. [Accessed on 13 September 2024]
//Android Knowledge. 2023. Notes App - CRUD SQLite Database in Android Studio using Kotlin| Create Read Update Delete Data. [Youtube] https://www.youtube.com/watch?v=BVAslimaGSk.[Accessed on 14 Septemeber 2024]
//CodePath. nd. Using DialogFragment. [Online] https://guides.codepath.com/android/Using-DialogFragment. [Accessed on 27 September 2024]
//DigitalOcean. 2022. Android Shared Preferences Example Tutorial .[Online]https://www.digitalocean.com/community/tutorials/android-shared-preferences-example-tutorial. [ Accessed on 27 September 2024]