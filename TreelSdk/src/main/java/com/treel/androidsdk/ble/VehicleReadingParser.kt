package com.logicare.treel.ui.viewVehicle.ble


import com.treel.androidsdk.ble.ConversionUtils
import com.treel.androidsdk.ble.Reading
import com.treel.androidsdk.utility.Constants
import com.treel.androidsdk.utility.Constants.BLE_DATA_TYPE
import com.treel.androidsdk.utility.Constants.DECRYPTION_KEY
import com.treel.androidsdk.utility.Utility
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Singleton class responsible for parsing the sensor data
 *
 * @author Nitin Karande
 */
object VehicleReadingParser {

    fun parse(scanResult: ByteArray): Reading {
        // Timber.d(" Sensor Original Hex: ${HexString.bytesToHex(scanResult)}")
        /**
         * Calculating Temperature from scan record
         */
        /**
         * Extracting 2 bytes start from 6th position from the notification data
         * 1st byte is Raw temperature value LSB
         * 2nd byte is Raw temperature value MSB
         */
        val decryptedBytes = decrypt(extractBytes(scanResult, 15, 16), DECRYPTION_KEY)!!
        //Timber.d("  Sensor decryptedBytes Hex: ${HexString.bytesToHex(decryptedBytes)}")
        val tempBytes = extractBytes(decryptedBytes, 1, 2)
        var temperature: Int
        if (ConversionUtils.isInvalidTemperature(tempBytes)) {
            temperature = Constants.OVR_RANGE
        } else {
            temperature = ConversionUtils.calculateSurfaceTemperature(tempBytes)
            if (temperature < Constants.TEMPERATURE_MIN || temperature > Constants.TEMPERATURE_MAX) {
                temperature = Constants.OVR_RANGE
            }
        }
        /*if(temperature != Constants.OVR_RANGE) {
            temperature = Constants.TEXT_OVR
        } else {
            parsedData[2] = String.format("%s%s", temperature.toString() , ConversionUtils.UNIT_TEMPERATURE_CENTIGRADE)

        }*/

        /**
         * Calculating Pressure from scan record
         */
        /**
         * Extracting 3 bytes start from 10th position from the notification data
         * 1st byte is Raw Pressure value LSB
         * 2nd byte is Raw Pressure value next byte
         * 3rd byte is Raw Pressure value MSB
         */
        val pressureBytes = extractBytes(decryptedBytes, 3, 2)
        var pressure: Int
        if (ConversionUtils.isInvalidPressure(pressureBytes)) {
            pressure = Constants.OVR_RANGE
        } else {
            pressure = ConversionUtils.calculatePressure(pressureBytes)
            if (pressure < Constants.PRESSURE_MIN || pressure > Constants.PRESSURE_MAX) {
                pressure = Constants.OVR_RANGE
            }
        }
        /**
         * Getting Battery health in percentage from scan record
         */
        /**
         * Extracting 1 bytes start from 19th position from the scan record
         * 1st byte is Battery health status in percent
         */
        var batteryHealth = ConversionUtils.getBatteryHealth(decryptedBytes[5])
        batteryHealth = if (batteryHealth > 100) 100 else batteryHealth


        //val tyreTemperCount = ConversionUtils.getTyreTemperCount(decryptedBytes[7])

        return Reading(batteryHealth, pressure, temperature, BLE_DATA_TYPE, Utility.getCurrentDateAndTime())
    }

    // Helper method to extract bytes from byte array.
    private fun extractBytes(scanRecord: ByteArray, start: Int, length: Int): ByteArray {
        val bytes = ByteArray(length)
        System.arraycopy(scanRecord, start, bytes, 0, length)
        return bytes
    }

    private fun decrypt(encryptedBytes: ByteArray, encryptionKey: String): ByteArray? {
        try {
            val cipher = Cipher.getInstance("AES/ECB/NOPADDING")
            val secretKey = SecretKeySpec(encryptionKey.toByteArray(), "AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKey)
            return cipher.doFinal(encryptedBytes)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}