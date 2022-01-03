package wee.digital.widget.extension

import android.view.View
import android.widget.EditText
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private var lastClickViewId: Int = 0

abstract class ViewClickListener(private val delayedInterval: Long = 300) : View.OnClickListener {

    @Volatile
    var onTrigger: Boolean = true

    private var lastClickTime: Long = System.currentTimeMillis()

    private val isDelayed: Boolean get() = System.currentTimeMillis() - lastClickTime > delayedInterval

    private val View.isAcceptClick: Boolean get() = id != lastClickViewId

    abstract fun onClicks(v: View)

    final override fun onClick(v: View?) {
        v ?: return
        if ((isDelayed || v.isAcceptClick) && onTrigger) {
            lastClickViewId = v.id
            lastClickTime = System.currentTimeMillis()
            onTrigger = false
            onClicks(v)
            GlobalScope.launch {
                delay(delayedInterval)
                onTrigger = true
            }
        }
    }

}

fun View?.addViewClickListener(delayedInterval: Long, listener: ((View?) -> Unit)? = null) {
    this ?: return
    if (listener == null) {
        setOnClickListener(null)
        if (this is EditText) {
            isFocusable = true
            isCursorVisible = true
        }
        return
    }
    setOnClickListener(object : ViewClickListener(delayedInterval) {
        override fun onClicks(v: View) {
            listener(v)
        }
    })
    if (this is EditText) {
        isFocusable = false
        isCursorVisible = false
    }
}

fun View?.addViewClickListener(listener: ((View?) -> Unit)? = null) {
    addViewClickListener(0, listener)
}

fun addClickListeners(vararg views: View?, block: (View?) -> Unit) {
    val listener = object : ViewClickListener() {
        override fun onClicks(v: View) {
            block(v)
        }
    }
    views.forEach {
        it?.setOnClickListener(listener)
    }
}

fun clearClickListeners(vararg views: View?) {
    views.forEach {
        it?.setOnClickListener(null)
    }
}

abstract class FastClickListener(private val clickCount: Int) : View.OnClickListener {

    private var lastClickTime: Long = 0

    private var currentClickCount: Int = 0

    abstract fun onViewClick(v: View?)

    final override fun onClick(v: View?) {
        if (System.currentTimeMillis() - lastClickTime > 420 || currentClickCount >= clickCount) {
            currentClickCount = 0
        }
        lastClickTime = System.currentTimeMillis()
        currentClickCount++
        if (currentClickCount == clickCount) {
            lastClickTime = 0
            currentClickCount = 0
            onViewClick(v)
        }
    }
}

fun View?.addFastClickListener(clickCount: Int, block: () -> Unit) {
    this?.setOnClickListener(object : FastClickListener(clickCount) {
        override fun onViewClick(v: View?) {
            block()
        }
    })
}

