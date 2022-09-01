package com.adyen.android.assignment.provider

import com.adyen.android.assignment.api.PlanetaryRestClient
import com.adyen.android.assignment.api.model.AstronomyPicture
import com.adyen.android.assignment.api.model.toDb
import com.adyen.android.assignment.api.model.toModel
import com.adyen.android.assignment.dao.AstronomyDao
import com.adyen.android.assignment.data.AstronomyInfo
import com.adyen.android.assignment.data.AstronomyInfo.PlanetaryData
import com.adyen.android.assignment.sortByComparator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal const val LATEST = "Latest"
internal const val FAVORITE = "My Favorites"

interface PlanetaryServiceProvider {
    suspend fun getAstronomyInfoAsFlow(): Flow<List<AstronomyInfo>>

    suspend fun getAstronomyPictures()

    suspend fun refreshAstronomyPictures()

    suspend fun updateFavorites(item: PlanetaryData)
}

class DefaultPlanetaryServiceProvider @Inject constructor(
    private val planetaryRestClient: PlanetaryRestClient,
    private val dispatcherProvider: DispatcherProvider,
    private val orderProvider: OrderProvider,
    private val planetsDao: AstronomyDao,
    private val eventLogger: EventLogger
) : PlanetaryServiceProvider, EventLogger by eventLogger {

    private val orderFlow by lazy { orderProvider.orderFlow }

    private val latestFlow by lazy { planetsDao.getPlanetsAsFlow() }

    private val favoritesFlow by lazy { planetsDao.getFavoritesAsFlow() }

    override suspend fun getAstronomyPictures() {
        withContext(dispatcherProvider.io) {
            if (planetsDao.getPlanetaryCount() == 0) {
                refreshAstronomyPictures()
            }
        }
    }

    override suspend fun refreshAstronomyPictures() {
        withContext(dispatcherProvider.io) {
            val results = planetaryRestClient.getAstronomyPictures()
            planetsDao.insertAllPlanets(results)
        }
    }

    override suspend fun updateFavorites(item: PlanetaryData) {
        withContext(dispatcherProvider.io) {
            planetsDao.updateFavorite(item.toDb)
        }
    }

    override suspend fun getAstronomyInfoAsFlow(): Flow<List<AstronomyInfo>> {
        return withContext(dispatcherProvider.io) {
            combine(latestFlow, favoritesFlow, orderFlow) { latest, favorites, order ->
                mutableListOf<AstronomyInfo>().apply {
                    if (latest.isNotEmpty() || favorites.isNotEmpty()) {

                        val comparator = order.toComparator

                        val sortedFavorites = getFavoritesListAsPlanetaryData(favorites, comparator)
                        addAll(sortedFavorites)

                        val sortedLatest = getLatestListAsPlanetaryData(latest, comparator)
                        addAll(sortedLatest)
                    }
                }
            }
        }
    }

    private fun getFavoritesListAsPlanetaryData(
        favorites: List<AstronomyPicture>,
        comparator: Comparator<PlanetaryData>?
    ): List<AstronomyInfo> = mutableListOf<AstronomyInfo>().apply {
        if (favorites.isNotEmpty()) {
            val sortedFavoritesModel = favorites.toModel().sortByComparator(comparator)
            add(AstronomyInfo.Header(FAVORITE))
            addAll(sortedFavoritesModel)
        } else {
            logMessage("No favorites available")
        }
    }

    private fun getLatestListAsPlanetaryData(
        latest: List<AstronomyPicture>,
        comparator: Comparator<PlanetaryData>?
    ): MutableList<AstronomyInfo> = mutableListOf<AstronomyInfo>().apply {
        if (latest.isNotEmpty()) {
            val sortedLatestModel = latest.toModel().sortByComparator(comparator)
            add(AstronomyInfo.Header(LATEST))
            addAll(sortedLatestModel)
        } else {
            logMessage("No results available.")
        }
    }
}