package wee.digital.sample.ui.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job
import wee.digital.library.extension.lightSystemWidgets
import wee.digital.library.extension.statusBarColorRes
import wee.digital.library.util.Logger
import wee.digital.sample.R
import wee.digital.widget.base.DialogLayout
import wee.digital.widget.extension.ViewClickListener


abstract class BaseDialogFragment<VB : ViewBinding> : DialogFragment(),
    FragmentView {

    protected val log: Logger by lazy { Logger(this::class) }

    protected val vb: VB by viewBinding(inflating())

    protected var dismissWhenTouchOutside: Boolean = false

    protected var clickedView: View? = null

    abstract fun inflating(): (LayoutInflater) -> ViewBinding

    /**
     * [DialogFragment] implements
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun getTheme(): Int {
        return R.style.App_Dialog_FullScreen_Transparent
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                this@BaseDialogFragment.onBackPressed()
            }
        }
        dialog.window?.also {
            it.decorView.setBackgroundColor(Color.TRANSPARENT)
            it.attributes.windowAnimations = R.style.App_DialogAnim
            it.statusBarColorRes(R.color.colorDialogBackground)
            it.lightSystemWidgets()
            onWindowConfig(it)
        }
        dialog.setOnDismissListener {
            println("onDismiss")
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        state: Bundle?
    ): View? {
        val view = vb.root
        view.setOnTouchListener { _, _ ->
            if (dismissWhenTouchOutside) {
                dismiss()
            }
            return@setOnTouchListener dismissWhenTouchOutside
        }
        onCreateView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.also {
            it.statusBarColorRes(R.color.colorDialogBackground)
            it.lightSystemWidgets()
        }
        onViewCreated()
        onLiveDataObserve()
    }

    override fun onStart() {
        super.onStart()
        when (theme) {
            R.style.App_Dialog_FullScreen,
            R.style.App_Dialog_FullScreen_Transparent,
            -> dialog?.window?.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        uiJobList.forEach { it.cancel(null) }
    }

    override fun dismissAllowingStateLoss() {
        if (view is DialogLayout) {
            (view as DialogLayout).animateDismiss {
                super.dismissAllowingStateLoss()
            }
        } else {
            super.dismissAllowingStateLoss()
        }
    }

    override fun dismiss() {
        if (view is DialogLayout) {
            (view as DialogLayout).animateDismiss {
                super.dismiss()
            }
        } else {
            super.dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        hideKeyboard()
        super.onDismiss(dialog)
    }

    override fun addClickListener(vararg views: View?) {
        val listener = object : ViewClickListener() {
            override fun onClicks(v: View) {
                clickedView = v
                onViewClick(v)
            }
        }
        views.forEach { it?.setOnClickListener(listener) }
    }

    /**
     * [FragmentView] implements
     */
    final override val uiJobList: MutableList<Job> = mutableListOf()

    final override val backPressedCallback: OnBackPressedCallback by lazy { getBackPressCallBack() }

    override fun onBackPressed() {
        backPressedCallback.remove()
        dismiss()
    }

    /**
     * [BaseDialogFragment] properties
     */
    protected open fun onWindowConfig(window: Window) {
        window.statusBarColorRes(R.color.colorDialogBackground)
        window.lightSystemWidgets()
    }

}