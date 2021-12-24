package com.treel.androidsdk.notification

import com.treel.androidsdk.R
import com.treel.androidsdk.database.Alert

class AlertNotification(alert: Alert) {

    private var mAlert: Alert? = null

    companion object {
        val TYPE_STACK = -1000
    }

    init {
        mAlert = alert
    }

    fun getAlert(): Alert {
        return mAlert!!
    }

    /*fun getBundle(): Bundle {
        if (mExtrasBundle == null) {
            mExtrasBundle = Bundle()
        }
        return mExtrasBundle!!
    }*/

    fun getAppName(): Int? {
        return R.string.app_name
    }

    fun getVinNumber(): String? {
        return mAlert!!.vinNumber
    }

    fun getAlertsText(): String? {
        return mAlert?.alertMsg
    }

    fun getUserNotificationGroup(): String? {
        return getVinNumber()
    }

}