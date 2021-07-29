package wee.digital.sample.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.view.WindowManager
import androidx.annotation.DrawableRes
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import wee.digital.sample.ui.model.StoreChat
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

fun AvatarView.bind(it: String?) {
    try {
        it ?: return
        var name = ""
        it.split(" ").forEach {
            name += it.first()
        }
        text = name
    } catch (e: Exception) {
        text = "er"
    }
}

fun AvatarView.bind(it: StoreChat): String {
    return when (it.type == "group") {
        true -> {
            bind(it.name)
            it.name
        }
        else -> {
            val user = it.listUserInfo?.first() ?: StoreUser()
            bind(user)
            "${user.firstName} ${user.lastName}"
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

fun Context.heightRecycler(): Int {
    val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    val size = Point()
    display.getSize(size)
    return (size.y / 2.6).toInt()
}