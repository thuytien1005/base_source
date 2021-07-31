package wee.digital.sample.ui.model

import android.net.Uri

class Media {
    var uri: Uri?
    var mimeType: String?
    var date: Long
    var width: Int
    var height: Int
    var size: Long
    var duration: Long
    var isBorderless: Boolean
    var isVideoGif: Boolean

    constructor(
        uri: Uri,
        mimeType: String,
        date: Long,
        width: Int,
        height: Int,
        size: Long,
        duration: Long,
        borderless: Boolean,
        videoGif: Boolean
    ) {
        this.uri = uri
        this.mimeType = mimeType
        this.date = date
        this.width = width
        this.height = height
        this.size = size
        this.duration = duration
        isBorderless = borderless
        isVideoGif = videoGif
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val media = o as Media
        return uri == media.uri
    }

    override fun hashCode(): Int {
        return uri.hashCode()
    }

    companion object {
        const val ALL_MEDIA_BUCKET_ID = "org.thoughtcrime.securesms.ALL_MEDIA"
    }
}