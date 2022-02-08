package com.treel.androidsdkdemo.utility

import com.treel.androidsdk.database.VehicleConfiguration

object ConstantData {

    fun getVehicleConfigurations() : ArrayList<VehicleConfiguration>{
        val vehicleConfigurations: ArrayList<VehicleConfiguration> = ArrayList()
        val vehicleConfiguration = VehicleConfiguration()
        vehicleConfiguration.vinNumber = "VINNUMBER1"
        vehicleConfiguration.macAddress = "C41D09405E98"
        vehicleConfiguration.tyrePosition = "1A"
        vehicleConfiguration.recommendedPressureSetPoint = 30
        vehicleConfiguration.lowPressureSetPoint  = 25
        vehicleConfiguration.highPressureSetPoint  = 35
        vehicleConfiguration.highTemperatureSetPoint  = 37
        vehicleConfigurations.add(vehicleConfiguration)

        val vehicleConfiguration1 = VehicleConfiguration()
        vehicleConfiguration1.vinNumber = "VINNUMBER1"
        vehicleConfiguration1.macAddress = "F321F4A8DEDF"
        vehicleConfiguration1.tyrePosition = "2A"
        vehicleConfiguration1.recommendedPressureSetPoint = 29
        vehicleConfiguration1.lowPressureSetPoint  = 25
        vehicleConfiguration1.highPressureSetPoint  = 35
        vehicleConfiguration1.highTemperatureSetPoint  = 36
        vehicleConfigurations.add(vehicleConfiguration1)

        val vehicleConfiguration3 = VehicleConfiguration()
        vehicleConfiguration3.vinNumber = "VINNUMBER2"
        vehicleConfiguration3.macAddress = "C4F2B6BED4DA"
        vehicleConfiguration3.tyrePosition = "1A"
        vehicleConfiguration3.recommendedPressureSetPoint = 31
        vehicleConfiguration3.lowPressureSetPoint  = 25
        vehicleConfiguration3.highPressureSetPoint  = 35
        vehicleConfiguration3.highTemperatureSetPoint  = 36
        vehicleConfigurations.add(vehicleConfiguration3)

        val vehicleConfiguration4 = VehicleConfiguration()
        vehicleConfiguration4.vinNumber = "VINNUMBER2"
        vehicleConfiguration4.macAddress = "D6E7F7214658"
        vehicleConfiguration4.tyrePosition = "2A"
        vehicleConfiguration4.recommendedPressureSetPoint = 32
        vehicleConfiguration4.lowPressureSetPoint  = 25
        vehicleConfiguration4.highPressureSetPoint  = 35
        vehicleConfiguration4.highTemperatureSetPoint  = 36
        vehicleConfigurations.add(vehicleConfiguration4)

        return vehicleConfigurations
    }
}