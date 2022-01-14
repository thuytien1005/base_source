package wee.digital.sample

import android.app.Activity
import androidx.multidex.MultiDexApplication
import com.example.camera.CameraApp
import wee.digital.library.Library
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

private var activityReference: WeakReference<Activity>? = null

val currentActivity: Activity? get() = activityReference?.get()

