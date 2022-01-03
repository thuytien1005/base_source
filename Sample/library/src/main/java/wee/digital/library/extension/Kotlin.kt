package wee.digital.library.extension

import android.os.Looper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

val isOnUiThread: Boolean get() = Looper.myLooper() == Looper.getMainLooper()

fun onUi(interval: Long = 0, block: () -> Unit) {
    GlobalScope.launch {
        if (interval > 0) delay(interval)
        withContext(Dispatchers.Main) {
            block()
        }
    }
}

fun <T : Any> flowResult(block: suspend FlowCollector<Result<T>>.() -> T): Flow<Result<T>> {
    return flow {
        emit( runCatching { block() })
    }.flowOn(Dispatchers.IO)
}


fun ByteArray?.isNullOrEmpty(): Boolean {
    this ?: return true
    return this.isEmpty()
}