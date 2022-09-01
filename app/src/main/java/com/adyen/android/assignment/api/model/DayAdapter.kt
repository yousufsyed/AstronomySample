package com.adyen.android.assignment.api.model

import java.time.LocalDate

class DayAdapter {

    fun toJson(date: LocalDate): String = date.toString()

    /**
     * Maps the [AstronomyPicture.date] json string to a [LocalDate]
     */
    fun fromJson(date: String): LocalDate = LocalDate.parse(date)
}