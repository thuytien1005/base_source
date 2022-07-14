package wee.digital.sample.ui.base

import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.viewbinding.ViewBinding

interface FragmentView : BaseView {

    val fragment: Fragment

    /**
     * [BaseView] implements
     */
    override val baseActivity: BaseActivity<*>? get() = fragment.requireActivity() as? BaseActivity<*>

    override val lifecycleOwner: LifecycleOwner get() = fragment

    override fun activityNavController(): NavController? {
        return baseActivity?.activityNavController()
    }

    fun onCreateView() = Unit

    /**
     * [FragmentView] utils
     */
    fun <T : ViewBinding> viewBinding(block: Inflating): Lazy<T> {
        return lazy {
            @Suppress("UNCHECKED_CAST")
            block.invoke(fragment.layoutInflater) as T
        }
    }

    fun requestFocus(v: View?) {
        launch(240) { v?.requestFocus() }
    }

    /**
     * Back press handle
     */
    val backPressedCallback: OnBackPressedCallback

    fun getBackPressCallBack(): OnBackPressedCallback {
        return object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressed()
            }
        }
    }

    fun onBackPressed()

    /**
     * LifecycleScope
     */
    val lifecycleScope get() = fragment.lifecycleScope

    fun addObserver(observer: LifecycleObserver) {
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    /**
     * Navigation
     */
    fun childNavigate(@IdRes actionId: Int, block: (NavBuilder.() -> Unit)? = null) {
        fragment.findNavController().navigate(actionId, block)
    }

    fun childPopBackStack(@IdRes fragmentId: Int = 0, inclusive: Boolean = false) {
        if (fragmentId != 0) {
            fragment.findNavController().popBackStack(fragmentId, inclusive)
        } else {
            fragment.findNavController().popBackStack()
        }
    }

    fun mainNavigate(@IdRes actionId: Int, block: (NavBuilder.() -> Unit)? = null) {
        val nav = activityNavController()
        nav?.navigate(actionId, block)
    }

    fun mainNavigateNoAnim(@IdRes actionId: Int, block: (NavBuilder.() -> Unit)? = null) {
        val nav = activityNavController()
        nav?.navigateNoAnim(actionId, block)
    }

    fun mainPopBackStack(@IdRes fragmentId: Int = 0, inclusive: Boolean = false) {
        val nav = activityNavController()
        if (fragmentId != 0) {
            nav?.popBackStack(fragmentId, inclusive)
        } else {
            nav?.popBackStack()
        }
    }

    /**
     * Keyboard utils
     */
    fun inputModeAdjustResize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            fragment.view?.setOnApplyWindowInsetsListener { _, windowInsets ->
                val imeHeight = windowInsets.getInsets(WindowInsets.Type.ime()).bottom
                fragment.view?.setPadding(0, 0, 0, imeHeight)
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

    fun hideKeyboard() {
        fragment.view?.hideKeyboard()
    }

    fun showKeyboard() {
        fragment.view?.showKeyboard()
    }

    fun transition(id: Int): Transition {
        return TransitionInflater.from(fragment.requireContext())
            .inflateTransition(android.R.transition.move)
    }
}