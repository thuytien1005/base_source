package wee.digital.widget

import android.app.Application
import android.view.View

object Widget {

    /**
     * Module must be set on create application
     */
    private var mApp: Application? = null

    var currentFocus: View? = null

    var app: Application
        set(value) {
            mApp = value
        }
        get() {
            if (null == mApp) throw NullPointerException("module not be set")
            return mApp!!
        }

}