package wee.digital.camera.rs

import com.intel.realsense.librealsense.*
import kotlinx.coroutines.*
import wee.digital.camera.log
import java.util.*

class RsStreamer2 {

    private var pipelineProfile: PipelineProfile? = null
    private var isStreaming: Boolean = false
    private val frameQueue: Queue<FrameSet> = LinkedList()

    private var streamScope: Job? = null
    private var getFrameScope: Job? = null

    fun start(pipeline: Pipeline) {
        isStreaming = true
        if (pipelineProfile != null) {
            resume(pipeline)
            return
        }
        pipelineProfile = pipeline.start(RsOptions.streamConfig)?.also {
            log.d("Rs streamer start")
            it.device.querySensors()?.find { sensor -> sensor.`is`(Extension.COLOR_SENSOR) }
                ?.also { sensor ->
                    RsDevice.sensor = sensor.`as`(Extension.COLOR_SENSOR)
                    if (RsDisplayInfo.hasDefault) {
                        RsDisplayInfo.sync(sensor)
                    } else {
                        RsDisplayInfo.hasDefault = true
                        RsDisplayInfo.default(sensor)
                    }
                }
            resume(pipeline)
        }
    }

    private fun resume(pipeline: Pipeline) {
        streamScope?.cancel()
        streamScope = CoroutineScope(Dispatchers.IO).launch {
            while (isStreaming) runBlocking {
                repeatWaitForFrames(pipeline)
            }
        }
        getFrameScope?.cancel()
        getFrameScope = CoroutineScope(Dispatchers.IO).launch {
            while (isStreaming) runBlocking {
                repeatFrameFilter()
                delay(20)
                RsOptions.logFps(frameQueue.size)
            }
        }
    }

    private fun repeatWaitForFrames(pipeline: Pipeline) {
        try {
            RsOptions.waitFramesLog.start()
            val frameSet: FrameSet = pipeline.waitForFrames()
            if (frameQueue.size >= 5) {
                frameQueue.poll()?.close()
            }
            frameQueue.add(frameSet.clone())
            frameSet.close()
            RsOptions.waitFramesLog.end()
        } catch (t: Throwable) {
            isStreaming = false
            log.e("Rs streamer waitForFrames error: ${t.message}")
            pause()
            RsController.resetAndRestart()
        }
    }

    private fun repeatFrameFilter(): Boolean {
        try {
            if (frameQueue.isEmpty()) return false
            val frameSet: FrameSet? = frameQueue.peek()?.clone()
            frameQueue.poll()?.close()
            frameSet ?: return true
            RsOptions.getFramesLog.start()

            val releaser = FrameReleaser()
            frameSet.releaseWith(releaser)
            val colorFrame = frameSet.first(StreamType.COLOR).releaseWith(releaser)

            if (RsDepthOffer.isGetDepthFrame) {
                val alignFrame =
                    (RsOptions.align?.process(frameSet) ?: frameSet).releaseWith(releaser)
                val colorizerFrame =
                    alignFrame.applyFilter(RsOptions.colorizer).releaseWith(releaser)
                val depthFrame = colorizerFrame.first(StreamType.DEPTH)?.releaseWith(releaser)
                RsDepthOffer.emit(depthFrame)
            }

            val snapshot = FrameSnapshot(colorFrame, null)
            releaser.close()

            callbackFrames(snapshot)
            RsOptions.getFramesLog.end()

        } catch (t: Throwable) {
            log.e("Rs streamer getFrameData error: ${t.message}")
        }
        return true
    }

    private fun callbackFrames(snapshot: FrameSnapshot) {
        if (RsController.isStartStream) {
            snapshot.colorData.convertMatToBitmap()
            if (RsController.imageLiveData.hasActiveObservers()) {
                RsController.imageLiveData.postValue(snapshot.colorData.safeBitmap)
            }
            RsController.streamListener?.onRsFrameSnapshot(snapshot)
            //RsController.recordVideo?.pushFrame(snapshot.colorData.safeBitmap)
        }
    }

    fun pause() {
        log.d("Rs cancel stream")
        streamScope?.cancel()
        getFrameScope?.cancel()
        isStreaming = false
    }

    fun close() {
        pause()
        pipelineProfile?.close()
        pipelineProfile = null
    }

}