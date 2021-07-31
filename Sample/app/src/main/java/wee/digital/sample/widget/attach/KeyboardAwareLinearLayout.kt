package wee.digital.sample.widget.attach

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.Surface
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.widget.LinearLayoutCompat
import wee.digital.sample.R

open class KeyboardAwareLinearLayout : LinearLayoutCompat {

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var rect = Rect()
    private var hiddenListeners = HashSet<OnKeyboardHiddenListener>()
    private var shownListeners = HashSet<OnKeyboardShownListener>()
    private var displayMetrics = DisplayMetrics()

    private val minKeyboardSize = resources.getDimensionPixelSize(R.dimen.min_keyboard_size)
    private val minCustomKeyboardSize =
        resources.getDimensionPixelSize(R.dimen.min_custom_keyboard_size)
    private val defaultCustomKeyboardSize =
        resources.getDimensionPixelSize(R.dimen.default_custom_keyboard_size)
    private val minCustomKeyboardTopMarginPortrait =
        resources.getDimensionPixelSize(R.dimen.min_custom_keyboard_top_margin_portrait)
    private val minCustomKeyboardTopMarginLandscape =
        resources.getDimensionPixelSize(R.dimen.min_custom_keyboard_top_margin_portrait)
    private val minCustomKeyboardTopMarginLandscapeBubble =
        resources.getDimensionPixelSize(R.dimen.min_custom_keyboard_top_margin_landscape_bubble)
    private val statusBarHeight get() = statusBarHeight()
    private var viewInset = getViewInset()

