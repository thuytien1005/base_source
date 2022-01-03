package wee.digital.sample.ui.base

import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.hideKeyboard
import wee.digital.library.extension.showKeyboard
import kotlin.reflect.KClass


interface FragmentView : BaseView {

    val fragment: Fragment get() = this as Fragment

    override val baseActivity: BaseActivity<*>? get() = fragment.activity as? BaseActivity<*>

    override val lifecycleOwner: LifecycleOwner get() = fragment.viewLifecycleOwner

    override fun activityNavController(): NavController? {
        return baseActivity?.activityNavController()
    }

    fun <T : ViewBinding> viewBinding(block: (LayoutInflater) -> ViewBinding): Lazy<T> {
        return lazy {
            @Suppress("UNCHECKED_CAST")
            block.invoke(fragment.layoutInflater) as T
        }
    }

    fun <T : ViewModel> lazyActivityVM(cls: KClass<T>): Lazy<T> {
        return lazy { ViewModelProvider(fragment.requireActivity()).get(cls.java) }
    }

    fun <T : ViewModel> activityVM(cls: KClass<T>): T {
        return ViewModelProvider(fragment.requireActivity()).get(cls.java)
    }

    fun onCreateView() = Unit

    fun requestFocus(v: View?) {
        launch(1000) { v?.requestFocus() }
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
    fun childNavigate(@IdRes actionId: Int, block: (NavigationBuilder.() -> Unit)? = null) {
        fragment.findNavController().navigate(actionId, block)
    }

    fun childPopBackStack(@IdRes fragmentId: Int = 0, inclusive: Boolean = false) {
        if (fragmentId != 0) {
            fragment.findNavController().popBackStack(fragmentId, inclusive)
        } else {
            fragment.findNavController().navigateUp()
        }
    }

    fun mainNavigate(@IdRes actionId: Int, block: (NavigationBuilder.() -> Unit)? = null) {
        activityNavController()?.navigate(actionId, block)
    }

    fun mainPopBackStack(@IdRes fragmentId: Int = 0, inclusive: Boolean = false) {
        if (fragmentId != 0) {
            activityNavController()?.popBackStack(fragmentId, inclusive)
        } else {
            activityNavController()?.navigateUp()
        }
    }

    /**
     * Keyboard
     */
    fun hideKeyboard() {
        lifecycleScope.launch {
            delay(200)
            fragment.requireActivity().hideKeyboard()
        }
    }

    fun showKeyboard() {
        lifecycleScope.launch {
            delay(200)
            fragment.requireActivity().showKeyboard()
        }
    }
}