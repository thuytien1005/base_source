package wee.digital.widget.extension

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

typealias Void = () -> Unit

fun Void?.does() {
    this?.also { it() }
}

typealias Block<reified T> = T.() -> Unit

fun <T> Block<T>?.doThis(t: T) {
    this?.also { t.it() }
}

fun <T> Block<T>?.doIt(t: T) {
    this?.also { it(t) }
}

interface SimpleOnTabSelectedListener : TabLayout.OnTabSelectedListener {
    override fun onTabSelected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {

    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }
}

interface SimpleDrawerListener : DrawerLayout.DrawerListener {
    override fun onDrawerStateChanged(p0: Int) {
    }

    override fun onDrawerSlide(p0: View, p1: Float) {
    }

    override fun onDrawerClosed(p0: View) {
    }

    override fun onDrawerOpened(p0: View) {
    }
}

interface SimplePageChangeListener : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
    }
}

interface SimpleMotionTransitionListener : MotionLayout.TransitionListener {
    override fun onTransitionChange(layout: MotionLayout?, startId: Int, endId: Int, progress: Float) {
    }

    override fun onTransitionStarted(layout: MotionLayout?, startId: Int, endId: Int) {
    }

    override fun onTransitionCompleted(layout: MotionLayout?, currentId: Int) {
    }

    override fun onTransitionTrigger(layout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
    }
}

interface SimpleTextWatcher : TextWatcher {

    fun EditText.setSilentText(s: String) {
        removeTextChangedListener(this@SimpleTextWatcher)
        setText(s)
        setSelection(s.length)
        addTextChangedListener(this@SimpleTextWatcher)
    }

    override fun afterTextChanged(s: Editable?) {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        textChange(s.toString())
    }

    fun textChange(string: String) {}

}
