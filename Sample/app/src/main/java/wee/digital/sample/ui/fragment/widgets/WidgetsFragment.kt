package wee.digital.sample.ui.fragment.widgets

import wee.digital.library.extension.darkSystemWidgets
import wee.digital.library.extension.start
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.databinding.WidgetsBinding
import wee.digital.sample.ui.base.BaseFragment
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.main.MainActivity

class WidgetsFragment : BaseFragment<WidgetsBinding>() {

    override fun inflating(): Inflating = WidgetsBinding::inflate

    override fun onViewCreated() {
        darkSystemWidgets()
        configFragmentView()
        configOtpView()
        configPinView()
        configInputView()
        vb.viewMain.addClickListener {
            start(MainActivity::class)
            baseActivity!!.finish()
        }
    }

    /**
     *
     */
    private fun configFragmentView() {
        vb.viewRs.addClickListener {
            mainNavigate(R.id.action_global_rsFragment)
        }
        vb.viewCam.addClickListener {
            mainNavigate(R.id.action_global_camFragment)
        }
        vb.viewFaceReg.addClickListener {
            mainNavigate(R.id.action_global_faceRegFragment)
        }
        vb.viewFaceMesh.addClickListener {
            mainNavigate(R.id.action_global_faceMeshFragment)
        }
    }

    private fun configOtpView() {
        vb.viewOtpView.addClickListener {
            if (vb.layoutOtpView.isExpanded) {
                vb.layoutOtpView.collapse()
            } else {
                vb.layoutOtpView.expand()
                vb.otpView.clear()
                vb.otpView.requestFocus()
            }
        }
        vb.otpView.onFilled = {
            toast(it)
        }
    }

    private fun configPinView() {
        vb.viewPinView.addClickListener {
            if (vb.layoutPinView.isExpanded) {
                vb.layoutPinView.collapse()
            } else {
                vb.layoutPinView.expand()
                vb.pinView.clear()
                vb.pinView.requestFocus()
            }
        }
        vb.pinView.onFilled = {
            toast(it)
        }
    }

    private fun configInputView() {
        vb.viewInputView.addClickListener { vb.layoutInputView.switch() }
    }

}
