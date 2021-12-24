package com.treel.androidsdk.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.lang.RuntimeException
import java.lang.StringBuilder

class ConfigDataBaseHandler(val context: Context) :
    SQLiteOpenHelper(context, DATABASENAME, null, 1) {


    override fun onCreate(db: SQLiteDatabase?) {
        val createTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_VEHICLE_CONFIGURATION($COL_ID INTEGER PRIMARY KEY,$COL_VIN_NUMBER TEXT,$COL_MAC_ADDRESS TEXT,$COL_TYRE_POSITION TEXT,$COL_REC_PRESSURE INTEGER,$COL_LOW_PRESSURE INTEGER,$COL_HIGH_PRESSURE INTEGER,$COL_HIGH_TEMPRATURE INTEGER,$COL_DATE_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP  ) "

        db?.execSQL(createTable)
        db?.execSQL("CREATE UNIQUE INDEX  IF NOT EXISTS index_mac_address ON $TABLE_VEHICLE_CONFIGURATION ($COL_MAC_ADDRESS)")
        val tyreDetectionTable =
            "CREATE TABLE IF NOT EXISTS $TABLE_TYRE_DETECTION($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT  ,$COL_VIN_NUMBER TEXT,$COL_MAC_ADDRESS TEXT ,$COL_TYRE_POSITION TEXT, $COL_SENSOR_DATA_TD TEXT,$COL_DATE_TIMESTAMP TEXT,$COL_LAST_LOW_PRESSURE_TD TEXT,$COL_LAST_HIGH_PRESSURE_TD TEXT,$COL_LAST_HIGH_TEMPRATURE_TD TEXT,$COL_DATA_TYPE_TD INTEGER )"
        db?.execSQL(tyreDetectionTable)
        db?.execSQL("CREATE UNIQUE INDEX  IF NOT EXISTS index_detection_mac_address ON $TABLE_TYRE_DETECTION ($COL_MAC_ADDRESS)")
        val table_alert =
            "CREATE TABLE  IF NOT EXISTS $TABLE_ALERTS ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_MAC_ADDRESS TEXT, $COL_VIN_NUMBER TEXT,$COL_TYRE_POSITION TEXT,$COL_ALERT_TYPE_AT INTEGER,$COL_TIMAESTAMP_AT TEXT,$COL_ISVIEWED_AT INTEGER,$COL_ALERT_MESSAGE_AT TEXT,$COL_UPDATE_TIMESTAMP_AT TEXT)"
        db?.execSQL(table_alert)
        db?.execSQL("CREATE UNIQUE INDEX  IF NOT EXISTS index_tyre_position_mac_address_timeStamp ON $TABLE_ALERTS ($COL_ALERT_TYPE_AT, $COL_MAC_ADDRESS, $COL_TIMAESTAMP_AT)")

        val tpmsHistory =
            "CREATE TABLE IF NOT EXISTS $TABLE_TPMS_DATA_HISTORY ($COL_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COL_MAC_ADDRESS TEXT, $COL_VIN_NUMBER TEXT,$COL_TYRE_POSITION TEXT,$COL_PRESSURE INTEGER,$COL_TEMPERATURE INTEGER,$COL_BATTERY INTEGER,$COL_DATE_TIMESTAMP TEXT)"
        db?.execSQL(tpmsHistory)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun insertTag(vehicleConfiguration: VehicleConfiguration): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_VIN_NUMBER, vehicleConfiguration.vinNumber)
        contentValues.put(COL_MAC_ADDRESS, vehicleConfiguration.macAddress)
        contentValues.put(COL_TYRE_POSITION, vehicleConfiguration.tyrePosition)
        contentValues.put(COL_REC_PRESSURE, vehicleConfiguration.recommendedPressure)
        contentValues.put(COL_LOW_PRESSURE, vehicleConfiguration.lowPressure)
        contentValues.put(COL_HIGH_PRESSURE, vehicleConfiguration.highPressure)
        contentValues.put(COL_HIGH_TEMPRATURE, vehicleConfiguration.highTemperature)
        val id = db.insertWithOnConflict(
            TABLE_VEHICLE_CONFIGURATION,
            null,
            contentValues,
            SQLiteDatabase.CONFLICT_IGNORE
        )
        if (id == "-1".toLongOrNull()) {
            db.update(
                TABLE_VEHICLE_CONFIGURATION,
                contentValues,
                "$COL_MAC_ADDRESS=?",
                arrayOf(vehicleConfiguration.macAddress)
            )
        }
        db.close()
        return id
    }


    fun getVehicleConfigure(): List<VehicleConfiguration> {
        val vehicleConfigurations: ArrayList<VehicleConfiguration> =
            ArrayList<VehicleConfiguration>()

        val query = "SELECT * FROM $TABLE_VEHICLE_CONFIGURATION"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(query, null)
        } catch (e: SQLiteException) {
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                var vehicleConfiguration = VehicleConfiguration()
                vehicleConfiguration.vinNumber =
                    cursor.getString(cursor.getColumnIndex(COL_VIN_NUMBER))
                vehicleConfiguration.macAddress =
                    cursor.getString(cursor.getColumnIndex(COL_MAC_ADDRESS))
                vehicleConfiguration.tyrePosition =
                    cursor.getString(cursor.getColumnIndex(COL_TYRE_POSITION))
                vehicleConfiguration.recommendedPressure =
                    cursor.getInt(cursor.getColumnIndex(COL_REC_PRESSURE))
                vehicleConfiguration.lowPressure =
                    cursor.getInt(cursor.getColumnIndex(COL_LOW_PRESSURE))
                vehicleConfiguration.highPressure =
                    cursor.getInt(cursor.getColumnIndex(COL_HIGH_PRESSURE))
                vehicleConfiguration.highTemperature =
                    cursor.getInt(cursor.getColumnIndex(COL_HIGH_TEMPRATURE))
                vehicleConfigurations.add(vehicleConfiguration)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return vehicleConfigurations
    }

    fun getTyreConfiguration(vinNumber: String, tyrePosition: String): VehicleConfiguration? {
        //var getVehicleConfigList:ArrayList<VehicleConfiguration> = ArrayList<VehicleConfiguration>()

        val query =
            "SELECT * FROM $TABLE_VEHICLE_CONFIGURATION  where $COL_VIN_NUMBER = ? AND $COL_TYRE_POSITION = ?, arrayOf($vinNumber,$tyrePosition)"
        val db = this.readableDatabase
        val cursor: Cursor? = null
        val vehicleConfiguration = VehicleConfiguration()
        try {
            db.execSQL(query)
        } catch (e: SQLiteException) {
            db.execSQL(query)
            return null
        }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                vehicleConfiguration.vinNumber =
                    cursor.getString(cursor.getColumnIndex(COL_VIN_NUMBER))
                vehicleConfiguration.macAddress =
                    cursor.getString(cursor.getColumnIndex(COL_MAC_ADDRESS))
                vehicleConfiguration.tyrePosition =
                    cursor.getString(cursor.getColumnIndex(COL_TYRE_POSITION))
                vehicleConfiguration.recommendedPressure =
                    cursor.getInt(cursor.getColumnIndex(COL_REC_PRESSURE))
                vehicleConfiguration.lowPressure =
                    cursor.getInt(cursor.getColumnIndex(COL_LOW_PRESSURE))
                vehicleConfiguration.highPressure =
                    cursor.getInt(cursor.getColumnIndex(COL_HIGH_PRESSURE))
                vehicleConfiguration.highTemperature =
                    cursor.getInt(cursor.getColumnIndex(COL_HIGH_TEMPRATURE))
            }
        }
        cursor?.close()
        return vehicleConfiguration
    }

    fun saveTyreDetectionEvent(tyreDetectionEvent: TyreDetectionEvent): Long {
        val db = this.writableDatabase
        val contentValues1 = ContentValues()
        contentValues1.put(COL_MAC_ADDRESS, tyreDetectionEvent.macAddress)
        contentValues1.put(COL_VIN_NUMBER, tyreDetectionEvent.vinNumber)
        contentValues1.put(COL_TYRE_POSITION, tyreDetectionEvent.tyrePosition)
        contentValues1.put(COL_SENSOR_DATA_TD, tyreDetectionEvent.sensorData)
        contentValues1.put(COL_DATE_TIMESTAMP, tyreDetectionEvent.timeStamp)
        contentValues1.put(COL_LAST_LOW_PRESSURE_TD, tyreDetectionEvent.lastLowPressureTimeStamp)
        contentValues1.put(COL_LAST_HIGH_PRESSURE_TD, tyreDetectionEvent.lastHighPressureTimeStamp)
        contentValues1.put(COL_LAST_HIGH_TEMPRATURE_TD, tyreDetectionEvent.lastHighTemperatureTimeStamp)
        contentValues1.put(COL_DATA_TYPE_TD, tyreDetectionEvent.dataType)
        val id = db.insertWithOnConflict(
            TABLE_TYRE_DETECTION,
            null,
            contentValues1,
            SQLiteDatabase.CONFLICT_IGNORE
        )
        if (id == "-1".toLongOrNull()) {
            db.update(
                TABLE_TYRE_DETECTION,
                contentValues1,
                "$COL_MAC_ADDRESS=?",
                arrayOf(tyreDetectionEvent.macAddress)
            )
        }
        db.close()
        return id
    }

    fun getTyreDetectionEvent(macAddress: String): TyreDetectionEvent? {
        val tyreDetectionEvent = TyreDetectionEvent()
        val query = "SELECT * FROM $TABLE_TYRE_DETECTION  where $COL_MAC_ADDRESS = ?"
        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            cursor = db.rawQuery(query, arrayOf(macAddress))
        } catch (e: SQLiteException) {
            db.execSQL(query)
            return null
        }
        if (cursor != null) {

            if (cursor.moveToFirst()) {
                // do {
                tyreDetectionEvent.vinNumber =
                    cursor.getString(cursor.getColumnIndex(COL_VIN_NUMBER))
                tyreDetectionEvent.macAddress =
                    cursor.getString(cursor.getColumnIndex(COL_MAC_ADDRESS))
                tyreDetectionEvent.tyrePosition =
                    cursor.getString(cursor.getColumnIndex(COL_TYRE_POSITION))
                tyreDetectionEvent.sensorData =
                    cursor.getString(cursor.getColumnIndex(COL_SENSOR_DATA_TD))
                tyreDetectionEvent.timeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_DATE_TIMESTAMP))
                tyreDetectionEvent.lastLowPressureTimeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_LAST_LOW_PRESSURE_TD))
                tyreDetectionEvent.lastHighPressureTimeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_LAST_HIGH_PRESSURE_TD))
                tyreDetectionEvent.lastHighTemperatureTimeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_LAST_HIGH_TEMPRATURE_TD))
                tyreDetectionEvent.dataType = cursor.getInt(cursor.getColumnIndex(COL_DATA_TYPE_TD))
                // } while (cursor.moveToNext())
            }
        }
        cursor?.close()
        return tyreDetectionEvent
    }

    //Alert Show Code
    fun insertAlert(alert: Alert): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_MAC_ADDRESS, alert.macAddress)
        contentValues.put(COL_VIN_NUMBER, alert.vinNumber)
        contentValues.put(COL_TYRE_POSITION, alert.tyrePosition)
        contentValues.put(COL_ALERT_TYPE_AT, alert.alertType)
        contentValues.put(COL_TIMAESTAMP_AT, alert.timeStamp)
        contentValues.put(COL_ISVIEWED_AT, alert.isViewed)
        contentValues.put(COL_ALERT_MESSAGE_AT, alert.alertMsg)
        contentValues.put(COL_UPDATE_TIMESTAMP_AT, alert.updateTimeStamp)


        val id = db.insertWithOnConflict(
            TABLE_ALERTS,
            null,
            contentValues,
            SQLiteDatabase.CONFLICT_IGNORE
        )
        if (id == "-1".toLongOrNull()) {
            db.update(
                TABLE_ALERTS,
                contentValues,
                "$COL_MAC_ADDRESS=?",
                arrayOf(alert.macAddress)
            )
        }

        db.close()
        return id
    }


    fun saveTpmsDataHistory(tpmsDetectionData: TpmsDetectionData): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_VIN_NUMBER, tpmsDetectionData.vinNumber)
        contentValues.put(COL_MAC_ADDRESS, tpmsDetectionData.macAddress)
        contentValues.put(COL_TYRE_POSITION, tpmsDetectionData.tyrePosition)
        contentValues.put(COL_PRESSURE, tpmsDetectionData.pressure)
        contentValues.put(COL_TEMPERATURE, tpmsDetectionData.temperaure)
        contentValues.put(COL_BATTERY, tpmsDetectionData.battery)
           contentValues.put(COL_DATE_TIMESTAMP, tpmsDetectionData.timeStamp)
        val id = db.insertWithOnConflict(
            TABLE_TPMS_DATA_HISTORY,
            null,
            contentValues,
            SQLiteDatabase.CONFLICT_IGNORE
        )
        db.close()
        return id
    }

    fun tyreDetectionEventsByVinNumber(vinNumber:  String): List<TyreDetectionEvent> {
        val tyreDetectionEvents: ArrayList<TyreDetectionEvent> = ArrayList<TyreDetectionEvent>()
        val query = "SELECT * FROM $TABLE_TYRE_DETECTION  where $COL_VIN_NUMBER = ?"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(query, arrayOf(vinNumber))
        } catch (e: SQLiteException) {
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                var tyreDetectionEvent = TyreDetectionEvent()
                tyreDetectionEvent.vinNumber =
                    cursor.getString(cursor.getColumnIndex(COL_VIN_NUMBER))
                tyreDetectionEvent.macAddress =
                    cursor.getString(cursor.getColumnIndex(COL_MAC_ADDRESS))
                tyreDetectionEvent.tyrePosition =
                    cursor.getString(cursor.getColumnIndex(COL_TYRE_POSITION))
                tyreDetectionEvent.sensorData =
                    cursor.getString(cursor.getColumnIndex(COL_SENSOR_DATA_TD))
                tyreDetectionEvent.timeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_DATE_TIMESTAMP))
                tyreDetectionEvent.lastLowPressureTimeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_LAST_LOW_PRESSURE_TD))
                tyreDetectionEvent.lastHighPressureTimeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_LAST_HIGH_PRESSURE_TD))
                tyreDetectionEvent.lastHighTemperatureTimeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_LAST_HIGH_TEMPRATURE_TD))
                tyreDetectionEvent.dataType =
                    cursor.getInt(cursor.getColumnIndex(COL_DATA_TYPE_TD))
                tyreDetectionEvents.add(tyreDetectionEvent)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tyreDetectionEvents
    }

    fun tyreDetectionEventsByVinNumber(vinNumber: Array<String>): List<TyreDetectionEvent> {
        val tyreDetectionEvents: ArrayList<TyreDetectionEvent> = ArrayList<TyreDetectionEvent>()
        val query = "SELECT * FROM $TABLE_TYRE_DETECTION  where $COL_VIN_NUMBER IN (${makePlaceholders(vinNumber.size)})"
        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(query,vinNumber)
        } catch (e: SQLiteException) {
            db.execSQL(query)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                var tyreDetectionEvent = TyreDetectionEvent()
                tyreDetectionEvent.vinNumber =
                    cursor.getString(cursor.getColumnIndex(COL_VIN_NUMBER))
                tyreDetectionEvent.macAddress =
                    cursor.getString(cursor.getColumnIndex(COL_MAC_ADDRESS))
                tyreDetectionEvent.tyrePosition =
                    cursor.getString(cursor.getColumnIndex(COL_TYRE_POSITION))
                tyreDetectionEvent.sensorData =
                    cursor.getString(cursor.getColumnIndex(COL_SENSOR_DATA_TD))
                tyreDetectionEvent.timeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_DATE_TIMESTAMP))
                tyreDetectionEvent.lastLowPressureTimeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_LAST_LOW_PRESSURE_TD))
                tyreDetectionEvent.lastHighPressureTimeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_LAST_HIGH_PRESSURE_TD))
                tyreDetectionEvent.lastHighTemperatureTimeStamp =
                    cursor.getString(cursor.getColumnIndex(COL_LAST_HIGH_TEMPRATURE_TD))
                tyreDetectionEvent.dataType =
                    cursor.getInt(cursor.getColumnIndex(COL_DATA_TYPE_TD))
                tyreDetectionEvents.add(tyreDetectionEvent)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tyreDetectionEvents
    }

    fun makePlaceholders(len: Int): String {
        return if (len < 1) {
            throw RuntimeException("No placeholders")
        } else {
            val sb = StringBuilder(len * 2 - 1)
            sb.append("?")
            for (i in 1 until len) {
                sb.append(",?")
            }
            sb.toString()
        }
    }
    companion object {
        var DATABASENAME = "com.treel.androidsdk"
        const val TABLE_VEHICLE_CONFIGURATION = "vehicle_configuration"
        var TABLE_TYRE_DETECTION = "tyre_detection_event"

        var COL_ID = "id"
        var COL_VIN_NUMBER = "vin_number"
        var COL_MAC_ADDRESS = "mac_address"
        var COL_TYRE_POSITION = "tyre_position"
        var COL_REC_PRESSURE = "recommended_pressure"
        var COL_LOW_PRESSURE = "low_pressure"
        var COL_HIGH_PRESSURE = "high_pressure"
        var COL_HIGH_TEMPRATURE = "high_temperature"
        var COL_DATE_TIMESTAMP = "timeStamp"

        //  Tyre Detection Parameter

       /* var COL_ID_TD = "tyre_id"
        var COL_VIN_NUMBER_TD = "vin_number"
        var COL_MAC_ADDRESS_TD = "mac_address"*/
        var COL_SENSOR_DATA_TD = "sensor_data"
        //var COL_TIMESTAMP_TD = "timeStamp"
        var COL_LAST_LOW_PRESSURE_TD = "last_low_pressure_time_stamp"
        var COL_LAST_HIGH_PRESSURE_TD = "last_high_pressure_time_stamp"
        var COL_LAST_HIGH_TEMPRATURE_TD = "last_high_temprature_stamp"
        var COL_DATA_TYPE_TD = "data_type"

        // Alert Show Parameter
        var TABLE_ALERTS = "alerts"
       /* var COL_ID_AT = "id"
        var COL_MAC_ADDRESS_AT = "mac_address"
        var COL_VIN_NUMBER_AT = "vin_number"
        var COL_TYRE_POSITION_AT = "tyre_position"*/
        var COL_ALERT_TYPE_AT = "alert_type"
        var COL_TIMAESTAMP_AT = "timeStamp"
        var COL_ISVIEWED_AT = "is_viewed"
        var COL_ALERT_MESSAGE_AT = "msg"
        var COL_UPDATE_TIMESTAMP_AT = "updateTimeStamp"

        // Alert Show Parameter
        var TABLE_TPMS_DATA_HISTORY = "tpms_data_history"

        var COL_PRESSURE = "pressure"
        var COL_TEMPERATURE = "temperature"
        var COL_BATTERY = "battery"
    }
}