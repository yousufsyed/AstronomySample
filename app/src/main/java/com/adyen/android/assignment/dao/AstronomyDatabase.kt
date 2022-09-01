package com.adyen.android.assignment.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adyen.android.assignment.api.model.AstronomyPicture

const val ASTRONOMY_DB = "ASTRONOMY_database"

@Database(entities = [AstronomyPicture::class], version = 1, exportSchema = false)
abstract class AstronomyDatabase : RoomDatabase() {
    abstract fun astronomyDao(): AstronomyDao
}