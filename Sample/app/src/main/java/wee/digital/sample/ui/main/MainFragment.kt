package wee.digital.sample.ui.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.transition.Transition
import androidx.viewbinding.ViewBinding
import com.google.android.material.transition.MaterialContainerTransform
import wee.digital.sample.R
import wee.digital.sample.data.api.httpErrorLiveData
import wee.digital.sample.data.api.networkErrorLiveData
import wee.digital.sample.data.api.progressLiveData
import wee.digital.sample.ui.base.BaseFragment
import wee.digital.sample.ui.dialog.DialogVM
import wee.digital.widget.extension.SimpleTransitionListener

abstract class MainFragment<B : ViewBinding> : BaseFragment<B>(), MainFragmentView {

    override val mainActivity get() = requireActivity() as? MainActivity
    override val mainVM by lazyActivityVM(MainVM::class)
    override val dialogVM by lazyActivityVM(DialogVM::class)

    open fun backgroundColor(): Int = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressLiveData.value = false
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            duration = resources.getInteger(R.integer.fragment_enter_duration).toLong()
            addListener(object : SimpleTransitionListener {
                override fun onTransitionStart(transition: Transition) {
                    onEnterTransitionStarted()
                }
            })
        }
        sharedElementReturnTransition = MaterialContainerTransform().apply {
            duration = resources.getInteger(R.integer.fragment_enter_duration).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = super<BaseFragment>.onCreateView(inflater, container, savedInstanceState)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        networkErrorLiveData.observe {
            showNetworkError(it)
        }
        httpErrorLiveData.observe {
            showHttpError(it)
        }
        progressLiveData.observe {
            if (it == true) showProgress() else hideProgress()
        }
    }


    open fun onEnterTransitionStarted() = Unit
}