package wee.digital.camera.rs

import com.intel.realsense.librealsense.*
import kotlinx.coroutines.*
import wee.digital.camera.log

class RsStreamer {

    interface StreamListener {
        fun onRsMessage(s: String?) = Unit
        fun onRsError(s: String?) = Unit
        fun onRsFrameSnapshot(snapshot: FrameSnapshot) = Unit
    }

    private var pipelineProfile: PipelineProfile? = null
    private var isStreaming: Boolean = false

    /**
     * Sync [RsDevice.sensor], [RsDisplayInfo]
     * then repeat this.[resume]
     */
    fun start(pipeline: Pipeline) {
        isStreaming = true
        imageProcessing = false
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

    /**
     * [pipeline.waitForFrames()] repeat and execute [onFrameFilter]
     */
    private var streamScope: Job? = null

    private fun resume(pipeline: Pipeline) {
        streamScope?.cancel()
        streamScope = CoroutineScope(Dispatchers.IO).launch {
            try {
                while (true) {
                    if (isStreaming) {
                        RsOptions.waitFramesLog.start()
                        val releaser = FrameReleaser()
                        val frameSet: FrameSet = pipeline
                            .waitForFrames()
                            .releaseWith(releaser)
                        RsOptions.waitFramesLog.print()
                        onFrameFilter(releaser, frameSet)
                    } else {
                        break
                    }
                }
            } catch (t: Throwable) {
                isStreaming = false
                log.e("Rs streamer waitForFrames error: ${t.message}")
                cancel()
                RsController.resetAndRestart()
            }
        }
    }

    fun pause() {
        log.d("Rs cancel stream")
        streamScope?.cancel()
        imageProcessing = true
        isStreaming = false

    }

    fun close() {
        pause()
        pipelineProfile?.close()
        pipelineProfile = null
    }

    /**
     * Repeat process color frame and depth frame
     * to create [FrameSnapshot] for this.[onImageProcess] method
     */
    @Volatile
    private var isGetFrames: Boolean = false
    private var getFrameScope: Job? = null

    private fun onFrameFilter(releaser: FrameReleaser, frameSet: FrameSet) {
        if (isGetFrames) {
            releaser.close()
            return
        }
        RsOptions.getFramesLog.start()
        isGetFrames = true
        getFrameScope?.cancel()
        getFrameScope = CoroutineScope(Dispatchers.IO).launch {
            val snapshot = getFrameSnapshot(releaser, frameSet)
            releaser.close()
            isGetFrames = false
            RsOptions.getFramesLog.print()
            onImageProcess(snapshot)
            RsOptions.logFps()
        }
    }

    private fun getFrameSnapshot(releaser: FrameReleaser, frameSet: FrameSet): FrameSnapshot {
        val colorFrame = frameSet.first(StreamType.COLOR).releaseWith(releaser)
        val alignFrame = (RsOptions.align?.process(frameSet) ?: frameSet).releaseWith(releaser)
        val colorizerFrame = alignFrame.applyFilter(RsOptions.colorizer).releaseWith(releaser)
        val depthFrame = colorizerFrame.first(StreamType.DEPTH).releaseWith(releaser)
        return FrameSnapshot(colorFrame, depthFrame)
    }

    /**
     * Callback for interfaces and post by livedata:
     * [RsController.recordVideo] for record image
     * [RsController.imageLiveData]  for preview
     * [RsController.streamListener] for image analyze
     */
    @Volatile
    private var imageProcessing: Boolean = false
    private var imageProcessScope: Job? = null

    private fun onImageProcess(snapshot: FrameSnapshot) {
        if (!RsController.isStartStream || imageProcessing) {
            snapshot.close()
            return
        }
        imageProcessing = true
        imageProcessScope?.cancel()
        imageProcessScope = CoroutineScope(Dispatchers.IO).launch {
            callbackFrame(snapshot)
            imageProcessing = false
        }
    }

    private fun callbackFrame(snapshot: FrameSnapshot) {
        snapshot.colorData.convertMatToBitmap()
        snapshot.depthData.convertMatToBitmap()
        if (RsController.isStartStream) {
            if (RsController.imageLiveData.hasActiveObservers()) {
                RsController.imageLiveData.postValue(snapshot.colorData.safeBitmap)
            }
            RsController.streamListener?.onRsFrameSnapshot(snapshot)
            //RsController.recordVideo?.pushFrame(snapshot.colorData.safeBitmap)
        }
    }

}