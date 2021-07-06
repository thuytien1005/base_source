package wee.digital.sample.ui.main.fragment.alert

import android.view.LayoutInflater
import android.widget.TextView
import kotlinx.android.synthetic.main.alert.*
import wee.digital.library.extension.*
import wee.digital.sample.databinding.HomeBinding
import wee.digital.sample.ui.main.MainDialogFragment
import wee.digital.widget.extension.backgroundTintRes
import wee.digital.widget.extension.gone
import wee.digital.widget.extension.isGone
import wee.digital.widget.extension.setHyperText

class AlertFragment : MainDialogFragment<HomeBinding>() {

    override fun inflating(): (LayoutInflater) -> HomeBinding {
        return HomeBinding::inflate
    }

    override fun onViewCreated() {

    }

    override fun onLiveDataObserve() {
        dialogVM.alertLiveData.observe(this::onBindArg)
    }

    /**
     * [AlertFragment] properties
     */
    private fun onBindArg(arg: AlertArg?) {
        arg ?: return
        alertImageViewIcon.setImageResource(arg.icon)
        alertImageViewIcon.backgroundTintRes(arg.iconBackgroundTint)
        alertTextViewTitle.text = arg.title
        alertTextViewMessage.setHyperText(arg.message)
        alertViewAccept.setBackgroundResource(arg.acceptBackground)
        alertViewAccept.onBindButton(arg.acceptLabel, arg.acceptOnClick)
        alertViewCancel.onBindButton(arg.cancelLabel, arg.cancelOnClick)
        if (arg.hideCancel) alertViewCancel.gone()
    }

    private fun TextView.onBindButton(label: String?, onClick: (AlertFragment) -> Unit) {
        this.isGone(label.isNullOrEmpty())
        this.text = label
        this.addViewClickListener {
            dismissAllowingStateLoss()
            onClick(this@AlertFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialogVM.alertLiveData.value?.onDismiss?.also {
            it()
        }
    }

}