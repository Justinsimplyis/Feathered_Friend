package com.ice_opscpoe.featheredfriends

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class HomeActivity : AppCompatActivity() {
    private lateinit var navObservations: LinearLayout
    private lateinit var navMap: LinearLayout
    private lateinit var navSettings: LinearLayout

    //progress bar
    private lateinit var progressText: TextView
    private lateinit var progressBar: ProgressBar
    private val observationGoal = 3
    private val notificationId = 1
    private val channelId = "goal_complete_channel"

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

        createNotificationChannel()
        updateProgress()

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
    private fun updateProgress() {
        val cursor = dbHelper.getAllObservations(currentUserId)
        val currentObservations = cursor.count
        cursor.close()

        val progress = (currentObservations.toFloat() / observationGoal) * 100

        progressBar.progress = progress.toInt()
        progressText.text = "Progress: ${progress.toInt()}% ($currentObservations/$observationGoal)"

        if (progress.toInt() >= 100) {
            // Check if notifications are enabled before sending the notification
            val isNotificationsEnabled = sharedPreferences.getBoolean("notificationsEnabled", true)
            if (isNotificationsEnabled) {
                sendGoalCompleteNotification()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Goal Completion"
            val descriptionText = "Notifications for goal completion"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendGoalCompleteNotification() {
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Congratulations!")
            .setContentText("You have completed your observation goal of $observationGoal species!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }
}
//Reference List
//DigitalOcean. 2022. Android Shared Preferences Example Tutorial .[Online]https://www.digitalocean.com/community/tutorials/android-shared-preferences-example-tutorial. [ Accessed on 27 September 2024]
//Android Knowledge. 2023. Notes App - CRUD SQLite Database in Android Studio using Kotlin| Create Read Update Delete Data. [Youtube] https://www.youtube.com/watch?v=BVAslimaGSk.[Accessed on 14 Septemeber 2024]
//Android Developers. 2024. Android ImageView and MediaStore: How to handle images in Android using ImageView and retrieving them from MediaStore. [Online] Available at: https://developer.android.com/reference/android/widget/ImageView and https://developer.android.com/reference/android/provider/MediaStore [Accessed on 29 September 2024].
//100bit Coding. 2023. Notifications in Android [Kotlin] | Android Studio. [Youtube] Available at: https://youtu.be/Kan_5OeSBN0. [Accessed 1 November 2024 ]
//Android Developers. 2024. Notification runtime permission. [Online] Available at: https://developer.android.com/develop/ui/views/notifications/notification-permission. [Accessed 1 Novemeber 2024]