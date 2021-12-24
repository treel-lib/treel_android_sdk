package com.treel.androidsdk.database

import com.treel.androidsdk.utility.Utility.getCurrentDateAndTime
import com.treel.androidsdk.utility.Utility.getCurrentDateTime

class Alert {
    var id: Long = 0
    var vinNumber: String? = null
    var macAddress: String? = null
    var tyrePosition: String? = null
    var alertType: Int = -1
    var timeStamp: String = getCurrentDateAndTime()
    var isViewed: Boolean = false // Is this alert is viewed by the user, viewed alerts should not be displayed to user again
    var alertMsg: String? = null
    var updateTimeStamp: String? = getCurrentDateTime()
}