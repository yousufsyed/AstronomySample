package com.adyen.android.assignment.api.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.adyen.android.assignment.data.AstronomyInfo.PlanetaryData
import java.time.LocalDate

const val PLANETARY_TABLE_NAME = "planetary_table"

@Entity(tableName = PLANETARY_TABLE_NAME)
data class AstronomyPicture(
    @PrimaryKey
    @ColumnInfo val title: String,
    @ColumnInfo val explanation: String,
    @ColumnInfo val date: String,
    @ColumnInfo val media_type: String,
    @ColumnInfo val hdurl: String?,
    @ColumnInfo val url: String,
    @ColumnInfo val service_version: String,
    @ColumnInfo val isFavorite: Boolean = false
)

fun List<AstronomyPicture>.toModel() = map { it.toModel }

val AstronomyPicture.toModel: PlanetaryData
    get() = PlanetaryData(
        serviceVersion = service_version,
        title = title,
        explanation = explanation,
        date = LocalDate.parse(date),
        hdUrl = hdurl,
        url = url,
        mediaType = media_type,
        isFavorite = isFavorite
    )

val PlanetaryData.toDb: AstronomyPicture
    get() = AstronomyPicture(
        service_version = serviceVersion,
        title = title,
        explanation = explanation,
        date = date.toString(),
        hdurl = hdUrl,
        url = url,
        media_type = mediaType,
        isFavorite = isFavorite
    )