package com.adyen.android.assignment.api

import androidx.annotation.VisibleForTesting
import com.adyen.android.assignment.BuildConfig
import com.adyen.android.assignment.api.model.AstronomyPicture
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val COUNT = 20

interface PlanetaryService {
    /**
     * APOD - Astronomy Picture of the day.
     * See [the docs](https://api.nasa.gov/) and [github micro service](https://github.com/nasa/apod-api#docs-)
     */
    @GET("/planetary/apod")
    suspend fun getPictures(
        @Query("count") count: Int = COUNT,
        @Query("api_key") key: String = BuildConfig.API_KEY
    ): Response<List<AstronomyPicture>>

    companion object {

        @VisibleForTesting
        private val client = OkHttpClient
            .Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .build()

        @VisibleForTesting
        internal val instance: PlanetaryService = Retrofit.Builder()
            .baseUrl(BuildConfig.NASA_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PlanetaryService::class.java)
    }
}
