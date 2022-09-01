package com.adyen.android.assignment.provider

import android.util.Log
import javax.inject.Inject

private const val TAG = "EventLogger"

interface EventLogger {

    fun logError(throwable: Throwable)

    fun logSuccess(message: String)

    fun logMessage(message: String)
}

class DefaultEventLogger @Inject constructor() : EventLogger {

    override fun logError(throwable: Throwable) {
        Log.e(TAG, throwable.message ?: "Unknown error occurred")
    }

    override fun logSuccess(message: String) {
        Log.d(TAG, message)
    }

    override fun logMessage(message: String) {
        Log.d(TAG, message)
    }

}