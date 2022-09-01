package com.adyen.android.assignment.api

import com.adyen.android.assignment.astronomyPictures
import com.adyen.android.assignment.planetaryErrorResponse
import com.adyen.android.assignment.planetarySuccessResponse
import com.adyen.android.assignment.provider.DispatcherProvider
import com.adyen.android.assignment.provider.NetworkAvailabilityProvider
import com.adyen.android.assignment.provider.NetworkConnectivityError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.DescribeSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Ignore
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class DefaultPlanetaryRestClientTest : DescribeSpec({

    val networkAvailabilityProvider: NetworkAvailabilityProvider = mock()
    val planetaryService: PlanetaryService = mock()
    val dispatcherProvider: DispatcherProvider = mock {
        on { io } doReturn UnconfinedTestDispatcher()
    }

    val planetaryRestClient = DefaultPlanetaryRestClient(
        planetaryService,
        dispatcherProvider,
        networkAvailabilityProvider
    )

    describe("planetaryRequestWhenNetworkGoesOffline") {
        runTest {
            whenever(networkAvailabilityProvider.isConnectedToNetwork()).thenReturn(false)

            it("expecting network exception to be thrown") {
                shouldThrow<NetworkConnectivityError> {
                    planetaryRestClient.getAstronomyPictures()
                }
            }
        }
    }

    describe(" planetaryRequestWhenNetworkReturnsError") {
        runTest {
            whenever(networkAvailabilityProvider.isConnectedToNetwork()).thenReturn(true)
            whenever(planetaryService.getPictures()).thenReturn(planetaryErrorResponse)

            it("expecting network exception to be thrown") {
                shouldThrowAny {
                    planetaryRestClient.getAstronomyPictures()
                }
            }
        }
    }

    xdescribe("whenPlanetaryResponseSucceeds") {
        runTest {
            whenever(networkAvailabilityProvider.isConnectedToNetwork()).thenReturn(true)
            whenever(planetaryService.getPictures()).thenReturn(planetarySuccessResponse)

            val result = planetaryRestClient.getAstronomyPictures()
            assertThat(result).isEqualTo(astronomyPictures)
        }
    }
})