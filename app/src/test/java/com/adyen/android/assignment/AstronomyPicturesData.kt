package com.adyen.android.assignment

import com.adyen.android.assignment.api.model.AstronomyPicture
import com.adyen.android.assignment.api.model.toModel


val astronomyPictureList = listOf(
    AstronomyPicture(
        date = "2006-09-04",
        explanation = "Where does dust collect in galaxies?",
        hdurl = "https://apod.nasa.gov/apod/image/0609/lmc_spitzer_big.jpg",
        media_type = "image",
        service_version = "v1",
        title = "The Large Magellanic Cloud in Infrared",
        url = "https://apod.nasa.gov/apod/image/0609/lmc_spitzer.jpg"
    ),
    AstronomyPicture(
        date = "2020-09-13",
        explanation = "Are stars better appreciated for their art after they die?",
        hdurl = "https://apod.nasa.gov/apod/image/2009/M2D9_HubbleSchmidt_985.jpg",
        media_type = "image",
        service_version = "v1",
        title = "M2-9: Wings of a Butterfly Nebula",
        url = "https://apod.nasa.gov/apod/image/2009/M2D9_HubbleSchmidt_985.jpg"
    ),
    AstronomyPicture(
        date = "2011-10-15",
        explanation = "Magnificent spiral galaxy NGC 4565 is viewed edge-on from planet Earth. ",
        hdurl = "https://apod.nasa.gov/apod/image/1110/ngc4565big_franke.jpg",
        media_type = "image",
        service_version = "v1",
        title = "NGC 4565: Galaxy on Edge",
        url = "https://apod.nasa.gov/apod/image/1110/ngc4565_franke.jpg"
    ),
    AstronomyPicture(
        date = "2005-04-05",
        explanation = "Light emitted by a planet far beyond our Solar System has been identified",
        hdurl = "https://apod.nasa.gov/apod/image/0504/planeteclipse_spitzer_big.jpg",
        media_type = "image",
        service_version = "v1",
        title = "Light From A Distant Planet",
        url = "https://apod.nasa.gov/apod/image/0504/planeteclipse_spitzer.jpg"
    ),
    AstronomyPicture(
        date = "1998-12-26",
        explanation = "The bright object in the center of the false color image above is quasar 3C279",
        hdurl = "https://apod.nasa.gov/apod/image/gamma_3c279_egret.gif",
        media_type = "image",
        service_version = "v1",
        title = "Gamma-Ray Quasar",
        url = "https://apod.nasa.gov/apod/image/gamma_3c279_egret.gif"
    )
)

val astronomyInfoList = astronomyPictureList.map { it.toModel }

