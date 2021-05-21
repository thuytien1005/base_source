package wee.digital.library.extension

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


interface SimpleMotionTransitionListener : MotionLayout.TransitionListener {
    override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
    }

    override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {
    }

    override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
    }

    override fun onTransitionTrigger(layout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
    }
}

interface SimpleActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {}

    override fun onActivityStopped(activity: Activity) {}
}

abstract class SimpleLifecycleObserver : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onEventCreated() {
        onCreated()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onEventStart() {
        onStart()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onEventResume() {
        onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onEventPause() {
        onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onEventStop() {
        onStop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onEventDestroy() {
        onDestroy()
    }

    open fun onCreated() {}
    open fun onStart() {}
    open fun onResume() {}
    open fun onPause() {}
    open fun onStop() {}
    open fun onDestroy() {}
}

abstract class SimpleCountDownTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
    override fun onFinish() = Unit
    override fun onTick(millisUntilFinished: Long) = Unit
}

interface SimpleTextWatcher : TextWatcher {

    fun EditText.setSilentText(s: String) {
        removeTextChangedListener(this@SimpleTextWatcher)
        setText(s)
        setSelection(s.length)
        addTextChangedListener(this@SimpleTextWatcher)
    }

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        textChange(s.toString())
    }

    fun textChange(string: String) {}

}
