package com.ice_opscpoe.featheredfriends
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EbirdApiService {
    @GET("ref/hotspot/geo")
    fun getNearbyHotspots(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("key") apiKey: String
    ): Call<List<BirdHotspot>>
//(Android Developers, 2024)

//REFERENCES
// Android Developers. (2024). Send a simple request. [online] Available at: https://developer.android.com/develop/connectivity/cronet/start [Accessed 28 Sep. 2024].
// Kaushal Vasava (2023). Retrofit in Android - Kaushal Vasava - Medium. [online] Medium. Available at: https://medium.com/@KaushalVasava/retrofit-in-android-5a28c8e988ce [Accessed 29 Sep. 2024].


 }

