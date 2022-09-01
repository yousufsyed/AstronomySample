package com.adyen.android.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.adyen.android.assignment.data.AstronomyInfo.PlanetaryData
import com.adyen.android.assignment.data.PlanetaryState
import com.adyen.android.assignment.data.PlanetaryState.LoadContent
import com.adyen.android.assignment.data.PlanetaryState.Loading
import com.adyen.android.assignment.provider.DispatcherProvider
import com.adyen.android.assignment.provider.EventLogger
import com.adyen.android.assignment.provider.PlanetaryServiceProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AstronomyViewModel @Inject constructor(
    private val planetaryServiceProvider: PlanetaryServiceProvider,
    private val dispatcherProvider: DispatcherProvider,
    private val eventLogger: EventLogger
) : ViewModel(), EventLogger by eventLogger {

    private val _planetaryStateFlow = MutableStateFlow<PlanetaryState>(Loading)

    val planetaryStateFlow: StateFlow<PlanetaryState> = _planetaryStateFlow

    init {
        fetchAstronomyPictures{
            initObserver()
        }
    }

    private fun initObserver() {
        viewModelScope.launch(dispatcherProvider.io) {
            planetaryServiceProvider.getAstronomyInfoAsFlow()
                .collectLatest { planetaryData ->
                    if (planetaryData.isNotEmpty()){
                        updateState(LoadContent(planetaryData))
                    } else {
                        updateState(PlanetaryState.Empty)
                    }
                }
        }
    }

    fun updatePlanetaryState(planetaryData: PlanetaryData) {
        viewModelScope.launch(dispatcherProvider.io) {
            planetaryServiceProvider.updateFavorites(planetaryData)
        }
    }

    private fun fetchAstronomyPictures(onCompletionAction: () -> Unit = {}) {
        _planetaryStateFlow.update { Loading }
        viewModelScope.launch(dispatcherProvider.io) {
            runCatching { planetaryServiceProvider.getAstronomyPictures() }
                .fold(
                    onSuccess = {
                        updateState(PlanetaryState.Completed)
                        onCompletionAction()
                    },
                    onFailure = { updateState(PlanetaryState.FullScreenError(it)) }
                )
        }
    }

    fun refresh() {
        _planetaryStateFlow.update { Loading }
        viewModelScope.launch(dispatcherProvider.io) {
            runCatching { planetaryServiceProvider.refreshAstronomyPictures() }
                .fold(
                    onSuccess = { updateState(PlanetaryState.Completed) },
                    onFailure = { updateState(PlanetaryState.FullScreenError(it)) }
                )
        }
    }

    private fun updateState(state: PlanetaryState) {
        logMessage(state.javaClass.name)
        _planetaryStateFlow.update { state }
    }
}

@Suppress("UNCHECKED_CAST")
class AstronomyViewModelFactory @Inject constructor(
    private val viewModel: AstronomyViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AstronomyViewModel::class.java)) {
            return viewModel as T
        }
        throw IllegalArgumentException("Incorrect ViewModel attached")
    }
}