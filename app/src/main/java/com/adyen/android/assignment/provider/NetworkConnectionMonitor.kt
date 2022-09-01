package com.adyen.android.assignment.provider

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.adyen.android.assignment.provider.NetworkStatus.Connected
import com.adyen.android.assignment.provider.NetworkStatus.Disconnected
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class NetworkConnectionMonitor @Inject constructor(
    private val context: Application
) : DefaultLifecycleObserver {

    private lateinit var connectivityManagerCallback: NetworkCallback

    private val _networkStatusFlow = MutableStateFlow<NetworkStatus>(Connected)

    val networkStatusFlow: StateFlow<NetworkStatus> = _networkStatusFlow

    fun registerLifecycleOwner(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        val connectivityManagerCallback = getConnectivityManagerCallback()
        val networkRequest = NetworkRequest
            .Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, connectivityManagerCallback)
    }

    private fun getConnectivityManagerCallback() =
        object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val networkCapability = connectivityManager.getNetworkCapabilities(network)
                val available =
                    networkCapability?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        ?: false
                announceStatus(if (available) Connected else Disconnected)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                announceStatus(Disconnected)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                val available =
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                announceStatus(if (available) Connected else Disconnected)
            }
        }

    override fun onPause(owner: LifecycleOwner) {
        connectivityManager.unregisterNetworkCallback(connectivityManagerCallback)
        super.onPause(owner)
    }

    private fun announceStatus(networkStatus: NetworkStatus) {
        _networkStatusFlow.update { networkStatus }
    }

    val connectivityManager: ConnectivityManager
        get() = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}

sealed class NetworkStatus {
    object Connected : NetworkStatus()
    object Disconnected : NetworkStatus()
}