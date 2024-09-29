package com.ice_opscpoe.featheredfriends

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import java.security.MessageDigest

class DBHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "FeatheredFriends.db"

        // User table constants
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"

        // Observation table constants
        const val TABLE_OBSERVATIONS = "observations"
        const val COLUMN_OBSERVATION_ID = "id"
        const val COLUMN_TITLE = "title"
        const val COLUMN_DETAILS = "details"
        const val COLUMN_DATE = "date"
        const val COLUMN_LOCATION = "location"
        const val COLUMN_USER_ID_FK = "user_id" // Foreign key for user

        // Favorite Birds table constants
        const val TABLE_FAVORITE_BIRDS = "favorite_birds"
        const val COLUMN_FAVORITE_BIRD_ID = "id"
        const val COLUMN_BIRD_NAME = "name"
        const val COLUMN_BIRD_IMAGE_URI = "image"
        const val COLUMN_USER_ID_FK_BIRD = "user_id" // Foreign key for user ID


    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create users table
        val createUserTable = ("CREATE TABLE $TABLE_USERS (" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_USERNAME TEXT," +
                "$COLUMN_PASSWORD TEXT)")
        db.execSQL(createUserTable)

        // Create observations table
        val createObservationsTable = ("CREATE TABLE $TABLE_OBSERVATIONS (" +
                "$COLUMN_OBSERVATION_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_DETAILS TEXT," +
                "$COLUMN_DATE TEXT," +
                "$COLUMN_LOCATION TEXT," +
                "$COLUMN_USER_ID_FK INTEGER," +
                "FOREIGN KEY($COLUMN_USER_ID_FK) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db.execSQL(createObservationsTable)

        //Create favorite birds table
        val createFavoriteBirdsTable = ("CREATE TABLE $TABLE_FAVORITE_BIRDS (" +
                "$COLUMN_FAVORITE_BIRD_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_BIRD_NAME TEXT," +
                "$COLUMN_BIRD_IMAGE_URI TEXT," +
                "$COLUMN_USER_ID_FK_BIRD INTEGER," +
                "FOREIGN KEY($COLUMN_USER_ID_FK_BIRD) REFERENCES $TABLE_USERS($COLUMN_USER_ID))")
        db.execSQL(createFavoriteBirdsTable)


    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OBSERVATIONS")
        db.execSQL("DROP Table IF EXISTS $TABLE_FAVORITE_BIRDS")
        onCreate(db)
    }
    // User-related methods
    fun addUser(username: String, password: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, hashPassword(password))
        db.insert(TABLE_USERS, null, values)
        db.close()
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val hashedPassword = hashPassword(password)
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME=? AND $COLUMN_PASSWORD=?",
            arrayOf(username, hashedPassword)  // Compare hashed passwords
        )
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    @SuppressLint("Range")
    fun getUserId(username: String): Int {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT $COLUMN_USER_ID FROM $TABLE_USERS WHERE $COLUMN_USERNAME=?",
            arrayOf(username)
        )
        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID))
        }
        cursor.close()
        db.close()
        return userId
    }
    // Password hashing function
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
    // Observation-related methods
    fun addObservation(title: String?, details: String?, date: String?, location: String?, userId: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)          // Title can be null
            put(COLUMN_DETAILS, details)      // Details can be null
            put(COLUMN_DATE, date)            // Date can be null
            put(COLUMN_LOCATION, location)     // Location can be null
            put(COLUMN_USER_ID_FK, userId)    // Store the user ID with the observation
        }
        db.insert(TABLE_OBSERVATIONS, null, values)
        db.close()
    }

    fun getAllObservations(userId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_OBSERVATIONS WHERE $COLUMN_USER_ID_FK = ?", arrayOf(userId.toString()))
    }

    fun updateObservation(id: Int, title: String?, details: String?, date: String?, location: String?) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)          // Title can be null
            put(COLUMN_DETAILS, details)      // Details can be null
            put(COLUMN_DATE, date)            // Date can be null
            put(COLUMN_LOCATION, location)     // Location can be null
        }
        db.update(TABLE_OBSERVATIONS, values, "$COLUMN_OBSERVATION_ID=?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteObservation(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_OBSERVATIONS, "$COLUMN_OBSERVATION_ID=?", arrayOf(id.toString()))
        db.close()
    }
    //favorite birds methods
    fun addFavoriteBird(name: String, imageUri:String, userId: Int) :Boolean{
        val db = this.writableDatabase
        val values = ContentValues()

        // Populate ContentValues with bird information
        values.put(COLUMN_BIRD_NAME, name)
        values.put(COLUMN_BIRD_IMAGE_URI, imageUri)
        values.put(COLUMN_USER_ID, userId)

        // Insert the new row and check if it was successful
        val result = db.insert(TABLE_FAVORITE_BIRDS, null, values)
        db.close()

        // Return true if insertion was successful, otherwise false
        return result != -1L
        db.close()
    }
    @SuppressLint("Range")
    fun getFavoriteBirds(userId: Int): List<FavoriteBird> {
        val birdsList = mutableListOf<FavoriteBird>()
        val db = this.readableDatabase
        val cursor = db.query("FavoriteBirds", null, "userId = ?", arrayOf(userId.toString()), null, null, null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex("birdName"))
                val imageUri = cursor.getString(cursor.getColumnIndex("imageUri"))
                birdsList.add(FavoriteBird(name, imageUri))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return birdsList
    }

    fun getAllFavoriteBirds(userId: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_FAVORITE_BIRDS WHERE $COLUMN_USER_ID_FK_BIRD = ?", arrayOf(userId.toString()))
    }

    fun deleteFavoriteBird(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_FAVORITE_BIRDS, "$COLUMN_FAVORITE_BIRD_ID=?", arrayOf(id.toString()))
        db.close()
    }
}
//Reference List
//