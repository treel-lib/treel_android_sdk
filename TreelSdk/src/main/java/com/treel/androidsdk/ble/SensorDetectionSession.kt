package com.treel.androidsdk.ble

import com.treel.androidsdk.database.ConfigDataBaseHandler
import com.treel.androidsdk.database.VehicleConfiguration
import com.treel.androidsdk.utility.Constants.BLE_DATA_TYPE

object SensorDetectionSession {

    private var configurations = ArrayList<VehicleConfiguration>()


    fun addConfigurations(
        configurations: ArrayList<VehicleConfiguration>,
        configDataBaseHandler: ConfigDataBaseHandler?
    ) {
        if (configurations.isNotEmpty()) {
            //clear()
        }
        this.configurations = configurations
        loadLastReadingsIfExist(configDataBaseHandler!!)

    }

    /**
     * Loading Reading and Alerts for Tyres which is scanned in the background. If these readings are latest (Within 4 minutes) app will display these sensor data on the ui.
     * It will be refreshed once real time data from the sensors will be received from the sensors.
     */
    private fun loadLastReadingsIfExist(configDataBaseHandler: ConfigDataBaseHandler) {
        for (configuration in configurations) {
            configuration.apply {
                 configDataBaseHandler.getTyreDetectionEvent(macAddress!!)?.let {
                     //If the sensor was detected within last 4 minutes, App will display the sensors data on tyre's ui
                     //if (isDetectedWithinLast2minutes(it.timeStamp) || it.dataType == CLOUD_DATA_TYPE) {
                     val sensorData = it.sensorData?.split(",")
                     sensorData?.let {it1->
                         val reading = Reading(battery = sensorData[2].toInt(), currentPressure = sensorData[0].toInt(), currentTemperature = sensorData[1].toInt(), dataType = BLE_DATA_TYPE, lastReportedTime = it.timeStamp)
                         tireReading = reading
                     }

                 }
            }
        }
    }

    fun getConfigurations() = configurations

    fun getReading(tagID: String): Reading? {
        for (configuration in configurations) {
            if (configuration.macAddress == tagID) return configuration.tireReading
        }
        return null
    }

    /*fun clear() {
        Timber.d("Timeout clearing")
        for (configuration in configurations) {
            configuration.stopTimeoutTimer()
        }
    }*/


}