package com.ice_opscpoe.featheredfriends

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ObservationDetailsActivity : AppCompatActivity() {

    private lateinit var observationTitleTextView: TextView
    private lateinit var observationDetailsTextView: TextView
    private lateinit var observationDateTextView: TextView
    private lateinit var observationLocationTextView: TextView
    private lateinit var editObservationButton: Button
    private lateinit var deleteObservationButton: Button
    private lateinit var backArrow: ImageView
    private lateinit var dbHelper: DBHelper
    private var observationId: Int = -1
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_observation_details)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        observationTitleTextView = findViewById(R.id.observationTitle)
        observationDetailsTextView = findViewById(R.id.observationDetails)
        observationDateTextView = findViewById(R.id.observationDate)
        observationLocationTextView = findViewById(R.id.observationLocation)
        editObservationButton = findViewById(R.id.editObservationButton)
        deleteObservationButton = findViewById(R.id.deleteObservationButton)
        backArrow = findViewById(R.id.backArrow)

        dbHelper = DBHelper(this)

        observationId = intent.getIntExtra("observationId", -1)
        userId = intent.getIntExtra("userId", -1)

        if (observationId != -1) {
            loadObservationDetails(observationId)
        }

        backArrow.setOnClickListener {
            finish()
        }

        editObservationButton.setOnClickListener {
            showEditObservationDialog()
        }

        deleteObservationButton.setOnClickListener {
            deleteObservation(observationId)
        }
    }

    private fun loadObservationDetails(observationId: Int) {
        val observation = dbHelper.getObservationById(observationId)
        observation?.let {
            observationTitleTextView.text = it.title
            observationDetailsTextView.text = it.details
            observationDateTextView.text = it.date
            observationLocationTextView.text = it.location
        }
    }

    private fun showEditObservationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_observation, null)

        val titleEditText = dialogView.findViewById<EditText>(R.id.editObservationTitle)
        val detailsEditText = dialogView.findViewById<EditText>(R.id.editObservationDetails)
        val dateEditText = dialogView.findViewById<EditText>(R.id.editObservationDate)
        val locationEditText = dialogView.findViewById<EditText>(R.id.editObservationLocation)
        val dialogSaveButton = dialogView.findViewById<Button>(R.id.dialogSaveButton)
        val dialogCancelButton = dialogView.findViewById<Button>(R.id.dialogCancelButton)

        titleEditText.setText(observationTitleTextView.text)
        detailsEditText.setText(observationDetailsTextView.text)
        dateEditText.setText(observationDateTextView.text)
        locationEditText.setText(observationLocationTextView.text)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogSaveButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val details = detailsEditText.text.toString().trim()
            val date = dateEditText.text.toString().trim()
            val location = locationEditText.text.toString().trim()

            if (title.isNotEmpty() && details.isNotEmpty() && date.isNotEmpty() && location.isNotEmpty()) {
                dbHelper.updateObservation(observationId, title, details, date, location)
                loadObservationDetails(observationId)
                Toast.makeText(this, "Observation updated successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }//(Android Knowledge. 2023)

        dialogCancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteObservation(observationId: Int) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Delete Observation")
            .setMessage("Are you sure you want to delete this observation?")
            .setPositiveButton("Yes") { _, _ ->
                dbHelper.deleteObservation(observationId)
                Toast.makeText(this, "Observation deleted successfully", Toast.LENGTH_SHORT).show()
                finish() // Go back to the previous activity
            }
            .setNegativeButton("No", null)
            .create()
        dialog.show()
    }
}
//Reference list
//Android Knowledge. 2023. Notes App - CRUD SQLite Database in Android Studio using Kotlin| Create Read Update Delete Data. [Youtube] https://www.youtube.com/watch?v=BVAslimaGSk.[Accessed on 14 Septemeber 2024]