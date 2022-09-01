package com.adyen.android.assignment.dao

import androidx.room.*
import com.adyen.android.assignment.api.model.AstronomyPicture
import com.adyen.android.assignment.api.model.PLANETARY_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface AstronomyDao {

    @Query("SELECT * FROM $PLANETARY_TABLE_NAME WHERE isFavorite = :favorite")
    fun getPlanetsAsFlow(favorite: Boolean = false): Flow<List<AstronomyPicture>>

    @Query("SELECT * FROM $PLANETARY_TABLE_NAME WHERE title = :title")
    fun getPlanet(title: String): AstronomyPicture

    @Query("SELECT * FROM $PLANETARY_TABLE_NAME WHERE isFavorite = :favorite")
    fun getFavoritesAsFlow(favorite: Boolean = true): Flow<List<AstronomyPicture>>

    @Query("Select COUNT(*) FROM $PLANETARY_TABLE_NAME")
    fun getPlanetaryCount() : Int

    @Update
    fun updateFavorite(astronomyPicture: AstronomyPicture)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPlanets(planets: List<AstronomyPicture>)

    @Query("DELETE FROM $PLANETARY_TABLE_NAME")
    suspend fun deletePlanetary()
}