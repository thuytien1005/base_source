package wee.digital.sample.app

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import wee.digital.library.Library
import wee.digital.library.util.SharedPref
import wee.digital.ml.ML
import wee.digital.sample.BuildConfig
import wee.digital.widget.Widget

lateinit var app: App private set

val pref by lazy { SharedPref(BuildConfig.APPLICATION_ID) }

class App : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        app = this
        Library.app = this
        Widget.app = this
        ML.app = this
    }

}
