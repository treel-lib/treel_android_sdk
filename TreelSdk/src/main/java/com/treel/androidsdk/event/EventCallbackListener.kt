package com.treel.androidsdk.event

import com.treel.androidsdk.notification.AlertNotification
import com.treel.androidsdk.database.VehicleConfiguration


interface EventCallbackListener {
    fun handleBleException(errorCode: Int?)
    fun onTpmsDataReceived(vehicleConfiguration: VehicleConfiguration)
    fun showAlertNotification(alertNotification: AlertNotification)
}