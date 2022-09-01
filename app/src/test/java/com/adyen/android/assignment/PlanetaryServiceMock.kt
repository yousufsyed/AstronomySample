package com.adyen.android.assignment

import com.adyen.android.assignment.api.model.AstronomyPicture
import com.adyen.android.assignment.api.model.toModel
import com.adyen.android.assignment.data.AstronomyInfo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.time.LocalDate

val astronomyPicture = AstronomyPicture(
    service_version="v1",
    title="Stars and Mars",
    explanation="Wandering through the evening sky...",
    date= "2008-05-10",
    media_type="image",
    hdurl="https://apod.nasa.gov/apod/image/0805/Triplets_zubenel_n.jpg",
    url="https://apod.nasa.gov/apod/image/0805/Triplets_zubenel_c800.jpg"
)

val planetaryData = astronomyPicture.toModel

val astronomyPictures by lazy { listOf(astronomyPicture) }

private const val rawSuccessResponse = "Response{protocol=h2, code=200, message=, url=https://api.nasa.gov/planetary/apod?count=20&api_key=cqCuowRl9CrdfyeLN9TdVkwkMHnxabNqQ3NYLtwU}"
private const val rawErrorResponse = "Response{protocol=h2, code=404, message=not-found, url=https://api.nasa.gov/planetary/apod?count=20&api_key=cqCuowRl9CrdfyeLN9TdVkwkMHnxabNqQ3NYLtwU}"

val planetarySuccessResponse: Response<List<AstronomyPicture>> =
    Response.success(
        200,
        astronomyPictures
    )

val planetaryErrorResponse: Response<List<AstronomyPicture>> =
    Response.error(
        404,
        rawErrorResponse.toResponseBody("application/json".toMediaType())
    )