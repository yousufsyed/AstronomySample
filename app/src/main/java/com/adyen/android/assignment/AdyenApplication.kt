package com.adyen.android.assignment

import android.app.Application
import com.adyen.android.assignment.di.AppComponent
import com.adyen.android.assignment.di.DaggerAppComponent

class AdyenApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

}