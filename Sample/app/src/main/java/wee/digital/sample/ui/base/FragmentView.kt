package wee.digital.sample.ui.base

import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

interface FragmentView : BaseView {

    val fragment: Fragment

    override val baseActivity: BaseActivity<*>? get() = fragment.requireActivity() as? BaseActivity<*>

    override val lifecycleOwner: LifecycleOwner get() = fragment.viewLifecycleOwner

    override fun navController(): NavController? {
        return fragment.findNavController()
    }

    override fun add(fragment: Fragment, stack: Boolean) {
        baseActivity?.add(fragment, stack)
    }

    override fun replace(fragment: Fragment, stack: Boolean) {
        baseActivity?.replace(fragment, stack)
    }

    override fun <T : Fragment> remove(cls: Class<T>) {
        baseActivity?.remove(cls)
    }

    fun <T : ViewBinding> viewBinding(block: (LayoutInflater) -> T): Lazy<T> {
        return lazy { block.invoke(fragment.layoutInflater) }
    }

    fun <T : ViewModel> Fragment.lazyActivityVM(cls: KClass<T>): Lazy<T> {
        return lazy { ViewModelProvider(requireActivity()).get(cls.java) }
    }

    fun <T : ViewModel> Fragment.activityVM(cls: KClass<T>): T {
        return ViewModelProvider(requireActivity()).get(cls.java)
    }

    fun post(runnable: Runnable) {
        fragment.view?.post(runnable)
    }

    fun post(delayed: Long, runnable: Runnable) {
        fragment.view?.postDelayed(runnable, delayed)
    }

}