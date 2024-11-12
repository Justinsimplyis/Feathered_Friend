package com.ice_opscpoe.featheredfriends

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ObservationsActivity : AppCompatActivity() {
    private lateinit var navHome: LinearLayout
    private lateinit var navMap: LinearLayout
    private lateinit var navSettings: LinearLayout

    private lateinit var observationsListView: ListView
    private lateinit var addObservationButton: Button
    private lateinit var dbHelper: DBHelper
    private var observationsList: MutableList<Observation> = mutableListOf()
    private var userId: Int = -1
    private var uniqueSpeciesCount: Int = 0

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
        observationsListView = findViewById(R.id.observationsListView)
        addObservationButton = findViewById(R.id.addObservationButton)
        dbHelper = DBHelper(this)

        userId = intent.getIntExtra("userId", -1)
        uniqueSpeciesCount = intent.getIntExtra("uniqueSpeciesCount", 0)

        loadObservations()

        addObservationButton.setOnClickListener {
            showAddObservationDialog()
        }

        navHome.setOnClickListener {
            val homeIntent = Intent(this, HomeActivity::class.java)
            homeIntent.putExtra("uniqueSpeciesCount", uniqueSpeciesCount)
            startActivity(homeIntent)
        }

        navMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }

        navSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        observationsListView.setOnItemClickListener { _, _, position, _ ->
            val observation = observationsList[position]
            val intent = Intent(this, ObservationDetailsActivity::class.java)
            intent.putExtra("observationId", observation.id)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }

    private fun loadObservations() {
        observationsList.clear()
        val cursor = dbHelper.getAllObservations(userId)

        while (cursor.moveToNext()) {
            val observation = Observation(
                cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_OBSERVATION_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DETAILS)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DATE)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_LOCATION))
            )
            observationsList.add(observation)
        }
        cursor.close()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, observationsList.map { it.title })
        observationsListView.adapter = adapter
    }

    private fun showAddObservationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_observation, null)

        val titleEditText = dialogView.findViewById<EditText>(R.id.editObservationTitle)
        val detailsEditText = dialogView.findViewById<EditText>(R.id.editObservationDetails)
        val dateEditText = dialogView.findViewById<EditText>(R.id.editObservationDate)
        val locationEditText = dialogView.findViewById<EditText>(R.id.editObservationLocation)
        val dialogSaveButton = dialogView.findViewById<Button>(R.id.dialogSaveButton)
        val dialogCancelButton = dialogView.findViewById<Button>(R.id.dialogCancelButton)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogSaveButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val details = detailsEditText.text.toString().trim()
            val date = dateEditText.text.toString().trim()
            val location = locationEditText.text.toString().trim()

            if (title.isNotEmpty() && details.isNotEmpty() && date.isNotEmpty() && location.isNotEmpty()) {
                dbHelper.addObservation(title, details, date, location, userId)
                incrementObservationCount()
                loadObservations()
                Toast.makeText(this, "Observation added successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialogCancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun incrementObservationCount() {
        uniqueSpeciesCount++
        Toast.makeText(this, "Progress incremented!", Toast.LENGTH_SHORT).show()
    }
}

//Reference List
//Android Knowledge. 2023. Notes App - CRUD SQLite Database in Android Studio using Kotlin| Create Read Update Delete Data. [Youtube] https://www.youtube.com/watch?v=BVAslimaGSk.[Accessed on 14 Septemeber 2024]
//Android Developer. 2024. Save data using SQLite. [Online]. https://developer.android.com/training/data-storage/sqlite. [Accessed on 28 Sepetember 2024]