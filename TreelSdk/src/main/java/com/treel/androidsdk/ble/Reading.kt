package com.treel.androidsdk.ble

import android.os.Parcel
import android.os.Parcelable

/**
 * Class responsible to map the sensor data
 * @author Sanjay Sah
 */
data class Reading(val battery: Int,
                   val currentPressure: Int,
                   val currentTemperature: Int,
                   val dataType: Int,
                   val lastReportedTime: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(battery)
        parcel.writeInt(currentPressure)
        parcel.writeInt(currentTemperature)
        parcel.writeInt(dataType)
        parcel.writeString(lastReportedTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reading> {
        override fun createFromParcel(parcel: Parcel): Reading {
            return Reading(parcel)
        }

        override fun newArray(size: Int): Array<Reading?> {
            return arrayOfNulls(size)
        }
    }
}