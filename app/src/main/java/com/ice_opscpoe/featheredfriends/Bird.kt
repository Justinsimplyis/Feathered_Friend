package com.ice_opscpoe.featheredfriends

//data class for Birds of the day
data class Bird(
    val name: String,
    val imageResourceId: Int,
    val description: String,
    val tips: String
)
//data class for favorite birds()
data class FavoriteBird(
    val name: String,
    val imageUri: String
)
