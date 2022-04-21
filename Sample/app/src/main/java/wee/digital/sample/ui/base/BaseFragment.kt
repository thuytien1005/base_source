package wee.digital.sample.ui.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job
import wee.digital.library.util.Logger

abstract class BaseFragment<VB : ViewBinding> : Fragment(), FragmentView {

    protected val log: Logger by lazy { Logger(this::class) }

    protected val vb: VB by viewBinding(inflating())

    abstract fun inflating(): (LayoutInflater) -> ViewBinding

    /**
     * [Fragment] implements
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher
            .addCallback(this, backPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = vb.root
        view.setOnTouchListener { _, _ -> true }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.focusable = View.FOCUSABLE
            view.isFocusableInTouchMode = false
        }
        view.isFocusable = true
        onCreateView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.d("onViewCreated")
        onViewCreated()
        onLiveDataObserve()
    }

    override fun onDestroyView() {
        log.d("onDestroyView")
        super.onDestroyView()
    }

    override fun onPause() {
        super.onPause()
        uiJobList.forEach { it.cancel(null) }
    }

    /**
     * [FragmentView] implements
     */
    final override val uiJobList: MutableList<Job> = mutableListOf()

    final override val backPressedCallback: OnBackPressedCallback by lazy { getBackPressCallBack() }

    override fun onBackPressed() {

        backPressedCallback.remove()
        activity?.onBackPressed()
    }

}