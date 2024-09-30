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
        private const val DATABASE_NAME = "FeatheredFriend.db"

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

    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OBSERVATIONS")
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
    }//(Android Knowledge. 2023), (Android Developer. 2024)

    // Observation-related methods
    fun addObservation(title: String?, details: String?, date: String?, location: String?, userId: Int) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TITLE, title)
            put(COLUMN_DETAILS, details)
            put(COLUMN_DATE, date)
            put(COLUMN_LOCATION, location)
            put(COLUMN_USER_ID_FK, userId)
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
            put(COLUMN_TITLE, title)
            put(COLUMN_DETAILS, details)
            put(COLUMN_DATE, date)
            put(COLUMN_LOCATION, location)
        }
        db.update(TABLE_OBSERVATIONS, values, "$COLUMN_OBSERVATION_ID=?", arrayOf(id.toString()))
        db.close()
    }
    fun getObservationById(observationId: Int): Observation? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery(
            "SELECT * FROM $TABLE_OBSERVATIONS WHERE $COLUMN_OBSERVATION_ID = ?",
            arrayOf(observationId.toString())
        )
        var observation: Observation? = null
        if (cursor.moveToFirst()) {
            observation = Observation(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_OBSERVATION_ID)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                details = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAILS)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LOCATION))
            )
        }
        cursor.close()
        db.close()
        return observation
    }
    fun deleteObservation(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_OBSERVATIONS, "$COLUMN_OBSERVATION_ID=?", arrayOf(id.toString()))
        db.close()
    }//(Android Knowledge. 2023), (Android Developer. 2024)

}
//Reference List
//Android Knowledge. 2023. Notes App - CRUD SQLite Database in Android Studio using Kotlin| Create Read Update Delete Data. [Youtube] https://www.youtube.com/watch?v=BVAslimaGSk.[Accessed on 14 Septemeber 2024]
//Android Developer. 2024. Save data using SQLite. [Online]. https://developer.android.com/training/data-storage/sqlite. [Accessed on 28 Sepetember 2024]