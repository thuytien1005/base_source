package wee.digital.sample.ui.main.fragment.progress

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import wee.digital.sample.databinding.ProgressBinding
import wee.digital.sample.ui.base.BaseAlertDialog

class ProgressDialog : BaseAlertDialog<ProgressBinding>() {

    override fun inflating(): (LayoutInflater, ViewGroup?, Boolean) -> ProgressBinding {
        return ProgressBinding::inflate
    }

    override fun onWindowConfig(window: Window) {
        window.setGravityBottom()
    }
}