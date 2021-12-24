package com.treel.androidsdk.utility

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.polidea.rxandroidble2.BuildConfig
import com.treel.androidsdk.R
import com.treel.androidsdk.utility.Constants.LOG_WRITER_DIR
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.math.roundToInt


/**
 * Utility class to perform some uM3/19/2017.
 */
@SuppressLint("SimpleDateFormat")
object Utility {


    val isBluetoothSupported: Boolean
        get() {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            return adapter != null
        }

    val isBluetoothEnabled: Boolean
        get() {
            val adapter = BluetoothAdapter.getDefaultAdapter()
            return adapter != null && adapter.isEnabled
        }

    val isMarshMallow: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /*fun getScreenHeight(c: Context): Int {
        val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.y
    }*/

    /*fun getScreenWidth(c: Context): Int {
        val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }*/

    /*fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }*/

    fun enableBluetooth() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        adapter.enable()
    }

    fun isLocationServiceEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //return (locationManager.isProviderEnabled(GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        return (locationManager.isProviderEnabled(GPS_PROVIDER) && locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        ))
    }

    fun getCurrentDateAndTime(): String {
        val format = "dd/MM/yyyy HH:mm:ss"
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(calendar.time)
    }

    fun getCurrentDateAndTimeAdding1Sec(): String {
        val format = "dd/MM/yyyy HH:mm:ss"
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, 1)
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(calendar.time)
    }

    fun getCurrentDateTime(): String {
        val format = "yyyy-MM-dd HH:mm:ss"
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(calendar.time)
    }

    fun convertUTCtoLocalDateTime(dateTime: String): String? {
        var converted: String? = null
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = utcFormat.parse(dateTime)!!
            val currentTFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())
            converted = currentTFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    fun convertDateTimeForWidget(dateTime: String): String? {
        var converted: String? = null
        try {
            val utcFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = utcFormat.parse(dateTime)!!
            val currentTFormat = SimpleDateFormat("dd MMM HH:mm")
            converted = currentTFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    /*fun convertLocalToUTCDate(dateTime: String): String {
        var converted = ""
        try {
            val utcFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = utcFormat.parse(dateTime)!!
            val outputFmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            //  outputFmt.timeZone = TimeZone.getTimeZone("UTC")
            converted = outputFmt.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }*/

    fun getLocalToUTCDate(dateTime: String): String? {
        val currentTFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val date = currentTFormat.parse(dateTime)!!
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.timeZone = TimeZone.getTimeZone("UTC")
        val time = calendar.time
        @SuppressLint("SimpleDateFormat") val outputFmt =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        outputFmt.timeZone = TimeZone.getTimeZone("UTC")
        return outputFmt.format(time)
    }

    fun convertUTCtoLocalDateTimeCloud(dateTime: String): String? {
        var converted: String? = null
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = utcFormat.parse(dateTime)!!
            val currentTFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())
            converted = currentTFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeFromDateTimeFormat(dateTime: String): String? {
        var converted: String? = null
        try {
            val utcFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = utcFormat.parse(dateTime)

            val currentTFormat = SimpleDateFormat("HH:mm")
            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    fun isDetectedWithinLast2minutes(lastDetectionTimestamp: String): Boolean {
        val format = "dd/MM/yyyy HH:mm:ss"
        val myFormat = SimpleDateFormat(format)
        var minutesDifference: Long = (10 * 60)
        try {
            val date1 = myFormat.parse(lastDetectionTimestamp)
            val date2 = myFormat.parse(getCurrentDateAndTime())
            val diff = date2!!.time - date1!!.time
            minutesDifference = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return minutesDifference <= (10 * 60)
    }


    fun isDetectedWithin10Lastminutes(lastDetectionTimestamp: String): Boolean {
        val format = "dd/MM/yyyy HH:mm:ss"
        val myFormat = SimpleDateFormat(format)
        var minutesDifference: Long = (10 * 60)
        try {
            val date1 = myFormat.parse(lastDetectionTimestamp)
            val date2 = myFormat.parse(getCurrentDateAndTime())
            val diff = date2.time - date1.time
            minutesDifference = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return minutesDifference <= (10 * 60)
    }

    /*fun isDetectedWithinLast1minutes(lastDetectionTimestamp: String): Boolean {
        val format = "dd/MM/yyyy HH:mm:ss"
        val myFormat = SimpleDateFormat(format)
        var minutesDifference: Long = (10 * 60)
        try {
            val date1 = myFormat.parse(lastDetectionTimestamp)
            val date2 = myFormat.parse(getCurrentDateAndTime())
            val diff = date2!!.time - date1!!.time
            minutesDifference = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return minutesDifference <= (50)
    }*/

    fun isDetectedBefore10minutes(lastDetectionTimestamp: String): Boolean {

        val format = "dd/MM/yyyy HH:mm:ss"
        val myFormat = SimpleDateFormat(format)
        var minutesDifference: Long = (10 * 60)
        try {
            val date1 = myFormat.parse(lastDetectionTimestamp)
            val date2 = myFormat.parse(getCurrentDateAndTime())
            val diff = date2!!.time - date1!!.time
            minutesDifference = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return minutesDifference > (10 * 60)
    }


    fun getCurrentDate(): String {
        val format = "dd/MM/yyyy"
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(calendar.time)
    }

    fun getCurrentDateByMonthName(addDaysCount: Int? = null): String {
        val format = "dd MMM yyyy"
        val calendar = Calendar.getInstance()
        addDaysCount?.let {
            calendar.add(Calendar.DATE, 1)
        }
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(calendar.time)
    }

    private fun getCurrentDateForLogFile(): String {
        val format = "dd-MM-yy"
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(calendar.time)
    }

    fun convertUTCToLocalOdometer(utcDate: String): String {
        var converted = ""
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd")
            // utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            // val newUTCDate = utcDate.replace("Z", "+00:00")
            val date = utcFormat.parse(utcDate)

            val currentTFormat = SimpleDateFormat("dd/MM/yyyy")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())

            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    fun convertUTCToLocal(utcDate: String): String {
        var converted = ""
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd")
            // utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            // val newUTCDate = utcDate.replace("Z", "+00:00")
            val date = utcFormat.parse(utcDate)

            val currentTFormat = SimpleDateFormat("dd MMM yyyy")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())

            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    fun convertISODateTimeToLocalDate(utcDate: String): String? {
        var converted: String? = null
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            // utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            // val newUTCDate = utcDate.replace("Z", "+00:00")
            val date = utcFormat.parse(utcDate)

            val currentTFormat = SimpleDateFormat("dd MMM yyyy")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())

            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    fun getTimeInBritishFormat(dateTime: String): String? {
        var converted: String? = null
        try {
            val utcFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date = utcFormat.parse(dateTime)

            val currentTFormat = SimpleDateFormat("hh:mm a dd MMM yyyy")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())

            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    fun convertISOToLocalDateFormat(dateTime: String?): String? {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val output = SimpleDateFormat("yyyy-MM-dd")
        var d: Date? = null
        try {
            d = input.parse(dateTime!!)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return output.format(d!!)
    }

    /*fun convertUTCtoLocal1(isoDate: String): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC");

        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val output = SimpleDateFormat("dd MMM  HH:mm")
        var d: Date? = null
        try {
            d = input.parse(isoDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return output.format(d!!)
    }*/

    fun convertUTCtoLocal(dateTime: String): String? {
        var converted: String? = null
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = utcFormat.parse(dateTime)
            val currentTFormat = SimpleDateFormat("dd MMM hh:mm a")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())
            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    fun addDay(oldDate: String, numberOfDays: Int): String? {
        var dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val calendar = Calendar.getInstance()
        try {
            calendar.time = dateFormat.parse(oldDate)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        calendar.add(Calendar.DAY_OF_YEAR, numberOfDays)
        dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val newDate = Date(calendar.timeInMillis)
        return dateFormat.format(newDate)
    }

    fun convertUTCtoLocalCustom(dateTime: String): String? {
        var converted: String? = null
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = utcFormat.parse(dateTime)
            val currentTFormat = SimpleDateFormat("dd MMM, '@' hh:mm a")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())
            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    fun convertUTCtoLocalDate(dateTime: String): String? {
        var converted: String? = null
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = utcFormat.parse(dateTime)
            val currentTFormat = SimpleDateFormat("dd MMM yyyy")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())
            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }


    fun convertUTCtoMilliSeconds(dateTime: String): Long? {
        var dateInMilliSec: Long? = null
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = utcFormat.parse(dateTime)
            val currentTFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())
            val converted = currentTFormat.format(date!!)
            val mDate = currentTFormat.parse(converted)
            dateInMilliSec = TimeUnit.MILLISECONDS.toMinutes(mDate!!.time)
            //dateInMilliSec = mDate.time
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dateInMilliSec
    }

    //get the current time zone

    private fun getCurrentTimeZone(): String {
        val tz = Calendar.getInstance().timeZone
        return tz.id
    }

    fun getDaysBetweenTwoDates(lastUpdateDate: String, currentDate: String): Long {
        val format = "dd/MM/yyyy"
        val myFormat = SimpleDateFormat(format)
        try {
            val date1 = myFormat.parse(lastUpdateDate)
            val date2 = myFormat.parse(currentDate)
            val diff = date2!!.time - date1!!.time
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0

    }

    /*fun getDaysBetweenTwoDatesAndTime(lastUpdateDate: String, currentDate: String): Long {
        val format = "dd/MM/yyyy HH:mm:ss"
        val myFormat = SimpleDateFormat(format)
        try {
            val date1 = myFormat.parse(lastUpdateDate)
            val date2 = myFormat.parse(currentDate)
            val diff = date2!!.time - date1!!.time
            return TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0

    }*/

    fun getDateDiffernceBetweenTwoDates(lastUpdateDate: String, currentDate: String): Boolean {
        val format = "dd/MM/yyyy HH:mm:ss"
        val myFormat = SimpleDateFormat(format)
        try {
            val strDate = myFormat.parse(lastUpdateDate)
            val endDate = myFormat.parse(currentDate)
            return endDate!!.after(strDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return false
    }

    /**
     * Return the unique device id
     */
    @SuppressLint("HardwareIds")
    fun getDeviceID(context: Context): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }


    /**
     * Return the  Device Model
     */
    fun getDeviceModel(): String {
        return Build.MODEL
    }

    fun getParseDateHistory(date: String?): String {
        var converted = ""
        try {
            var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val newDate = simpleDateFormat.parse(date!!)
            simpleDateFormat = SimpleDateFormat("dd MMM yyyy")
            converted = simpleDateFormat.format(newDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return converted
    }

    fun getParseDateHistoryFromDateTime(utcDate: String?): String? {
        var converted: String? = null
        try {
            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            // utcFormat.timeZone = TimeZone.getTimeZone("UTC")
            // val newUTCDate = utcDate.replace("Z", "+00:00")
            val date = utcFormat.parse(utcDate!!)

            val currentTFormat = SimpleDateFormat("dd MMM yyyy")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())

            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    fun convertLocalToISO(utcDate: String): String {
        var converted = ""
        try {
            val utcFormat = SimpleDateFormat("dd MMM yyyy")
            val date = utcFormat.parse(utcDate)
            val currentTFormat = SimpleDateFormat("yyyy-MM-dd")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())

            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    fun convertLocalToISODateTime(utcDate: String): String {
        var converted = ""
        try {
            val utcFormat = SimpleDateFormat("dd MMM yyyy")
            val date = utcFormat.parse(utcDate)
            val currentTFormat = SimpleDateFormat("yyyy-MM-dd")
            currentTFormat.timeZone = TimeZone.getTimeZone(getCurrentTimeZone())

            converted = currentTFormat.format(date!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "$converted ${getCurrentTime()}"
    }

    private fun getCurrentTime(): String {
        val localDateFormat = SimpleDateFormat("HH:mm:ss")
        return localDateFormat.format(Date())
    }

    fun getParseDate(date: String): String {
        var converted = ""
        try {
            var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val newDate = simpleDateFormat.parse(date)
            simpleDateFormat = SimpleDateFormat("dd MMM yyyy")
            converted = simpleDateFormat.format(newDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }



    fun getReminderAlarmDate(date: String): String {
        var converted = ""
        try {
            var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val newDate = simpleDateFormat.parse(date)
            simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            converted = simpleDateFormat.format(newDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return converted
    }

    /*fun compareDates(currentDate: String, selectedDate: String): Boolean {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val date1 = sdf.parse(currentDate)
            val date2 = sdf.parse(selectedDate)
            return date1!!.before(date2)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }*/

    fun getReminderPreviousDate(daysToMinus: Int, reminderDate: String): Calendar? {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        //val sdfDateTime = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        val calendar = Calendar.getInstance()
        try {
            calendar.time = sdf.parse(reminderDate)!!
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        calendar.add(Calendar.DATE, -daysToMinus)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR, 8)
        calendar.set(Calendar.AM_PM, Calendar.AM)
        val newDate = sdf.format(calendar.time)
        val daysDifference = getDaysBetweenTwoDates(getCurrentDate(), newDate)
        //Return null if the new date is already elapsed. In that case no need to set reminder in the Alarm
        if (calendar.timeInMillis < Calendar.getInstance().timeInMillis) {
            return null
        }
        return if (daysDifference >= 0) calendar else null
    }

    fun getHoursBetweenTwoDatesAndTime(lastUpdateDate: String, currentDate: String): String {
        val format = "yyyy-MM-dd'T'HH:mm:ss.SSS"
        val myFormat = SimpleDateFormat(format)
        try {
            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60


            val date1 = myFormat.parse(lastUpdateDate)
            val date2 = myFormat.parse(currentDate)
            var diff = date2!!.time - date1!!.time

            val elapsedHours = diff / hoursInMilli
            diff %= hoursInMilli

            val elapsedMinutes = diff / minutesInMilli
            // diff = diff % minutesInMilli
            // val elapsedSeconds = diff / secondsInMilli
            var minutes = elapsedMinutes.toString()
            if (elapsedMinutes < 10) {
                minutes = "0${elapsedMinutes}"
            }
            //    val elapsedHours = different / hoursInMilli
            return "$elapsedHours:$minutes Hrs"
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return "0:0 Hrs"

    }


    fun getDateDifferenceValue(lastDetectionTimestamp: String): String {
        val format = "dd/MM/yyyy HH:mm:ss"
        val myFormat = SimpleDateFormat(format)
        var minutesDifference = 1
        var minutesDifference1 = 1
        try {
            val date1 = myFormat.parse(lastDetectionTimestamp)
            val date2 = myFormat.parse(getCurrentDateAndTime())
            val diff = date2!!.time - date1!!.time
            minutesDifference = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS).toInt()
            minutesDifference1 = TimeUnit.SECONDS.convert(diff, TimeUnit.MILLISECONDS).toInt()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        val data = when {
            minutesDifference1 < 60 -> minutesDifference1
            minutesDifference1 in 60..3599 -> minutesDifference1.toDouble() / 60
            minutesDifference1 in 3600..86399 -> minutesDifference1.toDouble() / 3600
            minutesDifference1 in 86400..2591999 -> minutesDifference1.toDouble() / 86400
            else -> minutesDifference1.toDouble() / 2592000
        }
        val spittedData = data.toDouble().toString().split(".")
        val timeStamp = if (spittedData.isNotEmpty()) {
            spittedData[0]
        } else {
            data.toDouble().roundToInt().toString()
        }
        return when {
            minutesDifference < 60 -> "$timeStamp sec ago"
            minutesDifference in 60..3599 -> "$timeStamp min ago"
            minutesDifference in 3600..86399 -> "$timeStamp hour ago"
            minutesDifference in 86400..2591999 -> "$timeStamp day ago"
            else -> "$timeStamp month ago"
        }

    }

    fun stringToHex(ascii: String): String { // Step-1 - Convert ASCII string to char array
        val ch = ascii.toCharArray()
        // Step-2 Iterate over char array and cast each element to Integer.
        val builder = StringBuilder()
        for (c in ch) {
            val i = c.toInt()
            // Step-3 Convert integer value to hex using toHexString() method.
            builder.append(Integer.toHexString(i).toUpperCase(Locale.ROOT))
        }
        return builder.toString()
    }

    fun hexToAscii(hexStr: String): String {
        val output = java.lang.StringBuilder("")
        var i = 0
        while (i < hexStr.length) {
            val str = hexStr.substring(i, i + 2)
            output.append(str.toInt(16).toChar())
            i += 2
        }
        return output.toString()
    }

    // Add White Zero In vehicle
    fun addBeforeZero(i: Int, str: String): String {
        val str1 = StringBuilder()
        for (j in 0 until i) {
            str1.append("0")
        }
        return str1.toString() + str
    }

    fun logToFile(content: String) {
        if (!BuildConfig.DEBUG) return
        //Timber.d("File Logging")
        writeContent(content)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun writeContent(content: String): Completable {
        return Completable.fromAction {
            try {
                /**
                 * Create a new log directory if not exist
                 */
                val dir = File(LOG_WRITER_DIR)
                if (!dir.exists()) dir.mkdirs()
                /**
                 * Create a new log file if not exist or return the old file
                 */
                val logFile = File(LOG_WRITER_DIR, "${getCurrentDateForLogFile()}.txt")
                if (!logFile.exists())
                    logFile.createNewFile()

                logFile.appendText("${getCurrentDateAndTime()} $content \n")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getPressureValue(pressure: Int, pressureUnit: String): String {
        when (pressureUnit) {
            Constants.PSI -> return pressure.toString()
            Constants.BAR -> return getPSItoBar(pressure)
            Constants.KPA -> return getPSItoKPA(pressure)
        }
        return ""
    }

    fun getTemperatureValue(temperature: Int, temperatureUnit: String): String {
        when (temperatureUnit) {
            Constants.CENTIGRADE -> return temperature.toString()
            Constants.FAHRENHEIT -> return conversionCentigrateToFehrenteit(temperature)
        }
        return ""
    }

    fun getPSIPressureValue(pressure: Double, pressureUnit: String): String {
        when (pressureUnit) {
            Constants.PSI -> return pressure.toString()
            Constants.BAR -> return conversionBarToPsi(pressure)
            Constants.KPA -> return conversionKpaToPsi(pressure)
        }
        return ""
    }

    fun getCentigrateTemperatureValue(temperature: Int, temperatureUnit: String): String {
        when (temperatureUnit) {
            Constants.CENTIGRADE -> return temperature.toString()
            Constants.FAHRENHEIT -> return conversionFehrenteitToCentigrate(temperature)
        }
        return ""
    }

    fun getTemperatureUnit(temperatureUnit: String): String {
        when (temperatureUnit) {
            Constants.CENTIGRADE -> return Constants.DEGREE_CENTIGRADE
            Constants.FAHRENHEIT -> return Constants.DEGREE_Fahrenheit
        }
        return Constants.DEGREE_CENTIGRADE
    }

    private fun getPSItoKPA(pressure: Int): String {
        return (pressure * "6.89476".toFloat()).roundToInt().toString()
    }

    private fun getPSItoBar(pressure: Int): String {
        if (pressure == 0) {
            return pressure.toString()
        }
        return String.format("%.2f", (pressure / "14.504".toDouble()))
    }

    private fun conversionBarToPsi(pressure: Double): String {
        return (pressure * "14.504".toDouble()).roundToInt().toString()
    }

    private fun conversionKpaToPsi(pressure: Double): String {
        return (pressure / "6.89476".toFloat()).roundToInt().toString()
    }

    private fun conversionCentigrateToFehrenteit(temperature: Int): String {
        val temp = (temperature * 1.8) + 32
        return temp.roundToInt().toString()
    }

    private fun conversionFehrenteitToCentigrate(temperature: Int): String {
        val temp = (temperature - 32) * 0.555
        return temp.roundToInt().toString()
    }

    //TOdo on Get Unit Lael Unit
    fun getUnitLabel(selectedUnit: String): String {
        return when (selectedUnit) {
            Constants.PSI -> {
                Constants.DISPLAY_PSI
            }
            Constants.BAR -> {
                Constants.DISPLAY_BAR
            }
            Constants.KPA -> {
                Constants.DISPLAY_KPA
            }
            else -> Constants.DISPLAY_PSI
        }

    }

     @RequiresApi(Build.VERSION_CODES.O)
     fun createNotificationChannelForOreoAndAbove(
         context: Context,
         channelName: String,
         channelID: String
     ) {
         try {
             val notificationManager =
                 context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
             val mChannel = notificationManager.getNotificationChannel(channelID)
             if (mChannel == null) {
                 createChannel(channelID, context, channelName)
             } else {
                 notificationManager.deleteNotificationChannel(channelID)
                 createChannel(channelID, context, channelName)
             }
         } catch (e: Exception) {
             e.printStackTrace()
         }
     }


     @RequiresApi(Build.VERSION_CODES.O)
     private fun createChannel(channelID: String, context: Context, channelName: String) {
         val notificationManager =
             context.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
         val mChannel: NotificationChannel?
         val descriptionText = context.getString(R.string.app_name)
         val importance = NotificationManager.IMPORTANCE_HIGH
         mChannel = NotificationChannel(channelID, channelName, importance)
         mChannel.description = descriptionText
         mChannel.enableLights(false)
         mChannel.setShowBadge(false)
         mChannel.vibrationPattern = longArrayOf(0, 0, 0, 0, 0)

         notificationManager.createNotificationChannel(mChannel)
     }



    fun addSpace(i: Int, str: String): String {
        val str1 = StringBuilder()
        for (j in 0 until i) {
            str1.append(" ")
        }
        return str + str1.toString()
    }

    fun addAfterZero(i: Int, str: String): String {
        val str1 = StringBuilder()
        for (j in 0 until i) {
            str1.append("0")
        }
        return str + str1.toString()
    }

    fun validateHexString(hex: String): Boolean {
        val mPattern = Pattern.compile("[0-9A-F:]+")
        val matcher = mPattern.matcher(hex)
        return matcher.matches()
    }

    fun getIntToHex(value: Int): String {
        /*   val hex = Integer.toHexString(value)
          return parseLong(hex, 16).toInt()*/
        val hex = Integer.toHexString(value)
        return hex

    }

    fun getHexToInt(hex: String): Int {
        return java.lang.Long.parseLong(hex, 16).toInt()
    }

    fun hexToDec(hex: String): Int {
        return Integer.parseInt(hex, 16)
    }



    fun isInternetAvailable(context: Context): Boolean {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return this.getNetworkCapabilities(this.activeNetwork)?.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) ?: false
            } else {
                (@Suppress("DEPRECATION")
                return this.activeNetworkInfo?.isConnected ?: false)
            }
        }
    }
}

