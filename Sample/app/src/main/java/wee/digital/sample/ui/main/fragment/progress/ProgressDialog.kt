package wee.digital.sample.ui.main.fragment.progress

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import wee.digital.sample.R
import wee.digital.sample.databinding.ProgressBinding
import wee.digital.sample.ui.base.BaseAlertDialog
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