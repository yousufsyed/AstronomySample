package com.adyen.android.assignment.provider

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.*
import javax.inject.Inject

interface NetworkAvailabilityProvider {
    fun isConnectedToNetwork(): Boolean
}

class DefaultNetworkAvailabilityProvider @Inject constructor(
    private val context: Context
) : NetworkAvailabilityProvider {

    override fun isConnectedToNetwork(): Boolean {
        val cm = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager)
        return cm?.getNetworkCapabilities(cm.activeNetwork)?.let {
            it.hasTransport(TRANSPORT_CELLULAR) || it.hasTransport(TRANSPORT_WIFI) || it.hasTransport(
                TRANSPORT_ETHERNET
            )
        } ?: false
    }
}

class NetworkConnectivityError : RuntimeException()