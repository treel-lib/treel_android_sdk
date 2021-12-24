package com.treel.androidsdk

import android.content.Context
import android.util.Log
import com.logicare.treel.ui.viewVehicle.ble.VehicleReadingParser
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.exceptions.BleScanException
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.treel.androidsdk.ble.*
import com.treel.androidsdk.data.TyrePosition
import com.treel.androidsdk.data.VehicleType
import com.treel.androidsdk.database.*

import com.treel.androidsdk.event.EventCallbackListener
import com.treel.androidsdk.exception.BleScanException.SCAN_FAILED_CONFIGURATION_NOT_AVAILABLE
import com.treel.androidsdk.notification.AlertNotification
import com.treel.androidsdk.utility.Constants
import com.treel.androidsdk.utility.Constants.BLE_DATA_TYPE
import com.treel.androidsdk.utility.Constants.DEGREE_CENTIGRADE
import com.treel.androidsdk.utility.Constants.OVR_RANGE
import com.treel.androidsdk.utility.Constants.PSI
import com.treel.androidsdk.utility.Utility
import com.treel.androidsdk.utility.Utility.getCurrentDateAndTime
import io.reactivex.Completable

import io.reactivex.Observable

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TreelTagScan(context: Context) {
    /**
     * Observable to observe the Bluetooth adapter state of the device
     */
    private var bleStateSubscription: Disposable? = null
    private var rxBleClient: RxBleClient? = null
    private var context: Context? = null
    private var timerObservable: Disposable? = null
    private var disposeBag: CompositeDisposable = CompositeDisposable()
    private var scanSubscription: Disposable? = null
    private var database: ConfigDataBaseHandler? = null
    private var vehicleConfigurations: List<VehicleConfiguration>? = null
    private var eventCallbackListener: EventCallbackListener? = null

    init {
        this.context = context
        rxBleClient = RxBleClient.create(context)
        database = ConfigDataBaseHandler(context)
        vehicleConfigurations = ArrayList()
        getVehicleConfigurations()
        observeBleStateChanges()
    }


    fun getBleClient(context: Context): RxBleClient? {
        if (rxBleClient == null) {
            rxBleClient = RxBleClient.create(context)
        }
        return rxBleClient
    }

    fun startScan(context: Context) {
        getBleClient(context)
        if(database?.getVehicleConfigure()?.size ==0){
            eventCallbackListener?.handleBleException(SCAN_FAILED_CONFIGURATION_NOT_AVAILABLE)
            return
        }
        timerObservable?.dispose()
        timerObservable = Observable.interval(0, 10, TimeUnit.MINUTES)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                scanSubscription?.dispose()
            }
            .subscribe {
                scanSubscription?.dispose()

                scanSubscription = rxBleClient?.scanBleDevices(
                    ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .build()
                )
                    ?.subscribe(
                        { scanResult ->
                            handleScanResponse(scanResult)
                        },
                        { throwable ->
                            onScanFailure(throwable)
                        }
                    )

            }
    }

    private fun handleScanResponse(scanResult: ScanResult) {
        Log.d("Sensor Detected:", " ${scanResult.bleDevice.macAddress}")
        val beacon = ConversionUtils.getTreelBeacon(scanResult.scanRecord.bytes)
        when {
            beacon != null -> {
                beacon.macID = scanResult.bleDevice.macAddress
                /**
                 * One byte from the minor contains the pressure value
                 */
                /**
                 * One byte from the minor contains the pressure value
                 */
                val minor = beacon.minor
                val bytes = ConversionUtils.decimalToByteArray(minor)
                val pressure = ConversionUtils.getDecimal(bytes[1])

                getTagConfiguration(beacon.macID)?.let { it ->
                    Timber.d("Beacon Mac ID: ${beacon.macID} and minor $pressure")
                    var previousTemperature = 0
                    var previousBattery = 0
                    it.tireReading?.let {
                        previousTemperature = it.currentTemperature
                        previousBattery = it.battery
                    }
                    val reading = Reading(
                        previousBattery,
                        pressure,
                        previousTemperature,
                        BLE_DATA_TYPE,
                        getCurrentDateAndTime()
                    )
                    it.tireReading = reading
                    // eventCallbackListener?.onTpmsDataReceived(it)
                    checkForAlerts(it)
                    //view?.tyreReadingReceived(it)

                }
            }
            BluetoothUUID.isTreelBleDevice(scanResult.scanRecord) -> getTagConfiguration(scanResult.bleDevice.macAddress)?.let {
                Timber.d("BLE Mac ID: ${it.macAddress}")
                //it.restartTimeoutTimer()

                val reading = parseResponse(scanResult)
                it.tireReading = reading
                eventCallbackListener?.onTpmsDataReceived(it)
                checkForAlerts(it)
                // view?.tyreReadingReceived(it)
            }
            else -> return
        }
    }

    private fun getTagConfiguration(macAddress: String): VehicleConfiguration? {
        val newMacAddress = macAddress.replace(":", "")
        val config = SensorDetectionSession.getConfigurations()
        return config.firstOrNull { it.macAddress == newMacAddress }
    }

    private fun getVehicleConfigurations() {
        database = context?.let { ConfigDataBaseHandler(it) }
        val configurations = ArrayList(database?.getVehicleConfigure())
        SensorDetectionSession.addConfigurations(configurations, database)
    }

    private fun parseResponse(scanResult: ScanResult): Reading {
        return VehicleReadingParser.parse(scanResult.scanRecord.bytes)
    }

    /**
     * Checking if any alert related to sensor needs to be displayed
     */
    fun checkForAlerts(tagConfiguration: VehicleConfiguration) {
        /*val configuration = database?.getTyreConfiguration(
            tagConfiguration.vinNumber!!,
            tagConfiguration.tyrePosition!!
        )!!*/
        /*tagConfiguration.highPressure = configuration.highPressure
        tagConfiguration.lowPressure = configuration.lowPressure
        tagConfiguration.highTemperature = configuration.highTemperature*/

        val lowPressureSP: Int = tagConfiguration.lowPressure!!
        val highPressureSP: Int = tagConfiguration.highPressure!!
        val highTemperatureSP: Int = tagConfiguration.highTemperature!!
        val reading = tagConfiguration.tireReading
        var event = database?.getTyreDetectionEvent(tagConfiguration.macAddress!!)

        if (event == null) {
            event = TyreDetectionEvent()

        }

        event.macAddress = tagConfiguration.macAddress
        event.vinNumber = tagConfiguration.vinNumber
        event.tyrePosition = tagConfiguration.tyrePosition
        event.sensorData =
            "${reading!!.currentPressure},${reading.currentTemperature},${reading.battery}"
        event.timeStamp = getCurrentDateAndTime()
        event.dataType = BLE_DATA_TYPE
        // Get Selected User conversion
        val alertData: Alert? = when {

            reading.currentPressure < lowPressureSP -> {
                if (event.lastLowPressureTimeStamp == null || Utility.isDetectedBefore10minutes(
                        event.lastLowPressureTimeStamp!!
                    )
                ) {
                    getAlertData(
                        tagConfiguration,
                        LowPressureAlert(),
                        "${reading.currentPressure}  $PSI"
                    ).apply {
                        event.lastLowPressureTimeStamp = getCurrentDateAndTime()
                    }
                } else {
                    // event.lastLowPressureTimeStamp = getCurrentDateAndTime()
                    event.lastLowPressureTimeStamp = event.lastLowPressureTimeStamp
                    null
                }


            }
            reading.currentPressure > highPressureSP -> {
                if (event.lastHighPressureTimeStamp == null || Utility.isDetectedBefore10minutes(
                        event.lastHighPressureTimeStamp!!
                    )
                ) {
                    getAlertData(
                        tagConfiguration,
                        HighPressureAlert(),
                        "${reading.currentPressure} $PSI"
                    ).apply {
                        event.lastHighPressureTimeStamp = getCurrentDateAndTime()
                    }
                } else {
                    //event.lastHighPressureTimeStamp = getCurrentDateAndTime()
                    event.lastHighPressureTimeStamp = event.lastHighPressureTimeStamp
                    null
                }
            }
            else -> null
        }

        alertData?.let {
            handleAlert(alertData, tagConfiguration)
        }

        if (reading.currentTemperature != OVR_RANGE && reading.currentTemperature > highTemperatureSP) {
            if (event.lastHighTemperatureTimeStamp == null || Utility.isDetectedBefore10minutes(
                    event.lastHighTemperatureTimeStamp!!
                )
            ) {
                getAlertData(
                    tagConfiguration,
                    HighTemperatureAlert(),
                    "${reading.currentTemperature} $DEGREE_CENTIGRADE"
                ).apply {
                    handleAlert(this, tagConfiguration)
                }
                event.lastHighTemperatureTimeStamp = getCurrentDateAndTime()
            } else {
                event.lastHighTemperatureTimeStamp = event.lastHighTemperatureTimeStamp
            }
        }
        database?.saveTyreDetectionEvent(event)

        val tpmsDetectionData = TpmsDetectionData()
        tpmsDetectionData.vinNumber = event.vinNumber
        tpmsDetectionData.macAddress = event.macAddress
        tpmsDetectionData.tyrePosition = tagConfiguration.tyrePosition
        tpmsDetectionData.pressure = reading.currentPressure
        tpmsDetectionData.temperaure = reading.currentTemperature
        tpmsDetectionData.battery = reading.battery
        tpmsDetectionData.timeStamp = event.timeStamp
        database?.saveTpmsDataHistory(tpmsDetectionData)
    }

    private fun getAlertData(
        tagConfiguration: VehicleConfiguration,
        alertData: AlertData,
        alertValue: String
    ): Alert {
        val vehicleType = "BIKE"
        val alert = Alert()
        alert.vinNumber = tagConfiguration.vinNumber
        alert.macAddress = tagConfiguration.macAddress
        alert.tyrePosition = tagConfiguration.tyrePosition
        val tyrePositionAlias = when (tagConfiguration.tyrePosition) {
            TyrePosition.TYRE_1A.toString() -> if (vehicleType == VehicleType.BIKE.toString()) Constants.FRONT else Constants.FRONT_LEFT
            TyrePosition.TYRE_1B.toString() -> Constants.FRONT_RIGHT
            TyrePosition.TYRE_2A.toString() -> if (vehicleType == VehicleType.BIKE.toString()) Constants.REAR else Constants.REAR_LEFT
            TyrePosition.TYRE_2B.toString() -> Constants.REAR_RIGHT
            TyrePosition.TYRE_3A.toString() -> Constants.STEPNEY
            else -> {
                Constants.FRONT_LEFT
            }
        }
        alert.alertMsg = when (alertData) {
            is HighPressureAlert -> {
                alert.alertType = Constants.ALERT_HIGH_PRESSURE
                getStringAlertMsg(tyrePositionAlias, R.string.alert_high_pressure, alertValue)
            }
            is LowPressureAlert -> {
                alert.alertType = Constants.ALERT_LOW_PRESSURE
                getStringAlertMsg(tyrePositionAlias, R.string.alert_low_pressure, alertValue)
            }
            is HighTemperatureAlert -> {
                alert.alertType = Constants.ALERT_HIGH_TEMPERATURE
                getStringAlertMsg(tyrePositionAlias, R.string.alert_high_temperature, alertValue)
            }
            is TireRotationAlert -> {
                getStringAlertMsg(tyrePositionAlias, R.string.alert_high_pressure, alertValue)
            }
        }
        return alert
    }

    private fun getStringAlertMsg(
        tyrePositionAlias: String,
        alertMsg: Int,
        alertValue: String
    ): String? {
        return context?.getString(alertMsg, alertValue, tyrePositionAlias)
        //return view?.getStringAlertMsg(tyrePositionAlias, alertMsg, alertValue)
    }

    private fun handleAlert(receivedAlert: Alert, tagConfiguration: VehicleConfiguration) {
        if (receivedAlert.alertType == 3) {
            Timber.d("Saving new alert ${receivedAlert.macAddress} : ${receivedAlert.alertType} : ${Utility.getCurrentDateAndTimeAdding1Sec()}")
            receivedAlert.timeStamp = Utility.getCurrentDateAndTimeAdding1Sec()
        } else {
            Timber.d("Saving new alert ${receivedAlert.macAddress} : ${receivedAlert.alertType} : ${getCurrentDateAndTime()}")
        }

        database?.insertAlert(receivedAlert)
        //receivedAlert.alertMsg = receivedAlert.alertMsg

        val alertNotification = getAlertNotification(receivedAlert)
        eventCallbackListener?.showAlertNotification(alertNotification!!)

    }

    private fun getAlertNotification(alert: Alert): AlertNotification? {
        //Generating unique notification id which will be used to display notification for each tyre's different types of
        //notifications. Existing notification for the same tires same type of alert will be replaced with the new notification.
        return AlertNotification(alert)
    }

    private fun onScanFailure(throwable: Throwable?) {
        if (throwable is BleScanException) {
            eventCallbackListener?.handleBleException(throwable.reason)
        }
    }


    fun syncVehicleConfigurations(vehicleConfigurations: ArrayList<VehicleConfiguration>): String {
        for (vehicleConfiguration in vehicleConfigurations) {
            database = context?.let { ConfigDataBaseHandler(it) }
            database?.insertTag(vehicleConfiguration)
        }
        getVehicleConfigurations()
        return "Vehicle Configuration updated successfully"
    }

    fun fetchLatestTpmsData(vinNumber: String): List<TpmsDetectionData>? {
        val tpmsDetectionDatas: ArrayList<TpmsDetectionData> = ArrayList<TpmsDetectionData>()
        val tyreDetectionEvents = database?.tyreDetectionEventsByVinNumber(vinNumber)
        tyreDetectionEvents?.let {
            for (tyreDetectionEvent in it) {
                val tpmsDetectionData = TpmsDetectionData()
                //   var reading:Reading? = null
                var pressure = 0
                var temperature = 0
                var battery = 0
                tyreDetectionEvent.sensorData?.let { it1 ->
                    val sensorData = it1.split(",")
                    pressure = sensorData[0].toInt()
                    temperature = sensorData[1].toInt()
                    battery = sensorData[2].toInt()
                    //   reading = Reading(battery = sensorData.toInt(), currentPressure = it1[0].toInt(), currentTemperature = it1[1].toInt(), dataType = BLE_DATA_TYPE, lastReportedTime = tyreDetectionEvent.timeStamp)
                }
                tpmsDetectionData.vinNumber = tyreDetectionEvent.vinNumber
                tpmsDetectionData.macAddress = tyreDetectionEvent.macAddress
                tpmsDetectionData.tyrePosition = tyreDetectionEvent.tyrePosition
                tpmsDetectionData.pressure = pressure
                tpmsDetectionData.temperaure = temperature
                tpmsDetectionData.battery = battery
                tpmsDetectionData.timeStamp = tyreDetectionEvent.timeStamp
                tpmsDetectionDatas.add(tpmsDetectionData)
            }
        }
        return tpmsDetectionDatas.toList() ?: emptyList()
    }

    fun fetchLatestTpmsData(vinNumber: Array<String>): List<TpmsDetectionData>? {
        val tpmsDetectionDatas: ArrayList<TpmsDetectionData> = ArrayList<TpmsDetectionData>()
        val tyreDetectionEvents = database?.tyreDetectionEventsByVinNumber(vinNumber)
        tyreDetectionEvents?.let {
            for (tyreDetectionEvent in it) {
                val tpmsDetectionData = TpmsDetectionData()
                //   var reading:Reading? = null
                var pressure = 0
                var temperature = 0
                var battery = 0
                tyreDetectionEvent.sensorData?.let { it1 ->
                    val sensorData = it1.split(",")
                    pressure = sensorData[0].toInt()
                    temperature = sensorData[1].toInt()
                    battery = sensorData[2].toInt()
                    //   reading = Reading(battery = sensorData.toInt(), currentPressure = it1[0].toInt(), currentTemperature = it1[1].toInt(), dataType = BLE_DATA_TYPE, lastReportedTime = tyreDetectionEvent.timeStamp)
                }
                tpmsDetectionData.vinNumber = tyreDetectionEvent.vinNumber
                tpmsDetectionData.macAddress = tyreDetectionEvent.macAddress
                tpmsDetectionData.tyrePosition = tyreDetectionEvent.tyrePosition
                tpmsDetectionData.pressure = pressure
                tpmsDetectionData.temperaure = temperature
                tpmsDetectionData.battery = battery
                tpmsDetectionData.timeStamp = tyreDetectionEvent.timeStamp
                tpmsDetectionDatas.add(tpmsDetectionData)
            }
        }
        return tpmsDetectionDatas.toList() ?: emptyList()
    }

    /**
     * Observer Bluetooth adapter state changes
     */
    @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
    private fun observeBleStateChanges() {
        bleStateSubscription?.dispose()
        bleStateSubscription = rxBleClient?.observeStateChanges()
            ?.subscribe({
                when (it) {
                    /**
                     * When bluetooth hardware is ready, start scanning
                     */
                    RxBleClient.State.READY -> {
                        context?.let { it1 -> startScan(it1) }

                    }

                    RxBleClient.State.BLUETOOTH_NOT_AVAILABLE -> {
                        eventCallbackListener?.handleBleException(BleScanException.BLUETOOTH_NOT_AVAILABLE)
                    }
                    RxBleClient.State.LOCATION_PERMISSION_NOT_GRANTED -> {
                        eventCallbackListener?.handleBleException(BleScanException.LOCATION_PERMISSION_MISSING)
                    }
                    RxBleClient.State.BLUETOOTH_NOT_ENABLED -> {
                        Completable.complete()
                            .delay(2000, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnComplete {
                                eventCallbackListener?.handleBleException(BleScanException.BLUETOOTH_DISABLED)
                            }
                            .subscribe()
                    }
                    RxBleClient.State.LOCATION_SERVICES_NOT_ENABLED -> {
                        eventCallbackListener?.handleBleException(BleScanException.LOCATION_PERMISSION_MISSING)
                    }
                }
            }, { t: Throwable? ->
                t?.printStackTrace()
                if (t is BleScanException) {
                    eventCallbackListener?.handleBleException(t.reason)
                }
            })

    }


    fun addOnEventCallbackListener(eventCallbackListener: EventCallbackListener) {
        this.eventCallbackListener = eventCallbackListener

    }
}
