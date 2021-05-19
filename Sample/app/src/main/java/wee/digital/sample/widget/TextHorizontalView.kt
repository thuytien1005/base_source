package wee.digital.sample.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import kotlinx.android.synthetic.main.widget_text_horizontal.view.*
import wee.digital.library.extension.isGone
import wee.digital.library.widget.AppCustomView
import wee.digital.sample.R

class TextHorizontalView : AppCustomView {

    override fun layoutResource(): Int {
        return R.layout.widget_text_horizontal
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onInitialize(context: Context, types: TypedArray) {
        title = types.title
        textFieldTextViewTitle.setTextColor(types.textColorHint)
        text = types.text
        textFieldTextViewProperty.setTextColor(types.textColor)
        src = types.srcRes
    }

    @DrawableRes
    var src: Int = 0
        set(value) {
            textFieldImageViewIcon.isGone(value <= 0)
            textFieldImageViewIcon.setImageResource(value)
            field = value
        }

    var title: String? = null
        set(value) {
            textFieldTextViewTitle.text = value
            field = value
        }

    var text: String? = null
        set(value) {
            textFieldTextViewProperty.text = value
            field = value
        }

}