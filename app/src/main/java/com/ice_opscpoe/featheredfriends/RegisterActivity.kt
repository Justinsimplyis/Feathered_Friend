package com.ice_opscpoe.featheredfriends

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password:EditText
    private lateinit var registerButton: Button
    private lateinit var loginPrompt: TextView

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        registerButton = findViewById(R.id.registerButton)
        loginPrompt = findViewById(R.id.loginPrompt)

        dbHelper = DBHelper(this)

        registerButton.setOnClickListener {
            val usernameText = username.text.toString()
            val passwordText = password.text.toString()


            if (usernameText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.addUser(usernameText, passwordText)
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        loginPrompt.setOnClickListener {
            // Navigates to Login Activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }
}