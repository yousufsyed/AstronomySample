package com.adyen.android.assignment.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

sealed class AstronomyInfo {

    data class Header(val title: String) : AstronomyInfo()

    @Parcelize
    data class PlanetaryData(
        val serviceVersion: String,
        val title: String,
        val explanation: String,
        val date: LocalDate,
        val hdUrl: String?,
        val url: String,
        val mediaType: String,
        val isFavorite: Boolean = false
    ) : AstronomyInfo(), Parcelable
}
