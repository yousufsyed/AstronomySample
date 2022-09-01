package com.adyen.android.assignment.provider

import com.adyen.android.assignment.api.PlanetaryRestClient
import com.adyen.android.assignment.api.model.toDb
import com.adyen.android.assignment.astronomyPictureList
import com.adyen.android.assignment.astronomyPictures
import com.adyen.android.assignment.dao.AstronomyDao
import com.adyen.android.assignment.data.AstronomyInfo
import com.adyen.android.assignment.planetaryData
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.inspectors.runTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.assertj.core.api.Assertions.assertThat
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class DefaultPlanetaryServiceProviderTest : DescribeSpec({

    this.isolationMode = IsolationMode.InstancePerTest

    val planetaryRestClient: PlanetaryRestClient = mock()
    val planetaryDao: AstronomyDao = mock()
    val orderProvider: OrderProvider = mock()
    val eventLogger: EventLogger = mock()

    val testScheduler = TestCoroutineScheduler()
    val testContext = TestScope(testScheduler)
    val dispatcherProvider: DispatcherProvider = mock {
        onBlocking { io } doReturn UnconfinedTestDispatcher(testScheduler)
    }

    val planetaryServiceProvider = DefaultPlanetaryServiceProvider(
            planetaryRestClient,
            dispatcherProvider,
            orderProvider,
            planetaryDao,
            eventLogger
        )

    describe("when getAstronomyPictures is invoked and DB is Empty") {
        runTest(testContext)  {
            whenever(planetaryDao.getPlanetaryCount()).thenReturn(0)
            whenever(planetaryRestClient.getAstronomyPictures()).thenReturn(astronomyPictures)

            planetaryServiceProvider.getAstronomyPictures()

            it("should should fetch planetary count from db") {
                verify(planetaryDao).getPlanetaryCount()
            }

            it("should invoke network call to fetch pictures") {
                verify(planetaryRestClient).getAstronomyPictures()
            }

            it("should insert the results to db") {
                verify(planetaryDao).insertAllPlanets(astronomyPictures)
            }
        }
    }

    describe("when getAstronomyPictures is invoked and DB has planetary data") {
        runTest(testContext) {
            whenever(planetaryDao.getPlanetaryCount()).thenReturn(4)
            whenever(planetaryRestClient.getAstronomyPictures()).thenReturn(astronomyPictures)

            planetaryServiceProvider.getAstronomyPictures()

            it("should invoke planetary count on db") {
                verify(planetaryDao).getPlanetaryCount()
            }

            it("should not invoke network call to fetch pictures") {
                verify(planetaryRestClient, never()).getAstronomyPictures()
            }

            it("should not invoke call to insert the results to db") {
                verify(planetaryDao, never()).insertAllPlanets(astronomyPictures)
            }
        }
    }

    describe("when refresh is invoked") {

        describe("when network call succeeds") {
            runTest(testContext) {
                whenever(planetaryRestClient.getAstronomyPictures()).thenReturn(astronomyPictures)

                planetaryServiceProvider.refreshAstronomyPictures()

                it("should invoke network call to fetch pictures") {
                    verify(planetaryRestClient).getAstronomyPictures()
                }

                it("should insert the results to db") {
                    verify(planetaryDao).insertAllPlanets(astronomyPictures)
                }
            }
        }

        describe("when network call fails") {
            runTest(testContext) {
                val error = RuntimeException("this should fail")
                whenever(planetaryRestClient.getAstronomyPictures()).thenThrow(error)

                it("should throw exception") {
                    shouldThrow<RuntimeException> {
                        planetaryServiceProvider.refreshAstronomyPictures()
                    }

                    it("should invoke network call to fetch pictures") {
                        verify(planetaryRestClient).getAstronomyPictures()
                    }

                    it("should not invoke insert call on db") {
                        verify(planetaryDao, never()).insertAllPlanets(astronomyPictures)
                    }
                }
            }
        }
    }

    describe("when update favorites is invoked") {
        runTest(testContext) {

            planetaryServiceProvider.updateFavorites(planetaryData)

            it("should trigger an update on db") {
                verify(planetaryDao).updateFavorite(planetaryData.toDb)
            }
        }
    }

    describe("when getAstronomyInfoAs flow is invoked") {
        runTest(testContext) {
            whenever(planetaryDao.getFavoritesAsFlow()).thenReturn(flow { emit(emptyList()) })
            whenever(planetaryDao.getPlanetsAsFlow()).thenReturn(flow { emit( astronomyPictureList ) })
            whenever(orderProvider.orderFlow).thenReturn(flow { emit(Order.NONE) })

            val result = planetaryServiceProvider.getAstronomyInfoAsFlow().toList().last()

            assertThat(result).isNotNull
            assertThat(result.size).isEqualTo(astronomyPictureList.size + 1)
            assertThat(result.first()).isInstanceOf(AstronomyInfo.Header::class.java)
            assertThat((result.first() as AstronomyInfo.Header).title).isEqualTo(LATEST)
        }
    }

    describe("when getAstronomyInfoAs flow is invoked but there is not data in db") {
        runTest(testContext) {
            whenever(planetaryDao.getFavoritesAsFlow()).thenReturn(flow { emit( emptyList() ) })
            whenever(planetaryDao.getPlanetsAsFlow()).thenReturn(flow { emit( emptyList() ) })
            whenever(orderProvider.orderFlow).thenReturn(flow { emit(Order.NONE) })

            val result = planetaryServiceProvider.getAstronomyInfoAsFlow().toList().last()

            assertThat(result).isEmpty()
        }
    }

})