package wee.digital.sample

import android.app.Activity
import androidx.lifecycle.LifecycleObserver
import androidx.multidex.MultiDexApplication
import wee.digital.library.Library
import wee.digital.library.extension.SimpleActivityLifecycleCallbacks
import wee.digital.widget.Widget
import java.lang.ref.WeakReference

class App : MultiDexApplication(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()
        app = this
        Library.init(this)
        Widget.init(this)
        app.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                activityReference = WeakReference(activity)
            }
        })
    }

}

lateinit var app: App private set

private var activityReference: WeakReference<Activity>? = null

val currentActivity: Activity? get() = activityReference?.get()



