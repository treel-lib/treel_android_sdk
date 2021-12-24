package com.treel.androidsdk.exception

import android.util.Log
import com.polidea.rxandroidble2.exceptions.BleScanException
import java.util.*
import java.util.concurrent.TimeUnit

interface ExceptionHandling {

    fun handleBleException(bleScanException: BleScanException){
        val text:String

        when(bleScanException.reason){

            BleScanException.BLUETOOTH_NOT_AVAILABLE -> text = "Bluetooth is not available"
            BleScanException.BLUETOOTH_DISABLED -> text = "Enable bluetooth and try again"
            BleScanException.LOCATION_PERMISSION_MISSING -> text = "On Android 6.0 location permission is required. Implement Runtime Permissions"
            BleScanException.LOCATION_SERVICES_DISABLED -> text = "Location services needs to be enabled on Android 6.0"
            BleScanException.UNDOCUMENTED_SCAN_THROTTLE -> text = String.format(
                   Locale.getDefault(),
                   "Android 7+ does not allow more scans. Try in %d seconds",
                   secondsTill(bleScanException.retryDateSuggestion)
               )

            BleScanException.UNKNOWN_ERROR_CODE, BleScanException.BLUETOOTH_CANNOT_START -> text = "Unable to start scanning"
            else -> text = "Unable to start scanning"   }


        Log.e("Exception Interface","---------------",bleScanException)
    }

     fun secondsTill(retryDateSuggestion: Date?): Any?{
        return TimeUnit.MILLISECONDS.toSeconds(retryDateSuggestion!!.time - System.currentTimeMillis())

    }


//    fun showBleException(bleScanException: BleScanException) : BleScanException{
//     //   Log.e("dataEcpHand","----------",bleScanException)
//        return bleScanException
//
//    }

}