package wee.digital.library.extension

import android.os.Looper
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

typealias Void = () -> Unit

fun Void?.does() {
    this?.also { it() }
}

typealias Block<reified T> = T.() -> Unit

fun <T> Block<T>?.doThis(t: T) {
    this?.also { t.it() }
}

fun <T> Block<T>?.doIt(t: T) {
    this?.also { it(t) }
}

data class Data<T>(val data: T?, val e: Exception?)

fun <T> emit(block: suspend FlowCollector<Data<T>>.() -> T): Flow<Data<T>> {
    return flow {
        try {
            emit(Data(this.block(), null))
        } catch (e: Exception) {
            emit(Data<T>(null, e))
        }
    }
}

fun <T> Flow<Data<T>>.each(block: (Data<T>) -> Unit) {
    flowOn(Dispatchers.IO).onEach {
        block(it)
    }.launchIn(GlobalScope)
}

val isOnUiThread: Boolean get() = Looper.myLooper() == Looper.getMainLooper()

fun LifecycleOwner.post(interval: Long = 0, block: Void) {
    flow {
        if (interval > 0) delay(interval)
        emit(true)
    }.flowOn(Dispatchers.Main).onEach {
        block()
    }.launchIn(this.lifecycleScope)
}

fun onUi(interval: Long = 0, block: Void) {
    flow {
        if (interval > 0) delay(interval)
        emit(true)
    }.flowOn(Dispatchers.Main).onEach {
        block()
    }.launchIn(GlobalScope)
}

fun onIo(interval: Long = 0, block: Void) {
    flow {
        if (interval > 0) delay(interval)
        emit(true)
    }.flowOn(Dispatchers.IO).onEach {
        block()
    }.launchIn(GlobalScope)
}