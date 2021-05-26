package wee.digital.sample.app

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import wee.digital.library.Library
import wee.digital.library.util.SharedPref
import wee.digital.ml.ML
import wee.digital.sample.BuildConfig
import wee.digital.widget.Widget

class App : Application(), LifecycleObserver , CameraXConfig.Provider {

    override fun onCreate() {
        super.onCreate()
        app = this
        Library.app = this
        Widget.app = this
        ML.app = this
    }
    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}

lateinit var app: App private set

val appId: String get() = BuildConfig.APPLICATION_ID

val pref: SharedPref by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { SharedPref(appId) }