    private var keyboardOpen = false
    private var rotation = -1
    private var isBubble: Boolean = false


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        updateRotation()
        updateKeyboardState()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Build.VERSION.SDK_INT >= 23 && rootWindowInsets != null) {
            val windowInsets = rootWindowInsets
            var bottomInset = if (Build.VERSION.SDK_INT >= 30) {
                windowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom
            } else {
                windowInsets.stableInsetBottom
            }
            if (bottomInset != 0 && (viewInset == 0 || viewInset == statusBarHeight)) {
                viewInset = bottomInset
            }
        }
    }

    private fun updateRotation() {
        val oldRotation = rotation
        rotation = getDeviceRotation()
        if (oldRotation != rotation) {
            onKeyboardClose()
        }
    }

    private fun updateKeyboardState() {
        getWindowVisibleDisplayFrame(rect)
        val availableHeight = getAvailableHeight()
        val keyboardHeight = availableHeight - rect.bottom
        if (keyboardHeight > minKeyboardSize) {
            if (getKeyboardHeight() != keyboardHeight) {
                if (isLandscape()) {
                    setKeyboardLandscapeHeight(keyboardHeight)
                } else {
                    setKeyboardPortraitHeight(keyboardHeight)
                }
            }
            if (!keyboardOpen) {
                onKeyboardOpen()
            }
        } else if (keyboardOpen) {
            onKeyboardClose()
        }
    }

    private fun setKeyboardPortraitHeight(height: Int) {
        if (isBubble) return
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putInt("keyboard_height_portrait", height).apply()
    }

    private fun setKeyboardLandscapeHeight(height: Int) {
        if (isBubble) return
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putInt("keyboard_height_landscape", height).apply()
    }

    private fun getKeyboardLandscapeHeight(): Int {
        if (isBubble) {
            return rootView.height - minCustomKeyboardTopMarginLandscapeBubble
        }
        val keyboardHeight = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt("keyboard_height_landscape", defaultCustomKeyboardSize)
        return clamp(
            keyboardHeight,
            minCustomKeyboardSize,
            rootView.height - minCustomKeyboardTopMarginLandscape
        )
    }

    private fun getKeyboardPortraitHeight(): Int {
        if (isBubble) {
            val height = rootView.height
            return height - (height * 0.45).toInt()
        }
        val keyboardHeight = PreferenceManager.getDefaultSharedPreferences(context)
            .getInt("keyboard_height_portrait", defaultCustomKeyboardSize)
        return clamp(
            keyboardHeight,
            minCustomKeyboardSize,
            rootView.height - minCustomKeyboardTopMarginPortrait
        )
    }

    private fun getAvailableHeight(): Int {
        val availableHeight = rootView.height - viewInset
        val availableWidth = rootView.width
        if (isLandscape() && availableHeight > availableWidth) {
            return availableWidth
        }
        return availableHeight
    }

    private fun isLandscape(): Boolean {
        val rotation = getDeviceRotation()
        return rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270
    }

    private fun getDeviceRotation(): Int {
        if (Build.VERSION.SDK_INT >= 30) {
            display.getRealMetrics(displayMetrics)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context.windowManager().defaultDisplay.getRealMetrics(displayMetrics)
            }
        }
        return when (displayMetrics.widthPixels > displayMetrics.heightPixels) {
            true -> Surface.ROTATION_90
            else -> Surface.ROTATION_0
        }
    }

    private fun getViewInset(): Int {
        try {
            val attachInfoFiled = View::class.java.getDeclaredField("mAttachInfo")
            attachInfoFiled.isAccessible = true
            val attachInfo = attachInfoFiled.get(this)
            if (attachInfo != null) {
                val stableInsetsFiled = attachInfo.javaClass.getDeclaredField("mStableInsets")
                stableInsetsFiled.isAccessible = true
                val insets = stableInsetsFiled.get(attachInfo) as Rect?
                return insets?.bottom ?: statusBarHeight
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return statusBarHeight
    }

    private fun notifyHiddenListeners() {
        val listener = HashSet(hiddenListeners)
        for (model in listener) {
            model.onKeyboardHidden()
        }
    }

    private fun notifyShownListeners() {
        val listener = HashSet(shownListeners)
        for (model in listener) {
            model.onKeyboardShown()
        }
    }

    fun getKeyboardHeight(): Int {
        return when (isLandscape()) {
            true -> getKeyboardLandscapeHeight()
            else -> getKeyboardPortraitHeight()
        }
    }

    fun onKeyboardClose() {
        keyboardOpen = false
        notifyHiddenListeners()
    }

    fun onKeyboardOpen() {
        keyboardOpen = true
        notifyShownListeners()
    }

    fun isKeyboardOpen(): Boolean {
        return keyboardOpen
    }

    fun setIsBubble(bool: Boolean) {
        isBubble = bool
    }

    fun postOnKeyboardClose(block: () -> Unit) {
        if (keyboardOpen) {
            val l = object : OnKeyboardHiddenListener {
                override fun onKeyboardHidden() {
                    removeOnKeyboardHiddenListener(this)
                    block()
                }
            }
            addOnKeyboardHiddenListener(l)
        } else {
            block()
        }
    }

    fun postOnKeyboardOpen(block: () -> Unit) {
        if (!keyboardOpen) {
            val l = object : OnKeyboardShownListener {
                override fun onKeyboardShown() {
                    removeOnKeyboardShownListener(this)
                }
            }
            addOnKeyboardShownListener(l)
        } else {
            block()
        }
    }

    fun addOnKeyboardHiddenListener(listener: OnKeyboardHiddenListener) {
        hiddenListeners.add(listener)
    }

    fun removeOnKeyboardHiddenListener(listener: OnKeyboardHiddenListener) {
        hiddenListeners.remove(listener)
    }

    fun addOnKeyboardShownListener(listener: OnKeyboardShownListener) {
        shownListeners.add(listener)
    }

    fun removeOnKeyboardShownListener(listener: OnKeyboardShownListener) {
        shownListeners.remove(listener)
    }

    interface OnKeyboardHiddenListener {
        fun onKeyboardHidden()
    }

    interface OnKeyboardShownListener {
        fun onKeyboardShown()
    }

}