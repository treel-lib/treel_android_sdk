package com.treel.androidsdk

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.treel.androidsdk.ble.BluetoothUUID.TREEL_IBEACON_SERVICE_UUID
import com.treel.androidsdk.ble.ConversionUtils
import com.treel.androidsdk.ble.Reading
import com.treel.androidsdk.ble.SensorDetectionSession
import com.treel.androidsdk.database.ConfigDataBaseHandler
import com.treel.androidsdk.database.VehicleConfiguration
import com.treel.androidsdk.event.EventCallbackListener
import com.treel.androidsdk.utility.Constants
import com.treel.androidsdk.utility.Utility
import org.altbeacon.beacon.*
import org.altbeacon.beacon.startup.BootstrapNotifier
import org.altbeacon.beacon.startup.RegionBootstrap
import org.altbeacon.bluetooth.BluetoothMedic


class TreelTagBeaconScan(context: Context): BootstrapNotifier,
    RangeNotifier {

    private var context: Context? = null
    private var database: ConfigDataBaseHandler? = null
    private var eventCallbackListener: EventCallbackListener? = null

    val rangingData = RangingData()
    val monitoringData = MonitoringData()
    var alreadyStartedRangingAtBoot = false
    var region: Region? = null
    var regionBootstrap: RegionBootstrap? = null
    var beaconManager: BeaconManager? = null
    val TAG = "BeaconReferenceTREEL"
    val CONNECTED_DEVICE_CHANNEL = "connected_device_channel"
    val FILE_SAVED_CHANNEL = "file_saved_channel"
    val PROXIMITY_WARNINGS_CHANNEL = "proximity_warnings_channel"
    init {
        this.context = context
        database = ConfigDataBaseHandler(this.context!!)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CONNECTED_DEVICE_CHANNEL,
                context.getString(R.string.channel_connected_devices_title),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = context.getString(R.string.channel_connected_devices_description)
            channel.setShowBadge(false)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val fileChannel = NotificationChannel(
                FILE_SAVED_CHANNEL,
                context.getString(R.string.channel_files_title),
                NotificationManager.IMPORTANCE_LOW
            )
            fileChannel.description = context.getString(R.string.channel_files_description)
            fileChannel.setShowBadge(false)
            fileChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val proximityChannel = NotificationChannel(
                PROXIMITY_WARNINGS_CHANNEL,
                context.getString(R.string.channel_proximity_warnings_title),
                NotificationManager.IMPORTANCE_LOW
            )
            proximityChannel.description =
                context.getString(R.string.channel_proximity_warnings_description)
            proximityChannel.setShowBadge(false)
            proximityChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.createNotificationChannel(fileChannel)
            notificationManager.createNotificationChannel(proximityChannel)
        }

        beaconManager = BeaconManager.getInstanceForApplication(this.context!!)
        //BeaconManager.setDebug(true)
        beaconManager?.backgroundMode = true
     //   getVehicleConfigurations()
       // observeBleStateChanges()

        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID_APP_ACTIVE)
        builder.setSmallIcon(R.drawable.jk_treel_logo)
        builder.setContentTitle(context.getString(R.string.msg_monitor_tyres))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Utility.createNotificationChannelForOreoAndAbove(
                context,
                Constants.NOTIFICATION_CHANNEL_NAME_SMARTTYRE_MONITOR,
                Constants.NOTIFICATION_CHANNEL_ID_APP_ACTIVE
            )
        }

       // beaconManager?.enableForegroundServiceScanning(builder.build(), 10000);
        beaconManager?.setEnableScheduledScanJobs(false)
        beaconManager?.backgroundBetweenScanPeriod = 0
        beaconManager?.backgroundScanPeriod = 1100
        beaconManager?.beaconParsers?.clear()
        beaconManager?.beaconParsers?.add(
            BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")
        )
        BluetoothMedic.getInstance()
            .setNotificationsEnabled(
                true,
                org.altbeacon.beacon.R.drawable.notification_icon_background
            )

        BluetoothMedic.getInstance().enablePowerCycleOnFailures(context)
        BluetoothMedic.getInstance()
            .enablePeriodicTests(context, BluetoothMedic.SCAN_TEST + BluetoothMedic.TRANSMIT_TEST)

        region = Region("wildcard-region", null, null, null)
        regionBootstrap = RegionBootstrap(this, region)
    }

    override fun didEnterRegion(region: Region?) {
        // In this example, this class sends a notification to the user whenever a Beacon
        // matching a Region (defined above) are first seen.
        Log.d(TAG, "didEnterRegion")
        monitoringData.state.postValue(MonitorNotifier.INSIDE)

    }

    override fun didExitRegion(region: Region?) {
        monitoringData.state.postValue(MonitorNotifier.OUTSIDE)
    }

    override fun didDetermineStateForRegion(state: Int, region: Region?) {
        Log.d(TAG, "didDetermineStateForRegion")
        monitoringData.state.postValue(state)
        // This is a convenient place to start ranging if you want it on.
        if (region != null && !alreadyStartedRangingAtBoot) {
            alreadyStartedRangingAtBoot = true
            BeaconManager.getInstanceForApplication(context!!).startRangingBeaconsInRegion(region)
            BeaconManager.getInstanceForApplication(context!!).addRangeNotifier(this)
        }
    }

    override fun getApplicationContext(): Context? {
        return this.context
    }


    override fun didRangeBeaconsInRegion(beacons: MutableCollection<Beacon>?, region: Region?) {
        // This method is called periodically with a list of all visible beacons matching the region
        if (beacons != null) {
            Log.d(TAG, "Detected ${beacons.count()} beacons:")
            for (beacon: Beacon in beacons) {
               Log.d(TAG, "Detected ${beacon} Device Name ${beacon.bluetoothName}")
                if ((beacon.id1.toString()).equals(TREEL_IBEACON_SERVICE_UUID.toString())) {

                    val minor = beacon.id3.toInt()
                    val bytes = ConversionUtils.decimalToByteArray(minor)
                    val pressure = ConversionUtils.getDecimal(bytes[1])

                    val config = getTagConfiguration(beacon.bluetoothAddress)
                    Log.d(TAG, "Match MacId: ${beacon.bluetoothAddress}")
                    config?.let {
                        /*BeaconLog.d(
                            TAG,
                            "Match MacId: ${beacon.bluetoothAddress} ::: ${it.vehicleRegistrationNo} : ${it.tyrePosition}"
                        )*/
                        var previousTemperature = 0
                        var previousBattery = 0
                        it.tireReading?.let { it1 ->
                            previousTemperature = it1.currentTemperature
                            previousBattery = it1.battery
                        }
                        val reading = Reading(
                            previousBattery,
                            pressure,
                            previousTemperature,
                            Constants.BLE_DATA_TYPE,
                            Utility.getCurrentDateAndTime()
                        )
                        it.tireReading = reading
                        /* BeaconLog.d(
                             TAG, " MacId : ${beacon.bluetoothAddress} $reading"
                         )*/
                        context?.let { it1 -> TreelTagScan(it1).checkForAlerts(it) }
                    }
                }
            }
        }
        rangingData.beacons.postValue(beacons)
    }

    // This MutableLiveData mechanism is used for sharing centralized beacon data with the ViewControllers
    class RangingData : ViewModel() {
        val beacons: MutableLiveData<Collection<Beacon>> by lazy {
            MutableLiveData<Collection<Beacon>>()
        }
    }

    // This MutableLiveData mechanism is used for sharing centralized beacon data with the ViewControllers
    class MonitoringData : ViewModel() {
        val state: MutableLiveData<Int> by lazy {
            MutableLiveData<Int>()
        }
    }

    private fun getTagConfiguration(macAddress: String): VehicleConfiguration? {
        val newMacAddress = macAddress.replace(":", "")
        val config = SensorDetectionSession.getConfigurations()
        return config.firstOrNull { it.macAddress == newMacAddress }
    }
}