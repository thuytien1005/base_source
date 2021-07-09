package wee.digital.sample.ui.fragment.alert

import android.view.LayoutInflater
import android.widget.TextView
import wee.digital.library.extension.*
import wee.digital.sample.databinding.AlertBinding
import wee.digital.sample.ui.main.MainDialogFragment
import wee.digital.widget.extension.*

class AlertFragment : MainDialogFragment<AlertBinding>() {

    override fun inflating(): (LayoutInflater) -> AlertBinding {
        return AlertBinding::inflate
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
        bind.imageViewIcon.setImageResource(arg.icon)
        bind.imageViewIcon.backgroundTintRes(arg.iconBackgroundTint)
        bind.textViewTitle.text = arg.title
        bind.textViewMessage.setHyperText(arg.message)
        bind.viewAccept.setBackgroundResource(arg.acceptBackground)
        bind.viewAccept.onBindButton(arg.acceptLabel, arg.acceptOnClick)
        bind.viewCancel.onBindButton(arg.cancelLabel, arg.cancelOnClick)
        if (arg.hideCancel) bind.viewCancel.gone()
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