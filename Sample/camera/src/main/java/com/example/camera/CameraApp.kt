package com.example.camera

import android.app.Application

object CameraApp {

    fun init(application: Application) {
        mApp = application
    }
}

private var mApp: Application? = null

val app: Application
    get() = mApp ?: throw NullPointerException(
        "Camera module must be init with " +
                "CameraApp.init(application: Application) in android.app.Application.onCreate()"
    )