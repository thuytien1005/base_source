package wee.digital.sample.widget

import android.content.Context
import android.content.res.TypedArray
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.adapter.BaseListAdapter
import wee.digital.library.adapter.ItemInflating
import wee.digital.sample.databinding.WidgetInputPhotoBinding
import wee.digital.sample.databinding.WidgetInputPhotoItemBinding
import wee.digital.sample.ui.model.Media
import wee.digital.sample.widget.attach.InputAwareLayout
import wee.digital.sample.widget.attach.getMediaInBucket
import wee.digital.widget.base.AppCustomView
import wee.digital.widget.extension.load

class WidgetPhotoView : AppCustomView<WidgetInputPhotoBinding>, InputAwareLayout.InputView {

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)

    val adapter = AdapterGallery()

    override fun inflating(): (LayoutInflater, ViewGroup?, Boolean) -> WidgetInputPhotoBinding {
        return WidgetInputPhotoBinding::inflate
    }

    override fun onInitialize(context: Context, types: TypedArray) {}

    fun bindAdapterView() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = getMediaInBucket(
                context, Media.ALL_MEDIA_BUCKET_ID,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true
            )
            launch(Dispatchers.Main) {
                adapter.set(list)
                adapter.bind(bind.galleryViewRecycler, 3)
            }
        }
    }

    override fun show(height: Int, immediate: Boolean) {
        val params = layoutParams
        params.height = height
        layoutParams = params
        visibility = VISIBLE
    }

    override fun hide(immediate: Boolean) {
        visibility = GONE
    }

    override fun isShowing(): Boolean {
        return visibility == VISIBLE
    }

    inner class AdapterGallery : BaseListAdapter<Media>() {

        override fun itemInflating(item: Media, position: Int): ItemInflating {
            return WidgetInputPhotoItemBinding::inflate
        }

        override fun ViewBinding.onBindItem(item: Media, position: Int) {
            if (this is WidgetInputPhotoItemBinding) {
                post { widgetInputItemPhoto.load(item.uri!!) }
            }
        }

    }

}