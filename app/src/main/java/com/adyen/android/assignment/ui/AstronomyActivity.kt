package com.adyen.android.assignment.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.adyen.android.assignment.AstronomyViewModel
import com.adyen.android.assignment.AstronomyViewModelFactory
import com.adyen.android.assignment.R
import com.adyen.android.assignment.component
import com.adyen.android.assignment.databinding.ActivityAstronomyBinding
import javax.inject.Inject

class AstronomyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAstronomyBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var viewModelFactory: AstronomyViewModelFactory

    private val viewModel: AstronomyViewModel by viewModels { viewModelFactory }

    private val navController by lazy {
        findNavController(R.id.nav_host_fragment_container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAstronomyBinding.inflate(layoutInflater)
        component.inject(this)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}