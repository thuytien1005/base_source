package wee.digital.ml.base

import android.os.SystemClock
import wee.digital.ml.availableMemoryInMB
import java.util.*

class ImageProcessorInfoCallback {

    private val log = Logger("ImageProcessor")

    private var frameProcessedInOneSecondInterval = 0

    private var numRuns = 0

    private var framesPerSecond = 0

    private val fpsTimer = Timer().also {
        it.scheduleAtFixedRate(object : java.util.TimerTask() {
            override fun run() {
                framesPerSecond = frameProcessedInOneSecondInterval
                frameProcessedInOneSecondInterval = 0
            }
        }, 0, 1000)
    }


    fun onProcessInfo(frameStartMs: Long, detectorStartMs: Long) {
        val endMs = SystemClock.elapsedRealtime()
        val currentFrameLatencyMs = endMs - frameStartMs
        val currentDetectorLatencyMs = endMs - detectorStartMs
        if (numRuns >= 500) {
            resetLatencyStats()
        }
        numRuns++
        frameProcessedInOneSecondInterval++
        // Only log inference info once per second. When frameProcessedInOneSecondInterval is
        // equal to 1, it means this is the first frame processed during the current second.
        if (frameProcessedInOneSecondInterval == 1) {
            log.d("""
                Num of Runs: $numRuns
                Frame latency: $currentFrameLatencyMs
                Detector latency: $currentDetectorLatencyMs
                Fps: $framesPerSecond
                Memory available in system: $availableMemoryInMB MB
            """.trimIndent())
        }
    }

    fun cancel() {
        fpsTimer.cancel()
        resetLatencyStats()
    }

    private fun resetLatencyStats() {
        numRuns = 0
    }
}