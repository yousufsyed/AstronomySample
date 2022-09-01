package com.adyen.android.assignment.data

sealed class PlanetaryState {
    object Loading : PlanetaryState()
    object Empty : PlanetaryState()
    object Completed : PlanetaryState()
    data class LoadContent(val results: List<AstronomyInfo>): PlanetaryState()
    data class FullScreenError(val error: Throwable) : PlanetaryState()
    data class PartialError(val error: Throwable) : PlanetaryState()
}
