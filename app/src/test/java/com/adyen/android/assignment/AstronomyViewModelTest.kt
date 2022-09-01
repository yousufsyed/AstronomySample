package com.adyen.android.assignment

import com.adyen.android.assignment.data.AstronomyInfo
import com.adyen.android.assignment.data.PlanetaryState
import com.adyen.android.assignment.data.PlanetaryState.LoadContent
import com.adyen.android.assignment.provider.DispatcherProvider
import com.adyen.android.assignment.provider.EventLogger
import com.adyen.android.assignment.provider.PlanetaryServiceProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.*

@OptIn(ExperimentalCoroutinesApi::class)
class AstronomyViewModelTest : DescribeSpec({
    this.isolationMode = IsolationMode.InstancePerTest

    val testDispatcher = UnconfinedTestDispatcher()

    val planetaryServiceProvider: PlanetaryServiceProvider = mock()
    val eventLogger: EventLogger = mock()
    val dispatcherProvider: DispatcherProvider = mock {
        on { io } doReturn testDispatcher
    }

    val astronomyViewModel = AstronomyViewModel(
        planetaryServiceProvider,
        dispatcherProvider,
        eventLogger
    )

    describe("updatePlanetaryState") {
        whenever(planetaryServiceProvider.getAstronomyInfoAsFlow())
            .thenReturn(
                flow { emit(emptyList()) }
            )

        astronomyViewModel.updatePlanetaryState(planetaryData)
        verify(planetaryServiceProvider).updateFavorites(planetaryData)
    }

    describe("when view-model is initialized") {
        whenever(planetaryServiceProvider.getAstronomyInfoAsFlow())
            .thenReturn(
                flow { astronomyInfoList }
            )

        val result = astronomyViewModel.planetaryStateFlow.value

        it("should emit Completed state on the flow") {
            assertThat(result).isNotNull
            assertThat(result).isEqualTo(PlanetaryState.Completed)
        }

        it("should invoke getAstronomyInfoAsFlow on provider") {
            verify(planetaryServiceProvider).getAstronomyInfoAsFlow()
        }
    }

    describe("astronomyPicturesFetchFailed") {
        runTest {
            whenever(planetaryServiceProvider.getAstronomyInfoAsFlow())
                .thenReturn(
                    flow { astronomyInfoList }
                )
            whenever(planetaryServiceProvider.refreshAstronomyPictures()).thenThrow(RuntimeException())

            astronomyViewModel.refresh()

            it("should invoke refreshAstronomyPictures on provider") {
                verify(planetaryServiceProvider).refreshAstronomyPictures()
            }

            it("should emit error state") {
                val state = astronomyViewModel.planetaryStateFlow.value
                assertThat(state).isNotNull
                assertThat(state).isInstanceOfAny(PlanetaryState.FullScreenError::class.java)
            }
        }
    }

    describe("when network call to refresh astronomy Pictures is successful") {
        runTest {
            whenever(planetaryServiceProvider.getAstronomyInfoAsFlow())
                .thenReturn(flow { astronomyInfoList })

            astronomyViewModel.refresh()

            it("should invoke refreshAstronomyPictures on provider") {
                verify(planetaryServiceProvider).refreshAstronomyPictures()
            }

            it("should emit Completed state") {
                val state = astronomyViewModel.planetaryStateFlow.value
                assertThat(state).isNotNull
                assertThat(state).isInstanceOfAny(PlanetaryState.Completed::class.java)
            }
        }
    }

})