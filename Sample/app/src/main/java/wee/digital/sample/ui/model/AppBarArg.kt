package wee.digital.sample.ui.model

import android.graphics.Color
import android.view.View

data class AppBarArg(
    var appBarColor: Int = Color.WHITE,
    var mainBackgroundColor: Int = Color.WHITE,
    var statusBarColor: Int = Color.WHITE,
    var navBarColor: Int = Color.WHITE,
    var leftButton1: Int = 0,
    var leftButton1onClick: ((View?) -> Unit)? = null,
    var rightButton1onClick: ((View?) -> Unit)? = null,
    var rightButton2onClick: ((View?) -> Unit)? = null,
    var rightButton1: Int = 0,
    var rightButton2: Int = 0,
    var isVisible: Boolean = true,
    var isGone: Boolean = false,
    var isFullScreen: Boolean = false,
    var logoVisible: Boolean = false,
    var title: String? = null,
    var progress: Int = -1,
    var delay: Long = 0,
    var lightStatusWidgets: Boolean = false
)