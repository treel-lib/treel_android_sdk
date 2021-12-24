package com.treel.androidsdkdemo

import android.app.Application
import com.treel.androidsdk.TreelTagBeaconScan

class AndroidSDKApplication : Application() {
    private var treelTagScan: TreelTagBeaconScan? = null
        override fun onCreate() {
        super.onCreate()

       treelTagScan = TreelTagBeaconScan(this)
       // val n = TreelTagScan(this).create(this)
        //treelTagScan?.startScan(this)

        }

}