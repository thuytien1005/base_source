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
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.Job
import wee.digital.library.extension.hideSystemUI
import wee.digital.library.util.Logger
import wee.digital.sample.R

abstract class BaseDialog<VB : ViewBinding> : DialogFragment(),
    FragmentView {

    override val fragment: Fragment get() = this

    protected val log: Logger by lazy { Logger(this::class) }

    protected val vb: VB by viewBinding(inflating())

    protected var dismissWhenTouchOutside: Boolean = false

    protected var clickedView: View? = null

    abstract fun inflating(): Inflating

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
                this@BaseDialog.onBackPressed()
            }
        }
        dialog.window?.also {
            it.decorView.setBackgroundColor(Color.TRANSPARENT)
            it.attributes.windowAnimations = R.style.App_DialogAnim
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
        savedInstanceState: Bundle?
    ): View? {
        val view = vb.root
        view.setOnTouchListener { _, _ ->
            if (dismissWhenTouchOutside) dismiss()
            return@setOnTouchListener dismissWhenTouchOutside
        }
        onCreateView()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        log.d("onViewCreated")
        hideSystemUI()
        onViewCreated()
        onLiveDataObserve()
    }

    override fun onPause() {
        super.onPause()
        uiJobList.forEach { it.cancel(null) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log.d("onDestroyView")
        onDismiss?.invoke()
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

    override fun onDismiss(dialog: DialogInterface) {
        hideKeyboard()
        super.onDismiss(dialog)
    }

    override fun dismiss() {
        super.dismissAllowingStateLoss()
    }

    private var onDismiss: (() -> Unit)? = null

    fun dismiss(block: (() -> Unit)?) {
        onDismiss = block
        dismiss()
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
    protected open fun onWindowConfig(window: Window) = Unit

}