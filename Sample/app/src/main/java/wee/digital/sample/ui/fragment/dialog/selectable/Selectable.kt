package wee.digital.sample.ui.fragment.dialog.selectable

import android.graphics.Color
import wee.digital.sample.R
import wee.digital.widget.extension.color

open class Selectable(
    val id: String = "",
    var ic: Int = R.drawable.ic_check,
    var icColor: Int = color(R.color.colorPrimary),
    var icBackground: Int = Color.WHITE,
    val text: String? = null,
    val description: String? = null,
    var value: Long = 0
) {
    override fun equals(other: Any?): Boolean {
        return (other as? Selectable)?.id == id
    }

    override fun toString(): String {
        return text ?: ""
    }
}
