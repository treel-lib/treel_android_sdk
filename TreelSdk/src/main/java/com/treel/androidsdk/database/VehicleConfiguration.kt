package com.treel.androidsdk.database

import com.treel.androidsdk.ble.Reading

class VehicleConfiguration() {
    var vinNumber: String? = null
    var macAddress: String?= null
    var tyrePosition: String?= null
    var recommendedPressureSetPoint: Int?= null
    var lowPressureSetPoint : Int?= null
    var highPressureSetPoint : Int?= null
    var highTemperatureSetPoint :Int?=null
    var tireReading: Reading? = null
    var alerts: ArrayList<Alert>? = null

}