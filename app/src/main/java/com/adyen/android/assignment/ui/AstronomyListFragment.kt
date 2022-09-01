package com.adyen.android.assignment.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.adyen.android.assignment.AstronomyViewModel
import com.adyen.android.assignment.AstronomyViewModelFactory
import com.adyen.android.assignment.R
import com.adyen.android.assignment.component
import com.adyen.android.assignment.data.AstronomyInfo.PlanetaryData
import com.adyen.android.assignment.data.PlanetaryState
import com.adyen.android.assignment.databinding.FragmentAstronomyListBinding
import com.adyen.android.assignment.provider.NetworkConnectivityError
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class AstronomyListFragment : Fragment(R.layout.fragment_astronomy_list) {

    @Inject
    lateinit var viewModelFactory: AstronomyViewModelFactory

    private val viewModel: AstronomyViewModel by activityViewModels { viewModelFactory }

    private val planetAdapter by lazy {
        PlanetaryAdapter(::showPlanetDetails)
    }

    private var _binding: FragmentAstronomyListBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAstronomyListBinding.bind(view)
        initViews()
        initObserver()
    }

    private fun initViews() {
        resetViews()
        with(binding) {
            swipeRefresh.setOnRefreshListener {
                viewModel.refresh()
            }

            astronomyListRv.apply {
                adapter = planetAdapter
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    false
                )
            }

            with(errorLayout) {
                networkSetting.setOnClickListener { launchNetworkSettings() }
                refresh.setOnClickListener { viewModel.refresh() }
            }

            reorderList.setOnClickListener {
                showOrderDialog()
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.planetaryStateFlow.collectLatest {
                    when (it) {
                        is PlanetaryState.LoadContent -> showContent(it)
                        is PlanetaryState.FullScreenError -> showError(it.error)
                        PlanetaryState.Loading -> showLoading()
                        PlanetaryState.Empty -> showEmptyState()
                        PlanetaryState.Completed -> {
                            // handle graceful completion, probably loading.... message
                        }

                    }
                }
            }
        }
    }

    private fun showLoading() {
        resetViews()
        binding.swipeRefresh.isRefreshing = true
    }

    private fun showEmptyState() {
        resetViews()
        binding.emptyStateTv.visibility = View.VISIBLE
    }

    private fun showContent(state: PlanetaryState.LoadContent) {
        resetViews()
        planetAdapter.submitList(state.results)
        with(binding) {
            swipeRefresh.visibility = View.VISIBLE
            astronomyListRv.visibility = View.VISIBLE
            reorderList.visibility = View.VISIBLE
        }
    }

    private fun showError(error: Throwable) {
        if (planetAdapter.itemCount > 0) {
            showSnackbarError(error)
        } else {
            showFullScreenError(error)
        }
    }

    private fun showFullScreenError(error: Throwable) {
        resetViews()
        Log.e("LIST", error.stackTrace.toString())
        binding.astronomyListRv.visibility = View.GONE
        with(binding.errorLayout) {
            if (error.isNetworkError) {
                errorTv1.setText(R.string.network_error_message1)
                errorTv2.setText(R.string.network_error_message2)
                networkSetting.visibility = View.VISIBLE
                refresh.visibility = View.GONE
            } else {
                errorTv1.setText(R.string.generic_error_message1)
                errorTv2.setText(R.string.generic_error_message2)
                refresh.visibility = View.VISIBLE
                networkSetting.visibility = View.GONE
            }
            errorContainer.visibility = View.VISIBLE
        }
    }

    private fun resetViews() {
        with(binding) {
            reorderList.visibility = View.GONE
            errorLayout.errorContainer.visibility = View.GONE
            emptyStateTv.visibility = View.GONE
            swipeRefresh.isRefreshing = false
        }
    }

    private fun showSnackbarError(error: Throwable) {
        val message = getString(
            if (error.isNetworkError) {
                R.string.network_error_message1
            } else {
                R.string.generic_error_message1
            }
        )
        binding.swipeRefresh.isRefreshing = false
        showSnackbar(message)
    }

    private fun launchNetworkSettings() {
        startActivity(
            Intent(Intent.ACTION_MAIN).apply {
                setClassName("com.android.phone", "com.android.phone.NetworkSetting")
            }
        )
    }

    private fun showPlanetDetails(planetaryData: PlanetaryData) {
        findNavController().navigate(
            AstronomyListFragmentDirections.actionShowPlanetDetails(planetaryData)
        )
    }

    private fun showOrderDialog() {
        findNavController().navigate(
            AstronomyListFragmentDirections.actionShowReorderList()
        )
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private val Throwable.isNetworkError: Boolean
        get() = when (this) {
            is NetworkConnectivityError -> true
            else -> false
        }
}