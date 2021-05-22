package wee.digital.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import kotlinx.android.synthetic.main.text_horizontal.view.*
import wee.digital.widget.base.AppCustomView
import wee.digital.widget.extension.isGone

class TextHorizontalView : AppCustomView {

    override fun layoutResource(): Int {
        return R.layout.text_horizontal
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