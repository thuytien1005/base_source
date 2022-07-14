package wee.digital.camera.rs

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.intel.realsense.librealsense.Device
import com.intel.realsense.librealsense.Pipeline
import com.intel.realsense.librealsense.RsContext
import kotlinx.coroutines.*
import wee.digital.camera.app
import wee.digital.camera.log

object RsController {

    private val stopPipelineDelayInMinutes get() = 10L
    var streamListener: RsStreamer.StreamListener? = null
    val imageLiveData = MutableLiveData<Bitmap?>()


    private var hasContext: Boolean = false
    private var isInitialized: Boolean = false
    private var isOnReset: Boolean = false
    var isStartStream: Boolean = false
    private var isStartPipeline: Boolean = false

    private var pauseStreamJob: Job? = null
    private var streamer = RsStreamer2()

    private var rsContext: RsContext? = null
    private var pipeline: Pipeline? = null
    private val rsDevice: Device?
        get() {
            var t: Device? = null
            rsContext?.queryDevices()?.foreach {
                t = it
                return@foreach
            }
            return t
        }

    /**
     * Stream configs
     */
    fun initContext() {
        if (hasContext) {
            return
        }
        RsDevice.findRsDevice {
            print("initContext")
            hasContext = true
            RsContext.init(app)
            resetAndRestart()
        }
    }

    fun resetAndRestart() {
        if (isOnReset) return
        isOnReset = true
        print("resetAndRestart")
        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                closePipeline()
                delay(2000)
                hardwareReset()
                delay(1000)
                RsDevice.findRsDevice { usb ->
                    RsOptions.syncDevice(usb)
                    delay(4000)
                    isInitialized = false
                    initPipeline()
                    if (isStartStream) {
                        delay(2000)
                        isStartStream = false
                        startPipeline()
                    }
                }
            }
            isOnReset = false
        }
    }

    fun hardwareReset() {
        val result = runBlocking(Dispatchers.IO) {
            runCatching {
                if (rsDevice != null) {
                    rsDevice?.hardwareReset()
                } else {
                    if (rsContext == null) {
                        rsContext = RsContext()
                    }
                    delay(1000)
                    rsContext?.queryDevices()?.foreach {
                        RsDeviceInfo.sync(it)
                        it.hardwareReset()
                    }
                }
            }
        }
        print("hardwareReset", result.exceptionOrNull())
    }

    /**
     * Rs hardware handle
     */
    private fun initPipeline() {
        if (isInitialized) return
        val result = runBlocking(Dispatchers.IO) {
            kotlin.runCatching {
                rsContext?.close()
                rsContext = RsContext()
                rsContext?.queryDevices()?.foreach {
                    pipeline = Pipeline(rsContext)
                }
            }
        }
        isInitialized = result.isSuccess
        print("initPipeline", result.exceptionOrNull())
        if (result.isFailure) {
            resetAndRestart()
        }
    }

    fun startPipeline() {
        isStartStream = true
        if (!isInitialized) return
        pauseStreamJob?.cancel()
        if (isStartPipeline && pipeline != null) {
            val result = runBlocking(Dispatchers.IO) {
                kotlin.runCatching {
                    streamer.start(pipeline!!)
                }
            }
            print("startPipeline (resume)", result.exceptionOrNull())
            return
        }
        val result = runBlocking(Dispatchers.IO) {
            kotlin.runCatching {
                if (pipeline == null) {
                    pipeline = Pipeline()
                }
                isStartPipeline = true
                streamer.start(pipeline!!)
            }
        }
        print("startPipeline", result.exceptionOrNull())
        if (result.isFailure) {
            resetAndRestart()
        }
    }

    fun delayToStopPipeline() {
        if (!isStartStream) return
        isStartStream = false
        if (!isInitialized) return
        print("delay to stopPipeline $stopPipelineDelayInMinutes minutes")
        streamer.pause()
        imageLiveData.postValue(null)
        pauseStreamJob?.cancel()
        pauseStreamJob = CoroutineScope(Dispatchers.IO).launch {
            delay(stopPipelineDelayInMinutes * 1000 * 60)
            stopPipeline()
        }
    }

    fun stopPipeline() {
        isStartPipeline = false
        if (!isInitialized) return
        val result = runBlocking(Dispatchers.IO) {
            kotlin.runCatching {
                streamer.close()
                pipeline?.stop()
            }
        }
        print("stopPipeline", result.exceptionOrNull())
        if (result.isFailure) {
            resetAndRestart()
        }
    }

    fun closePipeline() {
        if (!isInitialized) return
        isInitialized = false
        pauseStreamJob?.cancel()
        stopPipeline()
        val result = runBlocking(Dispatchers.IO) {
            kotlin.runCatching {
                RsDevice.sensor = null
                pipeline?.close()
                rsContext?.close()
                pipeline = null
            }
        }
        print("closePipeline", result.exceptionOrNull())
        if (result.isFailure) {
            resetAndRestart()
        }
    }

    /**
     *
     */
    private fun print(s: String?, t: Throwable? = null) {
        if (t != null) {
            log.e("Rs $s error: ${t.message}")
            streamListener?.onRsError(s)
        } else {
            log.d("Rs $s")
            streamListener?.onRsMessage(s)
        }
    }

    /**
     *
     */


}