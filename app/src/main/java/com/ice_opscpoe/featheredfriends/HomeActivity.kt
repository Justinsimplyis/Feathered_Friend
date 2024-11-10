package com.ice_opscpoe.featheredfriends

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HomeActivity : AppCompatActivity() {
    private lateinit var navObservations: LinearLayout
    private lateinit var navMap: LinearLayout
    private lateinit var navSettings: LinearLayout

    //progress bar
    private lateinit var progressText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var notificationSwitch: Switch

    private val totalChallengeCount = 5
    private var uniqueSpeciesCount = 0

    // Bird of the Day components
    private lateinit var birdOfTheDayTitle: TextView
    private lateinit var birdOfTheDayImage: ImageView
    private lateinit var birdOfTheDayDescription: TextView

    private lateinit var dbHelper: DBHelper
    private lateinit var sharedPreferences: SharedPreferences

    private val birdsOfTheDay = arrayOf(
        Bird("Southern Yellow-Billed Hornbill", R.drawable.southern_yellow_billed_hornbill,
            "This large bird is known for its long tail and distinctive yellow bill.",
            "Listen for their loud calls in open savanna and woodland areas, often perched on trees."),
        Bird("Cape Sugarbird", R.drawable.cape_sugarbird,
            "A medium-sized bird with a long tail, adapted to feed on nectar from flowers.",
            "Look for them near flowering plants, particularly proteas."),
        Bird("Southern Ground Hornbill", R.drawable.southern_ground_hornbill,
            "A large bird with striking black feathers and a bright red throat pouch.",
            "Found in open grasslands and savannas, often walking on the ground.")
    )

    private var currentBirdIndex = 0
    private var currentUserId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        NotificationUtils.createNotificationChannel(this)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navObservations = findViewById(R.id.navObservations)
        navMap = findViewById(R.id.navMap)
        navSettings = findViewById(R.id.navSettings)



        birdOfTheDayTitle = findViewById(R.id.birdOfTheDayTitle)
        birdOfTheDayImage = findViewById(R.id.birdOfTheDayImage)
        birdOfTheDayDescription = findViewById(R.id.birdOfTheDayDescription)

        progressText = findViewById(R.id.progressText)
        progressBar = findViewById(R.id.progressBar)


        dbHelper = DBHelper(this)
        sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        // Load current user ID
        currentUserId = sharedPreferences.getInt("userId", -1)

        // Check and update the bird of the day
        updateBirdOfTheDayIfNeeded()

        // Gets the data passed from the ObservationsActivity (number of unique species observed)
        uniqueSpeciesCount = intent.getIntExtra("uniqueSpeciesCount", 0)

        // Updates the progress
        updateProgress(uniqueSpeciesCount)

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
        }


    }

    private fun updateBirdOfTheDayIfNeeded() {
        val lastUpdateTime = sharedPreferences.getLong("lastUpdateTime", 0)
        val currentTime = System.currentTimeMillis()

        // Check if 24 hours have passed since the last update
        if (currentTime - lastUpdateTime > 24 * 60 * 60 * 1000) {
            // Update the bird of the day
            updateBirdOfTheDay()

            // Save the current time as the last update time
            sharedPreferences.edit().putLong("lastUpdateTime", currentTime).apply()
        } else {
            // If less than 24 hours, just update the display
            displayCurrentBirdOfTheDay()
        }
    }

    private fun updateBirdOfTheDay() {
        currentBirdIndex = (currentBirdIndex + 1) % birdsOfTheDay.size
        displayCurrentBirdOfTheDay()
    }

    private fun displayCurrentBirdOfTheDay() {
        val bird = birdsOfTheDay[currentBirdIndex]
        birdOfTheDayTitle.text = bird.name
        Glide.with(this).load(bird.imageResourceId).into(birdOfTheDayImage)
        birdOfTheDayDescription.text = bird.description
    }
    private fun openBirdVideo(videoUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "Unable to open video", Toast.LENGTH_SHORT).show()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun updateProgress(uniqueSpeciesCount: Int) {
        progressText.text = "Progress: $uniqueSpeciesCount/$totalChallengeCount"
        progressBar.progress = uniqueSpeciesCount

        // Check if the challenge is completed
        if (uniqueSpeciesCount >= totalChallengeCount) {
            progressText.append("\nChallenge Completed!")

            // If the notification switch is on, show the notification
            if (notificationSwitch.isChecked) {
                NotificationUtils.showGoalCompletedNotification(this)
            }
        }
    }
}
//Reference List
//DigitalOcean. 2022. Android Shared Preferences Example Tutorial .[Online]https://www.digitalocean.com/community/tutorials/android-shared-preferences-example-tutorial. [ Accessed on 27 September 2024]
//Android Knowledge. 2023. Notes App - CRUD SQLite Database in Android Studio using Kotlin| Create Read Update Delete Data. [Youtube] https://www.youtube.com/watch?v=BVAslimaGSk.[Accessed on 14 Septemeber 2024]
//Android Developers. 2024. Android ImageView and MediaStore: How to handle images in Android using ImageView and retrieving them from MediaStore. [Online] Available at: https://developer.android.com/reference/android/widget/ImageView and https://developer.android.com/reference/android/provider/MediaStore [Accessed on 29 September 2024].