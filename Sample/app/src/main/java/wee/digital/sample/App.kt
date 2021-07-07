package wee.digital.sample

import android.app.Activity
import android.app.Application
import androidx.lifecycle.LifecycleObserver
import wee.digital.library.Library
import wee.digital.widget.Widget
import java.lang.ref.WeakReference

class App : Application(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        app = this
        Library.init(this)
        Widget.init(this)
    }

}

lateinit var app: App private set

private var activityReference: WeakReference<Activity>? = null

val currentActivity: Activity? get() = activityReference?.get()



