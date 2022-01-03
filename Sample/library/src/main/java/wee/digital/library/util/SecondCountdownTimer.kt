package wee.digital.library.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.*

open class SecondCountdownTimer(private val intervalMillis: Long = 10 * SECOND) {

    companion object {
        private const val SECOND = 1000L
        private val nowInMillis get() = System.currentTimeMillis()
    }

    private var startTime: Long = 0

    private var job: Job? = null

    private val stickLiveData = MutableLiveData<Long>()

    fun start(lifecycleOwner: LifecycleOwner) {
        startTime = nowInMillis
        stickLiveData.value = intervalMillis + startTime - nowInMillis
        stickLiveData.observe(lifecycleOwner, Observer { remainMillis ->
            remainMillis ?: return@Observer
            if (remainMillis > 0) {
                val seconds = remainMillis / 1000
                onTicks(seconds, "%02d:%02d".format(seconds / 60, seconds % 60))
            } else {
                onFinished()
            }
        })
        resume()
    }

    fun cancel() {
        job?.cancel(null)
    }

    private fun resume() {
        job = Job()
        CoroutineScope(Dispatchers.Default + job!!).launch {
            do {
                onDoStick()
                delay(200)
            } while (nowInMillis - startTime <= intervalMillis)
            onDoStick()
        }
    }

    private fun onDoStick() {
        val remainMillis = intervalMillis + startTime - nowInMillis
        stickLiveData.postValue(remainMillis)
    }

    open fun onTicks(second: Long, text: String) = Unit

    open fun onFinished() = Unit

}