package com.treel.androidsdkdemo

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.treel.androidsdkdemo.databinding.ActivityMainBinding
import com.treel.androidsdkdemo.pushNotification.NotificationBuilder
import com.treel.androidsdkdemo.utility.ConstantData
import com.treel.androidsdkdemo.utility.DialogUtils
import com.treel.androidsdk.database.VehicleConfiguration
import com.treel.androidsdk.TreelTagScan
import com.treel.androidsdk.database.TpmsDetectionData
import com.treel.androidsdk.notification.AlertNotification
import com.treel.androidsdk.event.EventCallbackListener
import com.treel.androidsdk.exception.BleScanException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_tpms_data.*
import timber.log.Timber
import java.util.*

class MainActivity : BaseActivity(), EventCallbackListener, View.OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private var tagScan: TreelTagScan? = null
    private var adapter: TpmsDataAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationBuilder.newInstance(this).createNotificationChannelForOreoAndAbove()
        }

        tagScan = TreelTagScan(this@MainActivity)
        // tagScan.create(this)
        tagScan?.startBleScanning(this)
        tagScan?.addOnEventCallbackListener(this)

        binding.buttonFetchTpmsData.setOnClickListener(this)
        binding.buttonFetchAllVinNumberTpmsData.setOnClickListener(this)
        binding.buttonSyncConfig.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.buttonSyncConfig -> {
                val vehicleConfigurations = ConstantData.getVehicleConfigurations()
                val response = tagScan?.syncVehicleConfigurations(vehicleConfigurations)
                showErrorMsg(response)
            }
            binding.buttonFetchTpmsData -> {
                val latestTpmsDatas = tagScan?.fetchLatestTpmsData("VINNUMBER1")
                showTpmsDataOnTable(latestTpmsDatas)
            }
            binding.buttonFetchAllVinNumberTpmsData -> {
                val vinNumber = arrayOf("VINNUMBER1", "VINNUMBER2")
                val latestTpmsDatas = tagScan?.fetchLatestTpmsData(vinNumber)
                showTpmsDataOnTable(latestTpmsDatas)
            }
        }
    }

    private fun showTpmsDataOnTable(latestTpmsDatas: List<TpmsDetectionData>?) {
        layoutTpmsData.visibility = View.VISIBLE
        recyclerViewScanData.layoutManager = LinearLayoutManager(this)
        adapter = TpmsDataAdapter(latestTpmsDatas?.toList() ?: emptyList())
        recyclerViewScanData.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    override fun getCoordinatorLayout() = binding.coordinatorLayout as ViewGroup

    override fun onBluetoothEnabled() {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_RUNTIME_PERMISSION_REQUEST ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSettingsEnabled()
                } else {
                    onRuntimePermissionDenied(LOCATION_RUNTIME_PERMISSION_REQUEST)
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun handleBleException(errorCode: Int?) {
        val message = when (errorCode) {
            BleScanException.SCAN_FAILED_CONFIGURATION_NOT_AVAILABLE -> "Sensor Configuration not available"
            BleScanException.BLUETOOTH_NOT_AVAILABLE -> "Bluetooth is not available"
            BleScanException.BLUETOOTH_DISABLED -> "Enable bluetooth and try again"
            BleScanException.LOCATION_PERMISSION_MISSING -> "On Android 6.0 location permission is required. Implement Runtime Permissions"
            BleScanException.LOCATION_SERVICES_DISABLED -> "Location services needs to be enabled on Android 6.0"
            BleScanException.SCAN_FAILED_ALREADY_STARTED -> "Scan with the same filters is already started"
            BleScanException.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "Failed to register application for bluetooth scan"
            BleScanException.SCAN_FAILED_FEATURE_UNSUPPORTED -> "Scan with specified parameters is not supported"
            BleScanException.SCAN_FAILED_INTERNAL_ERROR -> "Scan failed due to internal error"
            BleScanException.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> "Scan cannot start due to limited hardware resources"
            BleScanException.UNDOCUMENTED_SCAN_THROTTLE -> String.format(
                Locale.getDefault(),
                "Android 7+ does not allow more scans. Try in %d seconds"
            )
            BleScanException.UNKNOWN_ERROR_CODE, BleScanException.BLUETOOTH_CANNOT_START -> "Unable to start scanning"
            else -> "Unable to start scanning"
        }

        DialogUtils.showYesNoAlert(
            this,
            R.string.action_ok,
            R.string.action_cancel,
            getString(R.string.label_warning),
            message,
            object : DialogUtils.OnDialogYesNoActionListener {
                override fun onYesClick() {
                    checkSettingsEnabled()
                }

                override fun onNoClick() {
                }
            })
    }
    /**
     * callback to Realtime TPMS data received
     */
    override fun onTpmsDataReceived(vehicleConfiguration: VehicleConfiguration) {
        Timber.d("Configuration Received", vehicleConfiguration)

    }

    /**
     * Firing notification
     */
    override fun showAlertNotification(alertNotification: AlertNotification) {
        Timber.d("alertNotification Received")
        NotificationBuilder.newInstance(this).sendNotification(alertNotification)
    }

}