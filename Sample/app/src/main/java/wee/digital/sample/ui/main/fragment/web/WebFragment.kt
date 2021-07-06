package wee.digital.sample.ui.main.fragment.web

import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.web.*
import wee.digital.sample.databinding.WebBinding
import wee.digital.sample.ui.main.MainDialogFragment

class WebFragment : MainDialogFragment<WebBinding>() {

    override fun inflating(): (LayoutInflater) -> WebBinding {
        return WebBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(dialogViewDismiss, dialogView)
    }

    override fun onLiveDataObserve() {
        dialogVM.webLiveData.observe(this::onBindArg)
    }

    override fun onViewClick(v: View?) {
        dismissAllowingStateLoss()
    }

    private fun onBindArg(it: WebArg?) {
        it ?: return
        dialogTextViewTitle.text = it.title
        dialogWebView.loadUrl(it.url)
    }

}

