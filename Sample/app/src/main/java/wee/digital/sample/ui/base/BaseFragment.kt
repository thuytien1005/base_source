package wee.digital.sample.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job
import wee.digital.library.extension.hideKeyboard
import wee.digital.library.util.Logger

abstract class BaseFragment<VB : ViewBinding> : Fragment(),
    FragmentView {

    protected val log: Logger by lazy { Logger(this::class) }

    val vb: VB by viewBinding(inflating())

    abstract fun inflating(): Inflating

    /**
     * [Fragment] override
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = vb.root
        onCreateView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.d("onViewCreated")
        onViewCreated()
        onLiveDataObserve()
    }

    override fun onResume() {
        super.onResume()
        log.d("onResume")
        requireActivity().hideKeyboard()
    }

    override fun onPause() {
        super.onPause()
        log.d("onPause")
    }

    /**
     * [FragmentView] implements
     */
    final override val fragment: Fragment get() = this

    final override val uiJobList: MutableList<Job> = mutableListOf()

    final override val backPressedCallback: OnBackPressedCallback by lazy { getBackPressCallBack() }

    override fun onBackPressed() {
        fragment.requireActivity().onBackPressed()
        backPressedCallback.remove()
    }

}