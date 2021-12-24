package com.treel.androidsdk.database

import com.treel.androidsdk.utility.Utility.getCurrentDateAndTime

class TyreDetectionEvent {
    var id:Int?= null
    var vinNumber: String? = null
    var macAddress: String?= null
    var tyrePosition: String?= null
     var sensorData: String? = null
    var timeStamp: String = getCurrentDateAndTime()
    var lastLowPressureTimeStamp: String? = null
    var lastHighPressureTimeStamp: String? = null
    var lastHighTemperatureTimeStamp: String? = null
    var dataType: Int = 0
}