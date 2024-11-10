package com.ice_opscpoe.featheredfriends

import android.icu.text.CaseMap.Title

//data class for Birds of the day
data class Bird(
    val name: String,
    val imageResourceId: Int,
    val description: String,
    val tips: String
)

//data class for observation
data class Observation(
    val id: Int,
    val title: String,
    val details: String,
    val date: String,
    val location: String
)
