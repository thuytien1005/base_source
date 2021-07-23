package wee.digital.widget.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import wee.digital.widget.R
import wee.digital.widget.base.AppCustomView
import wee.digital.widget.databinding.AvatarBinding
import wee.digital.widget.extension.GlideApp

class AvatarView(context: Context, attrs: AttributeSet? = null) :
    AppCustomView<AvatarBinding>(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    override fun inflating(): (LayoutInflater, ViewGroup?, Boolean) -> AvatarBinding {
        return AvatarBinding::inflate
    }

    override fun onInitialize(context: Context, types: TypedArray) {
        bind.imageViewAvatar.setImageDrawable(types.src)
        bind.textViewName.text = types.text

        bind.textViewName.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            types.getDimension(R.styleable.AppCustomView_android_textSize, getPixels(R.dimen.textSize2))
        )
    }

    var image: String? = null
        set(value) {
            GlideApp.with(context)
                .load(value)
                .override(bind.imageViewAvatar.width, bind.imageViewAvatar.height)
                .transform(CircleCrop())
                .into(bind.imageViewAvatar)
            field = value
        }

    var text: String? = null
        set(value) {
            bind.textViewName.text = value
            field = value
        }

}