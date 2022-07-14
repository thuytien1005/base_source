package wee.digital.sample

import android.app.Activity
import androidx.multidex.MultiDexApplication
import wee.digital.camera.CameraApp
import wee.digital.library.Library
import wee.digital.library.util.Logger
import wee.digital.widget.Widget
import java.lang.ref.WeakReference


class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        app = this
        Library.init(this)
        Widget.init(this)
        CameraApp.init(this)
    }

}

lateinit var app: App private set

val log by lazy { Logger("sampleApp") }

private var activityReference: WeakReference<Activity>? = null

val currentActivity: Activity? get() = activityReference?.get()

