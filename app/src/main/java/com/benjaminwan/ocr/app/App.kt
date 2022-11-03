package com.benjaminwan.ocr.app

import android.app.Application
import com.airbnb.mvrx.Mavericks
import com.benjaminwan.ocr.BuildConfig
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy

class App : Application() {
    companion object {
        lateinit var INST: App
    }

    override fun onCreate() {
        super.onCreate()
        INST = this
        initLogger()
        Mavericks.initialize(false)
    }

    private fun initLogger() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(true)
            .tag("AndroidOcr")
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean = BuildConfig.DEBUG
        })
    }
}