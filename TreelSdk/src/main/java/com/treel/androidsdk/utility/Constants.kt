package com.treel.androidsdk.utility

import android.os.Environment

/**
 * @author Nitin Karande
 */
object Constants {
    const val CAMERA = 0
    const val GALLERY = 1

    const val APP_NAME = "Treel"
    val ROOT_FOLDER = Environment.getExternalStorageDirectory().toString() + "/" + APP_NAME
    val BEACON_LOG_WRITER_DIR =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()
    val IMAGE_DIR = ROOT_FOLDER + "/images/"
    val CRASH_DIR = ROOT_FOLDER + "/crash_logs/"
    val HUD_LOG_WRITER_DIR = ROOT_FOLDER + "/Logs"
    val LOG_WRITER_DIR = ROOT_FOLDER + "/Logs/"
    val FCM_NOTIFICATION_GROUP = "FCMNOTIFICATION"
    val FCM_ACTION_APP_UPDATE = "HOME"
    val FCM_ACTION_CUSTOM = "ALERTS"


    const val PSI = "PSI"
    const val BAR = "BAR"
    const val KPA = "KPA"
    const val DISPLAY_PSI = "psi"
    const val DISPLAY_BAR = "bar"
    const val DISPLAY_KPA = "kPa"
    const val FAHRENHEIT = "F"
    const val CENTIGRADE = "C"
    const val DEGREE_Fahrenheit = "\u00b0" + "F"
    const val DEGREE_CENTIGRADE = "\u00b0" + "C"
    const val DEGREE_SYMBOL = "\u00b0"

    val NOTIFICATION_CHANNEL_ID_DEFAULT = "TREELCARE"
    val NOTIFICATION_CHANNEL_NAME_SMARTTYRE_MONITOR = "SmartTyre_Monitor"
    val NOTIFICATION_CHANNEL_ID_APP_ACTIVE = "TREELCARE_ACTIVE"

    val OVR_RANGE = 65356

    /**
     * Parameters Ranges
     */
    val TEMPERATURE_MIN = -40
    val TEMPERATURE_MAX = 125

    val PRESSURE_MIN = 0
    val PRESSURE_MAX = 217

    const val ODOMETER_UPDATE_REMINDER_DAYS = 30


    const val ALERT_HIGH_PRESSURE = 1
    const val ALERT_LOW_PRESSURE = 2
    const val ALERT_HIGH_TEMPERATURE = 3
    const val ALERT_TIRE_ROTATION = 4
    const val ALERT_REMINDER = 5
    const val ALERT_FCM_CUSTOM = 6


    const val FRONT = "Front"
    const val REAR = "Rear"
    const val FRONT_LEFT = "Front Left"
    const val FRONT_RIGHT = "Front Right"
    const val REAR_LEFT = "Rear Left"
    const val REAR_RIGHT = "Rear Right"
    const val STEPNEY = "Stepney"

    const val DECRYPTION_KEY = "#@Trl2018-lespl$"


    const val OS_ANDROID = "android"
    const val OS_iOS = 1
    const val BACK_TIME: Long = 500


    const val ERROR_ACCOUNT_NOT_VERIFIED = 505



    const val BLE_DATA_TYPE = 0
    const val CLOUD_DATA_TYPE = 1
    const val CLOUD_CONNECTED_DEVICE = "Connected"
    const val CLOUD_BLE_DEVICE = "CLOUD_BLE"
    const val LOCAL_BLE_DEVICE = "LOCAL_BLE"

    //SetPoint
    const val SETPOINT_HIGH_PRESSURE = 35
    const val SETPOINT_LOW_PRESSURE = 28
    const val SETPOINT_HIGH_TEMPERATURE = 60

    const val TYRE_SCANNING_TIMEOUT = 10 * 60 * 1000
    const val TYRE_UI_TIMEOUT = 10 * 60 * 1000 // Previous last was 3 min

    const val BLE_CLOUD_TIMEOUT = 1 * 60 * 1000

    const val IS_DUMMY_TAG = 0
    const val IS_VALID_TAG = 1
    const val DAY_BEFORE_0 = 0
    const val DAY_BEFORE_1 = 1
    const val DAY_BEFORE_15 = 15
    const val DATA_STATUS = "Live"

    const val RECOMMENDED_PRESSURE = 32
}