package com.treel.androidsdkdemo.utility

import com.treel.androidsdk.database.VehicleConfiguration

object ConstantData {

    fun getVehicleConfigurations() : ArrayList<VehicleConfiguration>{
        val vehicleConfigurations: ArrayList<VehicleConfiguration> = ArrayList()
        val vehicleConfiguration = VehicleConfiguration()
        vehicleConfiguration.vinNumber = "VINNUMBER1"
        vehicleConfiguration.macAddress = "E12E4EAA21C3"
        vehicleConfiguration.tyrePosition = "1A"
        vehicleConfiguration.recommendedPressure = 30
        vehicleConfiguration.lowPressure = 25
        vehicleConfiguration.highPressure = 35
        vehicleConfiguration.highTemperature = 37
        vehicleConfigurations.add(vehicleConfiguration)

        val vehicleConfiguration1 = VehicleConfiguration()
        vehicleConfiguration1.vinNumber = "VINNUMBER1"
        vehicleConfiguration1.macAddress = "FCD9E9CCE895"
        vehicleConfiguration1.tyrePosition = "2A"
        vehicleConfiguration1.recommendedPressure = 29
        vehicleConfiguration1.lowPressure = 25
        vehicleConfiguration1.highPressure = 35
        vehicleConfiguration1.highTemperature = 36
        vehicleConfigurations.add(vehicleConfiguration1)

        val vehicleConfiguration3 = VehicleConfiguration()
        vehicleConfiguration3.vinNumber = "VINNUMBER2"
        vehicleConfiguration3.macAddress = "FB4F2BD0D397"
        vehicleConfiguration3.tyrePosition = "1A"
        vehicleConfiguration3.recommendedPressure = 31
        vehicleConfiguration3.lowPressure = 25
        vehicleConfiguration3.highPressure = 35
        vehicleConfiguration3.highTemperature = 36
        vehicleConfigurations.add(vehicleConfiguration3)

        val vehicleConfiguration4 = VehicleConfiguration()
        vehicleConfiguration4.vinNumber = "VINNUMBER2"
        vehicleConfiguration4.macAddress = "D6E7F7214658"
        vehicleConfiguration4.tyrePosition = "2A"
        vehicleConfiguration4.recommendedPressure = 32
        vehicleConfiguration4.lowPressure = 25
        vehicleConfiguration4.highPressure = 35
        vehicleConfiguration4.highTemperature = 36
        vehicleConfigurations.add(vehicleConfiguration4)

        return vehicleConfigurations
    }
}