package wee.digital.widget.extension

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestListener
import java.io.File

@GlideModule
class MyGlideApp : AppGlideModule()

interface SimpleRequestListener : RequestListener<Drawable> {

    fun onCompleted()

    override fun onLoadFailed(
        e: GlideException?, model: Any?,
        target: com.bumptech.glide.request.target.Target<Drawable>?,
        isFirstResource: Boolean,
    ): Boolean {
        onCompleted()
        return true
    }

    override fun onResourceReady(
        resource: Drawable?, model: Any?,
        target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource?,
        isFirstResource: Boolean,
    ): Boolean {
        onCompleted()
        return true
    }
}

fun ImageView.load(url: String?) {
    val request = GlideApp.with(context)
        .load(url)
        .override(width, height)
    request.into(this)
}

fun ImageView.load(bitmap: Bitmap?) {
    val request = GlideApp.with(context)
        .load(bitmap)
        .override(width, height)
    request.into(this)
}

fun ImageView.load(res: Int) {
    val request = GlideApp.with(context)
        .load(res)
        .override(width, height)
    request.into(this)
}

fun ImageView.load(bytes: ByteArray?) {
    val request = GlideApp.with(context)
        .load(bytes)
        .override(width, height)
    request.into(this)
}

fun ImageView.load(drawable: Drawable?) {
    val request = GlideApp.with(context)
        .load(drawable)
        .override(width, height)
    request.into(this)
}

fun ImageView.load(file: File) {
    val request = GlideApp.with(context)
        .load(file)
        .override(width, height)
    request.into(this)
}

fun ImageView.reload(res: Int) {
    val request = GlideApp.with(context)
        .load(res)
        .override(width, height)
        .thumbnail(0.1f)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
    request.into(this)
}

fun ImageView.reload(url: String?) {
    val request = GlideApp.with(context)
        .load(url)
        .override(width, height)
        .thumbnail(0.1f)
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
    request.into(this)
}

fun ImageView.clear() {
    this
    val bitmap: Bitmap? = null
    GlideApp.with(context)
        .load(bitmap)
        .into(this)
}
