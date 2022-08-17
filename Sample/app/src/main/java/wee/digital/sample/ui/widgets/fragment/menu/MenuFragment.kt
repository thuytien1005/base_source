package wee.digital.sample.ui.widgets.fragment.menu

import wee.digital.library.extension.darkSystemWidgets
import wee.digital.library.extension.start
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.databinding.MenuBinding
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.main.MainActivity
import wee.digital.sample.ui.main.MainFragment


class MenuFragment : MainFragment<MenuBinding>() {

    override fun inflating(): Inflating = MenuBinding::inflate

    override fun onViewCreated() {
        darkSystemWidgets()
        configFragmentView()
        configOtpView()
        configPinView()
        configInputView()
        vb.viewMain.addClickListener {
            start(MainActivity::class)
            requireActivity().finish()
        }
    }

    override fun onLiveDataObserve() {

    }


    /**
     *
     */
    private fun configFragmentView() {
        vb.viewRs.addClickListener {
            navigate(R.id.action_global_rsFragment)
        }
        vb.viewCam.addClickListener {
            navigate(R.id.action_global_camFragment)
        }
        vb.viewFaceReg.addClickListener {
            navigate(R.id.action_global_faceRegFragment)
        }
        vb.viewFaceMesh.addClickListener {
            navigate(R.id.action_global_faceMeshFragment)
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