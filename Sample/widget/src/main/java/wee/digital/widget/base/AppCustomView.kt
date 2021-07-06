package wee.digital.widget.base

import android.content.Context
import android.content.res.TypedArray
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.viewbinding.ViewBinding
import wee.digital.widget.R

abstract class AppCustomView<B : ViewBinding> : ConstraintLayout {

    protected abstract fun onInitialize(context: Context, types: TypedArray)

    protected open fun styleResource(): IntArray {
        return R.styleable.AppCustomView
    }

    protected val bind: B by lazy {
        inflating().invoke(LayoutInflater.from(context), this, true)
    }

    abstract fun inflating(): (LayoutInflater, ViewGroup, Boolean) -> B

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        onViewInit(context, attrs)
    }

    private fun onViewInit(context: Context, attrs: AttributeSet?) {
        val types = context.theme.obtainStyledAttributes(attrs, styleResource(), 0, 0)
        try {
            bind.root
            onInitialize(context, types)
        } finally {
            types.recycle()
        }
    }

    fun anim(@AnimRes res: Int): Animation {
        return AnimationUtils.loadAnimation(context, res)
    }

    fun drawable(@DrawableRes res: Int): Drawable {
        return ContextCompat.getDrawable(context, res)!!
    }

    fun createDrawable(@DrawableRes res: Int): Drawable? {
        return drawable(res).constantState?.newDrawable()?.mutate()
    }

    fun Drawable?.tint(@ColorInt color: Int): Drawable? {
        this ?: return null
        DrawableCompat.setTint(this, color)
        DrawableCompat.setTintMode(this, PorterDuff.Mode.SRC_IN)
        return this
    }

    fun Drawable?.tintRes(@ColorRes color: Int): Drawable? {
        return tint(ContextCompat.getColor(context, color))
    }

    fun color(@ColorRes res: Int): Int {
        return ContextCompat.getColor(context, res)
    }

    fun string(@StringRes res: Int): String {
        return context.getString(res)
    }

    fun View.backgroundTint(@ColorInt color: Int) {
        post {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                background?.colorFilter = BlendModeColorFilter(color, BlendMode.SRC_ATOP)
            } else {
                @Suppress("DEPRECATION")
                background?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            }
        }
    }

    fun View.backgroundTintRes(@ColorRes colorRes: Int) {
        backgroundTint(color(colorRes))
    }

    /**
     * Text
     */
    val TypedArray.text: String?
        get() = getString(R.styleable.AppCustomView_android_text)

    val TypedArray.title: String?
        get() = getString(R.styleable.AppCustomView_android_title)

    val TypedArray.hint: String?
        get() = getString(R.styleable.AppCustomView_android_hint)

    val TypedArray.clickable: Boolean
        get() = getBoolean(R.styleable.AppCustomView_android_clickable, true)

    /**
     * Input type
     */
    val TypedArray.maxLength: Int
        get() = getInt(R.styleable.AppCustomView_android_maxLength, 256)

    val TypedArray.maxLines: Int
        get() = getInt(R.styleable.AppCustomView_android_maxLines, 1)

    val TypedArray.textAllCaps: Boolean
        get() = getBoolean(R.styleable.AppCustomView_android_textAllCaps, false)

    /**
     * Color
     */
    val TypedArray.tint: Int
        get() {
            return getColor(R.styleable.AppCustomView_android_tint, color(R.color.colorPrimary))
        }

    val TypedArray.drawableTint: Int
        get() {
            return getColor(R.styleable.AppCustomView_android_drawableTint, Color.BLACK)
        }

    val TypedArray.backgroundTint: Int
        get() {
            return getColor(R.styleable.AppCustomView_android_backgroundTint, color(R.color.colorTransparent))
        }

    val TypedArray.textColor: Int
        get() {
            return getColor(R.styleable.AppCustomView_android_textColor, Color.BLACK)
        }

    val TypedArray.textColorHint: Int
        get() {
            return getColor(R.styleable.AppCustomView_android_textColorHint, Color.BLACK)
        }

    val TypedArray.hintColor: Int
        get() {
            return getColor(R.styleable.AppCustomView_android_textColor, Color.DKGRAY)
        }

    /**
     * Drawable
     */
    val TypedArray.drawableStart: Drawable?
        get() {
            return getDrawable(R.styleable.AppCustomView_android_drawableStart)
                    ?.constantState?.newDrawable()?.mutate()
        }

    val TypedArray.drawableEnd: Drawable?
        get() {
            return getDrawable(R.styleable.AppCustomView_android_drawableEnd)
                    ?.constantState?.newDrawable()?.mutate()
        }

    val TypedArray.drawable: Drawable?
        get() {
            return getDrawable(R.styleable.AppCustomView_android_drawable)
                    ?.constantState?.newDrawable()?.mutate()
        }

    val TypedArray.src: Drawable?
        get() {
            return getDrawable(R.styleable.AppCustomView_android_src)
                    ?.constantState?.newDrawable()?.mutate()
        }

    val TypedArray.srcRes: Int
        get() {
            return getResourceId(R.styleable.AppCustomView_android_src, 0)
        }

    val TypedArray.background: Int
        get() {
            return getResourceId(R.styleable.AppCustomView_android_background, 0)
        }

    /**
     * Checkable
     */
    val TypedArray.checkable: Boolean
        get() = getBoolean(R.styleable.AppCustomView_android_checkable, false)

    val TypedArray.checked: Boolean
        get() = getBoolean(R.styleable.AppCustomView_android_checked, false)

    /**
     * Padding
     */
    val TypedArray.paddingStart: Int
        get() = getDimensionPixelSize(R.styleable.AppCustomView_android_paddingStart, 0)

    val TypedArray.paddingEnd: Int
        get() = getDimensionPixelSize(R.styleable.AppCustomView_android_paddingEnd, 0)

    val TypedArray.paddingTop: Int
        get() = getDimensionPixelSize(R.styleable.AppCustomView_android_paddingTop, 0)

    val TypedArray.paddingBottom: Int
        get() = getDimensionPixelSize(R.styleable.AppCustomView_android_paddingBottom, 0)

    fun TypedArray.pixels(@StyleableRes id: Int) {
        getDimensionPixelSize(id, 0).toFloat()
    }

    /**
     * Selectors
     */
    val TypedArray.enabled: Boolean
        get() = getBoolean(R.styleable.AppCustomView_android_enabled, true)

}



