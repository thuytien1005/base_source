package wee.digital.sample.ui.main.fragment.register

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.viewModel
import wee.digital.sample.MainDirections
import wee.digital.sample.R
import wee.digital.sample.databinding.RegisterBinding
import wee.digital.sample.repository.model.RegisterData
import wee.digital.sample.ui.main.MainFragment
import wee.digital.widget.extension.string

class RegisterFragment : MainFragment<RegisterBinding>() {

    private val vm by viewModel(RegisterVM::class)

    override fun inflating(): (LayoutInflater) -> RegisterBinding {
        return RegisterBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.viewRegister)
    }

    override fun onLiveDataObserve() {
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.viewRegister -> validData()
        }
    }

    private fun validData() {
        val email = bind.inputViewEmail.text.toString()
        val pass = bind.inputViewPassword.text.toString()
        if (email.isEmpty()) {
            bind.inputViewEmail.error = string(R.string.register_invalid_email)
            return
        }
        if (pass.isEmpty()) {
            bind.inputViewPassword.error = string(R.string.register_invalid_pass)
            return
        }
        mainVM.registerData = RegisterData(email, pass)
        navigate(MainDirections.actionGlobalFaceCaptureFragment())
    }

}