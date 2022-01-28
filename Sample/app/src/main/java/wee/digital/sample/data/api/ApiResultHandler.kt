package wee.digital.sample.data.api

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import retrofit2.HttpException
import wee.digital.library.extension.flowResult
import wee.digital.sample.app
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Manifest:
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 */
class ApiResultHandler<T> {

    private val flow: Flow<Result<T?>>

    private var job: Job? = null

    val isCompleted get() = job?.isCompleted ?: true

    constructor(flow: Flow<Result<T?>>) {
        this.flow = flow
    }

    constructor(block: suspend () -> T) {
        this.flow = flowResult {
            return@flowResult block()!!
        }
    }

    private fun onEach(): Flow<Result<T?>> {
        return flow.onEach {
            onCompleted?.invoke(it.getOrNull())
            if (it.isSuccess) {
                onSuccess?.invoke(it.getOrNull()!!)
            } else {
                onHandleThrowable(it.exceptionOrNull())
            }
        }
    }

    private var onSuccess: (T.() -> Unit)? = null

    private var onFailure: (Throwable.() -> Unit)? = null

    private var onNetworkException: (IOException.() -> Unit) = {
        networkErrorLiveData.postValue(this)
        onFailure?.invoke(this)
    }

    private var onHttpException: (HttpException.() -> Unit) = {
        httpErrorLiveData.postValue(this)
        onFailure?.invoke(this)
    }

    private var onCompleted: (T?.() -> Unit)? = null

    private fun onHandleThrowable(it: Throwable?) {
        when (it) {
            is SocketException, is UnknownHostException, is SocketTimeoutException -> {
                onNetworkException(it as IOException)
            }
            is HttpException -> {
                onHttpException(it)
            }
        }
        onFailure?.invoke(it ?: NullPointerException("Unknown error"))
    }

    private fun networkDisconnected(): Boolean {
        val cm = app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        when {
            Build.VERSION.SDK_INT > Build.VERSION_CODES.O -> {
                val capabilities = cm.getNetworkCapabilities(cm.activeNetwork) ?: return true
                capabilities.run {
                    return when (true) {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI),
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR),
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> false
                        else -> true
                    }
                }
            }
            Build.VERSION.SDK_INT > Build.VERSION_CODES.M -> @Suppress("DEPRECATION") {
                val networkInfo = cm.activeNetworkInfo ?: return true
                networkInfo.run {
                    return when (type) {
                        ConnectivityManager.TYPE_WIFI,
                        ConnectivityManager.TYPE_MOBILE -> false
                        else -> true
                    }
                }
            }
            else -> @Suppress("DEPRECATION") {
                return cm.activeNetworkInfo?.isConnected != true
            }
        }
    }

    fun onSuccess(block: T.() -> Unit): ApiResultHandler<T> {
        onSuccess = block
        return this
    }

    fun onFailure(block: Throwable.() -> Unit): ApiResultHandler<T> {
        onFailure = block
        return this
    }

    fun onCompleted(block: T?.() -> Unit): ApiResultHandler<T> {
        onCompleted = block
        return this
    }

    fun onNetworkError(block: IOException.() -> Unit): ApiResultHandler<T> {
        onNetworkException = {
            block(this)
            networkErrorLiveData.postValue(this)
            onFailure?.invoke(this)
        }
        return this
    }

    fun onHttpError(block: HttpException.() -> Unit): ApiResultHandler<T> {
        onHttpException = {
            block(this)
            onFailure?.invoke(this)
        }
        return this
    }

    fun cancel() {
        progressLiveData.postValue(false)
        job?.cancel()
    }

    fun launch(scope: CoroutineScope): ApiResultHandler<T> {
        if (networkDisconnected()) {
            progressLiveData.postValue(false)
            onNetworkException.invoke(SocketException())
            return this
        }
        progressLiveData.postValue(true)
        job = onEach().onCompletion {
            progressLiveData.postValue(false)
        }.onEmpty {
            progressLiveData.postValue(false)
        }.launchIn(scope)
        return this
    }

}