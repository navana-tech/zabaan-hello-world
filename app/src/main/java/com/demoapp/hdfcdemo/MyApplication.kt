package com.demoapp.hdfcdemo

import android.content.Context
import android.util.Log
import androidx.multidex.MultiDexApplication
import com.demoapp.hdfcdemo.MyApplication
import com.zabaan.sdk.InitializeParams
import com.zabaan.sdk.Zabaan
import java.lang.Exception

class MyApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        try {
            val params: InitializeParams = InitializeParams.Builder()
                .context(this)
                .accessToken(BuildConfig.ACCESS_TOKEN)
                .build()
            Zabaan.setDebug(true)
            Zabaan.setSandbox(true)
            Zabaan.init(params)
        } catch (e: Exception) {
            Log.e("Exception", "Navana Assistant Failed to initialize : "+e.message)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    companion object {
        var appContext: Context? = null
            private set
        var isIntroPlayed = false
    }
}