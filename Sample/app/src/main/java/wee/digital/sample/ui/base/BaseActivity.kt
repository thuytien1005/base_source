package wee.digital.sample.ui.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job
import wee.digital.library.util.Logger

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity(),
    BaseView {

    protected val log: Logger by lazy { Logger(this::class) }

    protected val vb: VB by viewBinding(inflating())

    abstract fun inflating(): (LayoutInflater) -> ViewBinding

    /**
     * [AppCompatActivity] implements
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(vb.root)
        onViewCreated()
        onLiveDataObserve()
    }

    override fun onPause() {
        super.onPause()
        uiJobList.forEach { it.cancel(null) }
    }

    /**
     * [BaseView] implements
     */
    final override val baseActivity: BaseActivity<*>? get() = this

    final override val lifecycleOwner: LifecycleOwner get() = this

    override val uiJobList: MutableList<Job> = mutableListOf()

    /**
     * [BaseActivity] properties
     */
    protected open fun fragmentContainerId(): Int {
        throw NullPointerException("fragmentContainerId no has implement")
    }

    protected fun <T : ViewBinding> viewBinding(block: (LayoutInflater) -> ViewBinding): Lazy<T> {
        return lazy {
            @Suppress("UNCHECKED_CAST")
            block.invoke(layoutInflater) as T
        }
    }

    /**
     * SoftInputMode
     */
    fun inputModeAdjustResize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            vb.root.setOnApplyWindowInsetsListener { _, windowInsets ->
                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                vb.root.setPadding(0, 0, 0, imeHeight)
                windowInsets
            }
        } else {
            @Suppress("DEPRECATION")
            baseActivity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    fun inputModeAdjustNothing() {
        baseActivity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

}