package com.adyen.android.assignment.di

import android.app.Application
import com.adyen.android.assignment.BuildConfig
import com.adyen.android.assignment.ui.AstronomyActivity
import com.adyen.android.assignment.ui.AstronomyListFragment
import com.adyen.android.assignment.ui.OrderDialogFragment
import com.adyen.android.assignment.ui.PlanetDetailsFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application
        ): AppComponent
    }

    fun inject(astronomyActivity: AstronomyActivity)
    fun inject(astronomyListFragment: AstronomyListFragment)
    fun inject(planetDetailsFragment: PlanetDetailsFragment)
    fun inject(orderDialogFragment: OrderDialogFragment)
}

class AppComponentNotFoundException : RuntimeException()