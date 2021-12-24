package com.treel.androidsdkdemo

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.treel.androidsdkdemo.utility.DialogUtils
import com.treel.androidsdk.utility.Utility

abstract class BaseActivity : AppCompatActivity() {

    val LOCATION_RUNTIME_PERMISSION_REQUEST = 1003
    val STORAGE_RUNTIME_PERMISSION_REQUEST = 1005
    val CAMERA_RUNTIME_PERMISSION_REQUEST = 1006
    val BLUETOOTH_PERMISSION_REQUEST = 1001
    val LOCATION_PERMISSION_REQUEST = 1002
    val DRAW_OVERLAYS_PERMISSION_REQUEST_CODE = 1007

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        /*if (checkIsTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
        }*/
        /* val viewModel = ViewModelProviders.of(this).get(BaseViewModel::class.java) as BaseViewModel<V, P>

         if (viewModel.getPresenter<P>() == null) {
             viewModel.setPresenter(presenter)
         }
         presenter = viewModel.getPresenter<P>()!!
         presenter.let {
             it.attachLifecycle(lifecycle)
             it.attachView(this as V)
         }*/

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    fun checkSettingsEnabled(): Boolean {
        return if (Utility.isMarshMallow) {
            checkLocationSettings() && checkBluetoothSettings() && checkStoragePermission()
        } else {
            checkBluetoothSettings() && checkStoragePermission()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /*presenter.detachLifecycle(lifecycle)
        presenter.detachView()*/

    }

    fun showNoConnectivityError() {
        showSnack(getCoordinatorLayout(), R.string.no_connectivity, Snackbar.LENGTH_SHORT)
    }

    fun showUnknownError() {
        // showSnack(getCoordinatorLayout(), R.string.unknown_error, Snackbar.LENGTH_SHORT)
    }

    fun showSnack(
        parent: ViewGroup, messageResId: Int, length: Int,
        actionLabelResId: Int? = null, action: ((View) -> Unit)? = null,
        callback: ((Snackbar) -> Unit)? = null
    ) {
        showSnack(
            parent,
            getString(messageResId),
            length,
            actionLabelResId?.let { getString(it) },
            action,
            callback
        )
    }

    fun showSnack(
        parent: ViewGroup, message: String, length: Int,
        actionLabel: String? = null, action: ((View) -> Unit)? = null,
        callback: ((Snackbar) -> Unit)? = null
    ) {
        val snack = Snackbar.make(parent, message, length)
            .apply {
                if (actionLabel != null) {
                    setAction(actionLabel, action)
                }
            }
        customizeSnackbar(this, snack)
        snack.show()

    }

    private fun customizeSnackbar(context: Context, snackbar: Snackbar) {
        snackbar.setActionTextColor(Color.WHITE)
        val sbView = snackbar.view
        sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
        //TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);

    }

    fun showErrorMsg(string: String?) {
        showSnack(getCoordinatorLayout(), string!!, Snackbar.LENGTH_SHORT)
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun checkRuntimePermission1(permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            var msg: String? = null
            var permissions = arrayOfNulls<String>(0)
            var permissionRequest = 0
            when (permission) {
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        msg =
                            resources.getString(R.string.location_runtime_permission_required_android_q)
                    } else {
                        msg = resources.getString(R.string.location_runtime_permission_required)
                    }
                    permissions = arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    permissionRequest = LOCATION_RUNTIME_PERMISSION_REQUEST
                }
                Manifest.permission.READ_EXTERNAL_STORAGE -> {
                    msg = resources.getString(R.string.storage_runtime_permission_required)
                    permissions = arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    permissionRequest = STORAGE_RUNTIME_PERMISSION_REQUEST
                }
                Manifest.permission.CAMERA -> {
                    msg = resources.getString(R.string.camera_runtime_permission_required)
                    permissions = arrayOf(Manifest.permission.CAMERA)
                    permissionRequest = CAMERA_RUNTIME_PERMISSION_REQUEST
                }
            }

            if (!shouldShowRequestPermissionRationale(permission)) {
                if (permissionRequest == LOCATION_RUNTIME_PERMISSION_REQUEST) {
                    showPermissionDialog(msg!!, permissions, permissionRequest)
                } else {
                    showPermissionDialog(msg!!, permissions, permissionRequest)
                }
                return true
            }
            if (permissionRequest == LOCATION_RUNTIME_PERMISSION_REQUEST && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                requestLocationPermission()
            else
                ActivityCompat.requestPermissions(this@BaseActivity, permissions, permissionRequest)

            return true
        }
        return false
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun showPermissionDialog(
        msg: String,
        permissions: Array<String?>,
        permissionRequest: Int
    ) {

        DialogUtils.showYesNoAlert(this,
            resources.getString(R.string.permission_required),
            msg,
            object : DialogUtils.OnDialogYesNoActionListener {

                override fun onYesClick() {
                    if (permissionRequest == LOCATION_RUNTIME_PERMISSION_REQUEST && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                        requestLocationPermission()
                    else
                        ActivityCompat.requestPermissions(
                            this@BaseActivity,
                            permissions,
                            permissionRequest
                        )
                }

                override fun onNoClick() {
                    onRuntimePermissionDenied(permissionRequest)
                }
            })
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun onRuntimePermissionDenied(permissionRequest: Int) {
        var msg: String? = null
        var permissions = arrayOfNulls<String>(0)
        when (permissionRequest) {
            LOCATION_RUNTIME_PERMISSION_REQUEST -> {
                msg = resources.getString(R.string.location_permission_denied)
                permissions =
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            STORAGE_RUNTIME_PERMISSION_REQUEST -> {
                msg = resources.getString(R.string.storage_runtime_permission_required)
                permissions = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
            CAMERA_RUNTIME_PERMISSION_REQUEST -> {
                msg = resources.getString(R.string.camera_runtime_permission_required)
                permissions = arrayOf(Manifest.permission.CAMERA)
            }
        }

        DialogUtils.showSnackBarMsg(
            this,
            findViewById(android.R.id.content),
            msg!!,
            Snackbar.LENGTH_INDEFINITE,
            resources.getString(R.string.allow)
        ) {
            if (permissionRequest == LOCATION_RUNTIME_PERMISSION_REQUEST && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                requestLocationPermission()
            else
                ActivityCompat.requestPermissions(this@BaseActivity, permissions, permissionRequest)
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun checkLocationSettings(): Boolean {
        if (!Utility.isLocationServiceEnabled(this)) {
            DialogUtils.showYesNoAlert(this,
                resources.getString(R.string.location_permission_title),
                resources.getString(R.string.location_enable_request),
                object : DialogUtils.OnDialogYesNoActionListener {
                    override fun onYesClick() {
                        fireLocationServiceIntent()
                    }

                    override fun onNoClick() {
                        showLocationServiceRequiredMsg()
                    }
                })
            return false
        }
        return !checkRuntimePermission1(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun checkBluetoothSettings(): Boolean {
        if (!Utility.isBluetoothSupported) {
            DialogUtils.showOkAlert(
                this,
                resources.getString(R.string.bluetooth_permission_title),
                resources.getString(R.string.bluetooth_not_supported)
            ) {
                //finish()
            }
        } else if (!Utility.isBluetoothEnabled) {
            DialogUtils.showYesNoAlert(this,
                resources.getString(R.string.bluetooth_permission_title),
                resources.getString(R.string.bluetooth_enable_request),
                object : DialogUtils.OnDialogYesNoActionListener {
                    override fun onYesClick() {
                        fireBluetoothEnableIntent()
                    }

                    override fun onNoClick() {
                        showBluetoothRequiredMsg()
                    }
                })
            return false
        } else {
            onBluetoothEnabled()
        }
        return true
    }

    private fun checkStoragePermission(): Boolean {
        if (!BuildConfig.DEBUG) return true
        if (!checkRuntimePermission1(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return true
        }
        return false
    }

    private fun showBluetoothRequiredMsg() {
        DialogUtils.showSnackBarMsg(
            this,
            getCoordinatorLayout(),
            resources.getString(R.string.bluetooth_required_to_use_app),
            Snackbar.LENGTH_LONG,
            resources.getString(R.string.enable),
            { fireBluetoothEnableIntent() })
    }

    private fun showLocationServiceRequiredMsg() {
        DialogUtils.showSnackBarMsg(
            this,
            getCoordinatorLayout(),
            resources.getString(R.string.location_service_required),
            Snackbar.LENGTH_LONG,
            resources.getString(R.string.enable),
            { fireLocationServiceIntent() })
    }

    private fun fireBluetoothEnableIntent() {

        Utility.enableBluetooth()
        DialogUtils.showSnackBarMsg(
            this,
            getCoordinatorLayout(),
            resources.getString(R.string.bluetooth_enabled),
            Snackbar.LENGTH_SHORT
        )
        onBluetoothEnabled()
    }

    internal fun fireLocationServiceIntent() {
        val locationServiceIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivityForResult(locationServiceIntent, LOCATION_PERMISSION_REQUEST)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestForDrawingOverAppsPermission() {
        val intent =
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, DRAW_OVERLAYS_PERMISSION_REQUEST_CODE)
    }

    fun isDrawOverlaysAllowed(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            BLUETOOTH_PERMISSION_REQUEST -> if (resultCode == RESULT_OK) {
                DialogUtils.showSnackBarMsg(
                    this,
                    getCoordinatorLayout(),
                    resources.getString(R.string.bluetooth_enabled),
                    Snackbar.LENGTH_SHORT
                )
                //onBluetoothEnabled()

            } else {
                DialogUtils.showSnackBarMsg(
                    this,
                    getCoordinatorLayout(),
                    resources.getString(R.string.bluetooth_enabled_failed),
                    Snackbar.LENGTH_LONG,
                    resources.getString(R.string.enable)
                ) { fireBluetoothEnableIntent() }
            }
            LOCATION_PERMISSION_REQUEST ->
                if (resultCode == RESULT_OK || Utility.isLocationServiceEnabled(this)) {
                    showLocationEnabledMsgAndCheckBluetooth()
                } else {
                    DialogUtils.showSnackBarMsg(
                        this,
                        getCoordinatorLayout(),
                        resources.getString(R.string.location_enabled_failed),
                        Snackbar.LENGTH_LONG,
                        resources.getString(R.string.enable)
                    ) { fireLocationServiceIntent() }
                }
            DRAW_OVERLAYS_PERMISSION_REQUEST_CODE -> if (isDrawOverlaysAllowed()) {
                // startFloatingWidgetMaybe()
            }
            else -> {
            }
        }

    }

    /* private fun startFloatingWidgetMaybe() {
         if (isDrawOverlaysAllowed()) {
             startService(Intent(this, FloatingWidgetService::class.java))
             return
         }
     }*/
    private fun showLocationEnabledMsgAndCheckBluetooth() {
        DialogUtils.showSnackBarMsg(
            this,
            getCoordinatorLayout(),
            resources.getString(R.string.location_enabled),
            Snackbar.LENGTH_SHORT
        )
        if (!checkRuntimePermission1(Manifest.permission.ACCESS_FINE_LOCATION)) {
            if (checkBluetoothSettings()) {
                onBluetoothEnabled()
            }
        }
    }

    private fun requestLocationPermission() {
        val foreground = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (foreground) {
            val background = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (background) {
                // handleLocationUpdates()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    LOCATION_RUNTIME_PERMISSION_REQUEST
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION

                ), LOCATION_RUNTIME_PERMISSION_REQUEST
            )
        }
    }


    abstract fun getCoordinatorLayout(): ViewGroup

    abstract fun onBluetoothEnabled()

  /*  fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (!connectivityManager.isConnected) {
            return false
        }
        return true
    }*/

}