package wee.digital.ml

import android.app.Application

object ML {

    /**
     * Module must be set on create application
     */
    private var mApp: Application? = null

    var app: Application
        set(value) {
            mApp = value
        }
        get() {
            if (null == mApp) throw NullPointerException("module not be set")
            return mApp!!
        }

}