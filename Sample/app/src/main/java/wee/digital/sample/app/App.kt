package wee.digital.sample.app

import android.app.Application
import androidx.lifecycle.LifecycleObserver
import wee.digital.library.Library
import wee.digital.library.util.Shared
import wee.digital.sample.BuildConfig

lateinit var app: App private set

val pref by lazy { Shared(BuildConfig.APPLICATION_ID) }

class App : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        app = this
        Library.app = this
    }

}
