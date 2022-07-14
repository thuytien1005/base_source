package wee.digital.camera.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.SeekParams
import wee.digital.camera.databinding.RsSeekbarBinding
import wee.digital.camera.rs.RsDisplayInfo

class RsSeekBar : ConstraintLayout {

    protected val vb: RsSeekbarBinding

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        vb = RsSeekbarBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun bindRsOptions(rsOption: RsDisplayInfo.RsOption, onValueChanged: (Float) -> Unit) {

        val supportText = if (rsOption.isSupport) "" else "(not support)"

        vb.seekBar.onSeekChangeListener = null

        val option = rsOption.option
        vb.textViewTitle.text = "${option.name} $supportText"

        val color = if (!rsOption.isReadOnly && rsOption.isSupport) {
            Color.parseColor("#42A5F5")
        } else {
            Color.parseColor("#FF434343")
        }
        vb.seekBar.thumbColor(color)

        val min = rsOption.min
        if (min != vb.seekBar.min) {
            vb.textViewMin.text = min.toString()
            vb.seekBar.min = min
        }

        val max = rsOption.max
        if (max != vb.seekBar.max) {
            vb.textViewMax.text = max.toString()
            vb.seekBar.max = max
        }

        val progress = rsOption.value
        if (progress != vb.seekBar.progressFloat) {
            vb.seekBar.setProgress(progress)
        }


        val tickCount = (rsOption.max - rsOption.min / rsOption.step).toInt()
        if (tickCount in 1..49 && tickCount != vb.seekBar.tickCount) {
            vb.seekBar.tickCount = tickCount + 1
        }
        vb.seekBar.onSeekChangeListener = object : com.warkiz.widget.OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams) = Unit

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) = Unit

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                val value = seekBar.progressFloat
                onValueChanged(value)
            }
        }
    }

    fun bindValue(value: Float) {
        kotlin.runCatching { vb.seekBar.setProgress(value) }
    }
}