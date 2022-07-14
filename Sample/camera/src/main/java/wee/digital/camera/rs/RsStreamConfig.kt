package wee.digital.camera.rs

import android.util.Size
import wee.digital.camera.log

class RsStreamConfig(val name: String) {

    var streamWidth: Int = 0
        get() {
            if (field == 0) throw IllegalArgumentException("Rs stream config is not set up")
            return field
        }

    var streamHeight: Int = 0
        get() {
            if (field == 0) throw IllegalArgumentException("Rs stream config is not set up")
            return field
        }

    var rate: Int = 0

    /**
     * Real sense is wide screen but size for preview, record, capture is square
     * Example: rs size is 1280x720 that is display is 720x720
     */
    val imageWidth: Int get() = streamHeight
    val imageSize: Size get() = Size(streamHeight, streamHeight)

    override fun equals(other: Any?): Boolean {
        return streamWidth == (other as? RsStreamConfig)?.streamWidth
    }

    fun update(width: Int, height: Int, rate: Int) {
        log.d("Rs $name stream: width: $width height: $height frameRate: $rate")
        this.streamWidth = width
        this.streamHeight = height
        this.rate = rate
    }
}