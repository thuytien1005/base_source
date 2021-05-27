package wee.digital.ml.base

import android.util.Log
import wee.digital.ml.BuildConfig


open class Logger(private val tag: String) {

    fun d(s: String?) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, s.toString())
        }
    }

    fun e(e: Throwable) {
        Log.e(tag, e.message.toString())
    }
}
