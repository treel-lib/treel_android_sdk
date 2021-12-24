package com.treel.androidsdk.database

import com.treel.androidsdk.utility.Utility

class TpmsDetectionData {
    var vinNumber: String? = null
    var macAddress: String?= null
    var tyrePosition: String?= null
    var pressure: Int?= null
    var temperaure: Int?= null
    var battery: Int?= null
    var timeStamp: String = Utility.getCurrentDateAndTime()
}