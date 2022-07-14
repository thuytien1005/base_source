package wee.digital.camera.rs

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.intel.realsense.librealsense.Option
import com.intel.realsense.librealsense.Sensor
import wee.digital.camera.app

/**
 *
 */
class RsDisplayInfo {

    class RsOption(val option: Option, val default: Float = 0f) {
        var min: Float = 0f
        var max: Float = 0f
        var value: Float = default
        var isReadOnly: Boolean = false
        var isSupport: Boolean = true
        var step: Float = 0f

        fun sync(sensor: Sensor?) {
            sensor ?: return
            val opt = option
            kotlin.runCatching {
                min = sensor.getMinRange(opt)
                max = sensor.getMaxRange(opt)
                value = sensor.getValue(opt)
                step = sensor.getStep(opt)
                isReadOnly = sensor.isReadOnly(opt)
                isSupport = sensor.supports(opt)
            }
            Log.d("rsOption", "${opt.name} value: $value - min: $min - max: $max")
        }

        fun apply(sensor: Sensor?, value: Float) {
            sensor ?: return
            val opt = option
            kotlin.runCatching {
                if (!sensor.supports(opt) || sensor.isReadOnly(opt)) {
                    return
                }
                val min = sensor.getMinRange(opt)
                val max = sensor.getMaxRange(opt)
                if (sensor.getValue(opt) != value && value in min..max) {
                    Log.d("rsOption", "apply ${opt.name} $value")
                    sensor.setValue(opt, value)
                }
            }
        }

        fun apply(sensor: Sensor?) {
            apply(sensor, value)
        }

        fun default(sensor: Sensor?) {
            apply(sensor, default)
        }

    }

    companion object {

        private val pref: SharedPreferences by lazy {
            app.getSharedPreferences("wee.digital.rs", Context.MODE_PRIVATE)
        }

        var hasDefault: Boolean
            get() = pref.getBoolean("rsDefault", false)
            set(value) {
                val edit = pref.edit()
                edit.putBoolean("rsDefault", value)
                edit.apply()
            }

        val liveData = MutableLiveData(
            mutableMapOf(
                rsEntry(Option.BACKLIGHT_COMPENSATION, 2f),
                rsEntry(Option.BRIGHTNESS, 0f),
                rsEntry(Option.CONTRAST, 50f),
                rsEntry(Option.EXPOSURE, 2500f),
                rsEntry(Option.GAIN, 80f),
                rsEntry(Option.GAMMA, 250f),
                rsEntry(Option.HUE, 0f),
                rsEntry(Option.SATURATION, 50f),
                rsEntry(Option.SHARPNESS, 50f),
                rsEntry(Option.WHITE_BALANCE, 5700f),
                rsEntry(Option.ENABLE_AUTO_EXPOSURE, 1f),
                rsEntry(Option.ENABLE_AUTO_WHITE_BALANCE, 1f),
            )
        )

        private fun rsEntry(options: Option, default: Float = 0f): Pair<Int, RsOption> {
            return options.value() to RsOption(options, default)
        }

        fun sync(sensor: Sensor?) {
            sensor ?: return
            val current = liveData.value
            current?.entries?.forEach {
                it.value.sync(sensor)
            }
            liveData.postValue(current)

        }

        fun apply(sensor: Sensor?) {
            sensor ?: return
            val current = liveData.value
            current?.entries?.forEach {
                it.value.apply(sensor)
            }
            sync(sensor)
        }

        fun default(sensor: Sensor) {
            liveData.value?.entries?.forEach {
                it.value.default(sensor)
            }
            sync(sensor)
        }
    }

}