package wee.digital.sample.utils

import android.graphics.Color
import androidx.annotation.DrawableRes
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import wee.digital.sample.ui.model.StoreUser
import wee.digital.widget.custom.AvatarView
import wee.digital.widget.extension.drawable
import wee.digital.widget.extension.notNullOrEmpty

fun AvatarView.bind(it: StoreUser?) {
    it ?: return
    when {
        it.photoDisplay.notNullOrEmpty() -> {
            image = it.photoDisplay
        }
        else -> {
            val sb = StringBuilder()
            if (it.firstName.notNullOrEmpty()) {
                sb.append(it.firstName.substring(0, 1))
            }
            if (it.lastName.notNullOrEmpty()) {
                sb.append(it.lastName?.substring(0, 1) ?: "")
            }
            text = sb.toString()
        }
    }
}

fun homeNavItem(title: String, @DrawableRes res: Int): AHBottomNavigationItem {
    return AHBottomNavigationItem(
        title,
        drawable(res),
        Color.WHITE
    )
}