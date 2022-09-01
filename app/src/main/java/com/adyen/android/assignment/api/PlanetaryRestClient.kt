package com.adyen.android.assignment.api

import com.adyen.android.assignment.api.model.AstronomyPicture
import com.adyen.android.assignment.provider.DispatcherProvider
import com.adyen.android.assignment.provider.NetworkAvailabilityProvider
import com.adyen.android.assignment.provider.NetworkConnectivityError
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface PlanetaryRestClient {
    suspend fun getAstronomyPictures() : List<AstronomyPicture>
}

class DefaultPlanetaryRestClient @Inject constructor(
    private val planetaryService: PlanetaryService,
    private val dispatcherProvider: DispatcherProvider,
    private val networkAvailabilityProvider: NetworkAvailabilityProvider
) : PlanetaryRestClient {

    override suspend fun getAstronomyPictures(): List<AstronomyPicture> {
        return withContext(dispatcherProvider.io) {
            if(networkAvailabilityProvider.isConnectedToNetwork()) {
                val response = planetaryService.getPictures()
                if(response.isSuccessful) {
                    val result = response.body()
                    if(result.isNullOrEmpty()) {
                        throw NoResultsFoundException()
                    }
                    result
                } else {
                    throw PlanetaryServiceException(response.errorBody().toString(), response.code())
                }
            } else {
                throw NetworkConnectivityError()
            }
        }
    }
}

class NoResultsFoundException : RuntimeException("No Result found.")

class PlanetaryServiceException(override val message: String, val code: Int) : RuntimeException()