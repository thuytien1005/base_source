package wee.digital.widget.custom

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import wee.digital.widget.base.AppCustomView
import wee.digital.widget.databinding.TextHorizontalBinding
import wee.digital.widget.extension.isGone

class TextHorizontalView : AppCustomView<TextHorizontalBinding> {

    override fun inflating(): (LayoutInflater, ViewGroup?, Boolean) -> TextHorizontalBinding {
        return TextHorizontalBinding::inflate
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onInitialize(context: Context, types: TypedArray) {
        title = types.title
        bind.textViewTitle.setTextColor(types.textColorHint)
        text = types.text
        bind.textViewProperty.setTextColor(types.textColor)
        src = types.srcRes
    }

    @DrawableRes
    var src: Int = 0
        set(value) {
            bind.imageViewIcon.isGone(value <= 0)
            bind.imageViewIcon.setImageResource(value)
            field = value
        }

    var title: String? = null
        set(value) {
            bind.textViewTitle.text = value
            field = value
        }

    var text: String? = null
        set(value) {
            bind.textViewProperty.text = value
            field = value
        }


}