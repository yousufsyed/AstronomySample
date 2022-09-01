package com.adyen.android.assignment.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.room.Room
import com.adyen.android.assignment.BuildConfig
import com.adyen.android.assignment.api.DefaultPlanetaryRestClient
import com.adyen.android.assignment.api.PlanetaryRestClient
import com.adyen.android.assignment.api.PlanetaryService
import com.adyen.android.assignment.dao.ASTRONOMY_DB
import com.adyen.android.assignment.dao.AstronomyDao
import com.adyen.android.assignment.dao.AstronomyDatabase
import com.adyen.android.assignment.provider.*
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule {

    @[Provides Singleton]
    fun provideContext(application: Application): Context = application.applicationContext

    @[Provides Singleton]
    fun getDatabase(context: Context): AstronomyDatabase {
        return Room.databaseBuilder(
            context,
            AstronomyDatabase::class.java,
            ASTRONOMY_DB
        ).build()
    }

    @Provides
    fun getNycSchoolsDao(database: AstronomyDatabase): AstronomyDao =
        database.astronomyDao()

    @[Provides Singleton]
    fun getOkHttpClient(): OkHttpClient = OkHttpClient
        .Builder()
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    @[Provides Singleton]
    fun getGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Provides
    fun providePlanetaryApi(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): PlanetaryService = Retrofit.Builder()
        .baseUrl(BuildConfig.NASA_BASE_URL)
        .addConverterFactory(gsonConverterFactory)
        .client(okHttpClient)
        .build()
        .create(PlanetaryService::class.java)

    @[Provides Singleton]
    fun getPlanetaryRestClient(impl: DefaultPlanetaryRestClient): PlanetaryRestClient = impl

    @[Provides Singleton]
    fun getDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider = impl

    @[Provides Singleton]
    fun getEventLogger(impl: DefaultEventLogger): EventLogger = impl

    @[Provides Singleton]
    fun networkAvailabilityProvider(impl: DefaultNetworkAvailabilityProvider): NetworkAvailabilityProvider =
        impl

    @[Provides Singleton]
    fun planetaryServiceProvider(impl: DefaultPlanetaryServiceProvider): PlanetaryServiceProvider =
        impl

    @[Provides Singleton]
    fun orderProvider(impl: DefaultOrderProvider): OrderProvider = impl

    @Provides
    fun networkStatusProvider(impl: NetworkConnectionMonitor): DefaultLifecycleObserver = impl
}