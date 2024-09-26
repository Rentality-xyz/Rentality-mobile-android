package com.eigen.rentality

import android.app.Application
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib

class RentalityApp: Application() {

    override fun onCreate() {
        super.onCreate()
        AppsFlyerLib.getInstance().init("SyNJWwbnshhwGcxYgPzYLH", null, this)
        AppsFlyerLib.getInstance().start(this)
    }
}