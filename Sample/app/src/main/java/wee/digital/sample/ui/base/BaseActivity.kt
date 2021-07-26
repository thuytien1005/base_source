package wee.digital.sample.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity(),
        BaseView {

    protected val bind: B by viewBinding(inflating())

    abstract fun inflating(): (LayoutInflater) -> B

    /**
     * [AppCompatActivity] implements
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)
        onViewCreated()
        onLiveDataObserve()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        /*if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }*/
        return super.dispatchTouchEvent(event)
    }

    /**
     * [BaseView] implements
     */
    final override val baseActivity: BaseActivity<*>? get() = this

    final override val lifecycleOwner: LifecycleOwner get() = this

    /**
     * [BaseActivity] properties
     */
    protected open fun fragmentContainerId(): Int {
        throw NullPointerException("fragmentContainerId no has implement")
    }

    protected fun <T : ViewBinding> viewBinding(block: (LayoutInflater) -> T): Lazy<T> {
        return lazy { block.invoke(layoutInflater) }
    }

}