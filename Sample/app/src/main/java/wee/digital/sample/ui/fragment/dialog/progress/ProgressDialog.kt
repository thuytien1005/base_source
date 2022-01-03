package wee.digital.sample.ui.fragment.dialog.progress

import android.view.LayoutInflater
import wee.digital.sample.databinding.ProgressBinding
import wee.digital.sample.ui.base.BaseDialogFragment

class ProgressDialog : BaseDialogFragment<ProgressBinding>() {

    override fun inflating(): (LayoutInflater) -> ProgressBinding {
        return ProgressBinding::inflate
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
    }

}