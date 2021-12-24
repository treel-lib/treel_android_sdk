package com.treel.androidsdk.ble

import android.os.ParcelUuid
import com.polidea.rxandroidble2.scan.ScanRecord

/**
 * @author Nitin Karande
 */
object BluetoothUUID {
    val TREEL_TAG_SERVICE_UUID = ParcelUuid.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    val TREEL_IBEACON_SERVICE_UUID = ParcelUuid.fromString("ffffffff-ffff-ffff-ffff-ffffffffffe0")

    val HUD_READ_FIRMWARE_SERVICE_UUID = ParcelUuid.fromString("0000bba1-0000-1000-8000-00805f9b34fb")
    val HUD_VEHICLE_REGISTRATION_SERVICE_UUID = ParcelUuid.fromString("0000bb00-0000-1000-8000-00805f9b34fb")
    val HUD_VEHICLE_AXEL_CONFIG_SERVICE_UUID = ParcelUuid.fromString("0000bb01-0000-1000-8000-00805f9b34fb")
    val HUD_VEHICLE_RTC_SERVICE_UUID = ParcelUuid.fromString("0000bb02-0000-1000-8000-00805f9b34fb")
    val HUD_CUSTOMER_NAME_UUID = ParcelUuid.fromString("0000bb03-0000-1000-8000-00805f9b34fb")
    val HUD_VEHICLE_TYPE_UUID = ParcelUuid.fromString("0000bb04-0000-1000-8000-00805f9b34fb")
    val HUD_TIRE_CONFIGIRATION_SERVICE_UUID = ParcelUuid.fromString("0000bb10-0000-1000-8000-00805f9b34fb")
    val HUD_CONFIGURATION_DONE_COMMAND = ParcelUuid.fromString("0000bb33-0000-1000-8000-00805f9b34fb")

    val HUD_ALERT_CONFIGIRATION_SERVICE_UUID = ParcelUuid.fromString("0000bb40-0000-1000-8000-00805f9b34fb")
    val HUD_NOGO_ALERT_CONFIGIRATION_SERVICE_UUID = ParcelUuid.fromString("0000bb41-0000-1000-8000-00805f9b34fb")


    val HUD_WRITE_SECURE_KEY_SERVICE_UUID = ParcelUuid.fromString("0000bb32-0000-1000-8000-00805f9b34fb")

    val HUD_UNIT_CONFIG_SERVICE_UUID = ParcelUuid.fromString("0000bb20-0000-1000-8000-00805f9b34fb")
    val HUD_TAG_DATA_SERVICE_UUID = ParcelUuid.fromString("0000bb55-0000-1000-8000-00805f9b34fb")
    val DEBUG_DATA_SERVICE_UUID = ParcelUuid.fromString("0000bb54-0000-1000-8000-00805f9b34fb")
    val HUD_SWAP_TIRES_CONFIG_SERVICE_UUID = ParcelUuid.fromString("0000bb22-0000-1000-8000-00805f9b34fb")
    /*val HUD_READ_SERVICE_UUID = ParcelUuid.fromString("0000bba0-0000-1000-8000-00805f9b34fb")
    val HUD_SWAP_TIRES_CONFIG_SERVICE_UUID = ParcelUuid.fromString("0000bb22-0000-1000-8000-00805f9b34fb")
    val HUD_BMI_CONFIG_SERVICE_UUID = ParcelUuid.fromString("0000bb21-0000-1000-8000-00805f9b34fb")

    val FUEL_SENSORS_DATA_SERVICE_UUID = ParcelUuid.fromString("0000bb51-0000-1000-8000-00805f9b34fb")
    val WIRELESS_RECEIVER_DEBUG_DATA_SERVICE_UUID = ParcelUuid.fromString("0000bb53-0000-1000-8000-00805f9b34fb")
    val HUD_TAG_DATA_SERVICE_UUID = ParcelUuid.fromString("0000bb55-0000-1000-8000-00805f9b34fb")
    val HUD_WIRELESS_RECEIVER_MACID_SERVICE_UUID = ParcelUuid.fromString("0000bb52-0000-1000-8000-00805f9b34fb")
    val HUD_FUEL_SENSOR_MACID_SERVICE_UUID = ParcelUuid.fromString("0000bb50-0000-1000-8000-00805f9b34fb")
    val HUD_BUFFERING_INTERVAL_SERVICE_UUID = ParcelUuid.fromString("0000bb31-0000-1000-8000-00805f9b34fb")
    val HUD_UUID_AND_KEY_CONFIG_SERVICE_UUID = ParcelUuid.fromString("0000bb30-0000-1000-8000-00805f9b34fb")*/
    fun isTreelBleDevice(scanRecord: ScanRecord): Boolean {
        return scanRecord.serviceUuids?.get(0)?.uuid == TREEL_TAG_SERVICE_UUID.uuid ?: false
    }

}