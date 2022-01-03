package com.example.camera.ui

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.camera.R

class FaceScanLayout : ConstraintLayout {

    private var path: Path? = null

    /** corner radius */
    var radius: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var backgroundColor: Int? = null
        set(@ColorInt value) {
            field = value
            postInvalidate()
        }

    var strokeLineWidth: Float = 0F
        set(value) {
            field = value
            postInvalidate()
        }

    var strokeLineColor = 0XFFFFFFFF.toInt()
        set(@ColorInt value) {
            field = value
            postInvalidate()
        }

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        render(attrs)
    }

    private fun render(attrs: AttributeSet?) {
        attrs ?: return
        val a = context.obtainStyledAttributes(attrs, R.styleable.FaceScanLayout)
        radius = a.pixels(R.styleable.FaceScanLayout_faceScan_radius)
        backgroundColor =
            a.getColor(R.styleable.FaceScanLayout_faceScan_backgroundColor, Color.WHITE)
        strokeLineWidth = a.pixels(R.styleable.FaceScanLayout_faceScan_strokeLineWidth)
        strokeLineColor =
            a.getColor(R.styleable.FaceScanLayout_faceScan_strokeLineColor, Color.BLACK)
        a.recycle()
    }

    fun TypedArray.pixels(@StyleableRes id: Int): Float {
        return getDimensionPixelSize(id, 0).toFloat()
    }

    override fun dispatchDraw(canvas: Canvas) {
        /** for outline remake when ever draw */
        path = Path()
        val radii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        clipPathCanvas(canvas, radii)
        /** set drawable resource corner & background & stroke */
        GradientDrawable().apply {
            cornerRadii = radii
            if (strokeLineWidth != 0F) {
                this.setStroke(strokeLineWidth.toInt(), strokeLineColor, 0F, 0F)
            }
            backgroundColor?.let { setColor(it) } ?: setColor(Color.WHITE)
            background = this
        }
        outlineProvider = outlineProvider
        clipChildren = false
        super.dispatchDraw(canvas)
    }

    private fun clipPathCanvas(canvas: Canvas, floatArray: FloatArray) {
        path?.let {
            it.addRoundRect(
                RectF(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat()),
                floatArray,
                Path.Direction.CW
            )
            canvas.clipPath(it)
        }
    }

    override fun setBackgroundColor(color: Int) {
        backgroundColor = color
    }

    /** For not showing red underline */
    override fun setOutlineProvider(provider: ViewOutlineProvider?) {
        super.setOutlineProvider(provider)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutlineProvider(): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                path?.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        outline.setPath(it)
                    } else {
                        @Suppress("DEPRECATION")
                        outline.setConvexPath(it)
                    }
                } ?: throw Exception()
            }
        }
    }

}