package com.ice_opscpoe.featheredfriends

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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
    private lateinit var addFavoriteBirdButton: Button
    private lateinit var favoriteBirdsRecyclerView: RecyclerView

    // Bird of the Day components
    private lateinit var birdOfTheDayTitle: TextView
    private lateinit var birdOfTheDayImage: ImageView
    private lateinit var birdOfTheDayDescription: TextView

    private lateinit var dbHelper: DBHelper
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var favoriteBirdsAdapter: FavoriteBirdsAdapter

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
    private val IMAGE_PICK_CODE = 1000
    private var selectedImageUri: Uri? = null

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
        addFavoriteBirdButton = findViewById(R.id.addFavoriteBirdButton)
        favoriteBirdsRecyclerView = findViewById(R.id.favoriteBirdsRecyclerView)

        birdOfTheDayTitle = findViewById(R.id.birdOfTheDayTitle)
        birdOfTheDayImage = findViewById(R.id.birdOfTheDayImage)
        birdOfTheDayDescription = findViewById(R.id.birdOfTheDayDescription)

        dbHelper = DBHelper(this)
        sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE)

        // Load current user ID (you might have a better way to get this)
        currentUserId = sharedPreferences.getInt("userId", -1)

        // Set up RecyclerView for favorite birds
        favoriteBirdsRecyclerView.layoutManager = LinearLayoutManager(this)
        favoriteBirdsAdapter = FavoriteBirdsAdapter(listOf())
        favoriteBirdsRecyclerView.adapter = favoriteBirdsAdapter

        // Check and update the bird of the day
        updateBirdOfTheDayIfNeeded()

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

        addFavoriteBirdButton.setOnClickListener {
            showAddFavoriteBirdDialog()
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

    private fun showAddFavoriteBirdDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_favorite_bird, null)
        val editBirdName = dialogView.findViewById<EditText>(R.id.birdNameEditText)
        val btnChooseImage = dialogView.findViewById<Button>(R.id.selectImageButton)
        val imageViewBird = dialogView.findViewById<ImageView>(R.id.birdImageView)
        val btnAddFavoriteBird = dialogView.findViewById<Button>(R.id.addBirdButton)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Favorite Bird")
            .setView(dialogView)
            .create()

        btnChooseImage.setOnClickListener {
            chooseImageFromGallery()
        }


    }

    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                selectedImageUri = data.data
                // Updates the dialog's ImageView with the selected image
                updateDialogImage(selectedImageUri)
            } else {
                Toast.makeText(this, "Image selection canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }//(Android Knowledge. 2023), (Android Developers. 2024)

    private fun updateDialogImage(imageUri: Uri?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_favorite_bird, null)
        val imageViewBird = dialogView.findViewById<ImageView>(R.id.birdImageView)
        if (imageUri != null) {
            Glide.with(this).load(imageUri).into(imageViewBird) // Use Glide to load the selected image
        } else {
            imageViewBird.setImageResource(0) // Reset the image view if the URI is null
        }
   }
}
//Reference List
//DigitalOcean. 2022. Android Shared Preferences Example Tutorial .[Online]https://www.digitalocean.com/community/tutorials/android-shared-preferences-example-tutorial. [ Accessed on 27 September 2024]
//Android Knowledge. 2023. Notes App - CRUD SQLite Database in Android Studio using Kotlin| Create Read Update Delete Data. [Youtube] https://www.youtube.com/watch?v=BVAslimaGSk.[Accessed on 14 Septemeber 2024]
//Android Developers. 2024. Android ImageView and MediaStore: How to handle images in Android using ImageView and retrieving them from MediaStore. [Online] Available at: https://developer.android.com/reference/android/widget/ImageView and https://developer.android.com/reference/android/provider/MediaStore [Accessed on 29 September 2024].