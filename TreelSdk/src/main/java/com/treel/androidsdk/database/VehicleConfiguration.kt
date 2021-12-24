package com.treel.androidsdk.database

import com.treel.androidsdk.ble.Reading

class VehicleConfiguration() {
    var vinNumber: String? = null
    var macAddress: String?= null
    var tyrePosition: String?= null
    var recommendedPressure: Int?= null
    var lowPressure: Int?= null
    var highPressure: Int?= null
    var highTemperature:Int?=null
    var tireReading: Reading? = null
    var alerts: ArrayList<Alert>? = null

}