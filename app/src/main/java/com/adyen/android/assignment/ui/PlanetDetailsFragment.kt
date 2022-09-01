package com.adyen.android.assignment.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.adyen.android.assignment.*
import com.adyen.android.assignment.data.AstronomyInfo.PlanetaryData
import com.adyen.android.assignment.databinding.FragmentPlanetDetailsBinding
import javax.inject.Inject

class PlanetDetailsFragment : Fragment(R.layout.fragment_planet_details) {

    @Inject
    lateinit var viewModelFactory: AstronomyViewModelFactory

    private val viewModel: AstronomyViewModel by activityViewModels { viewModelFactory }

    private var _binding: FragmentPlanetDetailsBinding? = null

    private val args: PlanetDetailsFragmentArgs by navArgs()

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPlanetDetailsBinding.bind(view)
        bind(args.data)
    }

    private fun bind(data: PlanetaryData){
        with(binding) {
            planetIv.load(data.hdUrl)
            title.text = data.title
            date.text = data.date.toString()
            explanation.text = data.explanation

            favoriteIv.apply {
                tag = data
                favoriteIv.isSelected = data.isFavorite
                setOnClickListener { updateFavorite() }
            }
        }
    }

    private fun updateFavorite() {
        with(binding.favoriteIv) {
            (tag as? PlanetaryData)?.let {
                val newData = it.copy(isFavorite = !it.isFavorite)

                tag = newData
                isSelected = newData.isFavorite
                viewModel.updatePlanetaryState(newData)
            }
        }
    }

}