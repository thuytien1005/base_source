package wee.digital.sample.ui.main.fragment.register

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.viewModel
import wee.digital.sample.R
import wee.digital.sample.databinding.RegisterBinding
import wee.digital.sample.repository.model.RegisterData
import wee.digital.sample.ui.main.MainFragment

class RegisterFragment : MainFragment<RegisterBinding>() {

    private val vm by viewModel(RegisterVM::class)

    override fun inflating(): (LayoutInflater) -> RegisterBinding {
        return RegisterBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(bind.viewRegister)
    }

    override fun onLiveDataObserve() {
        vm.errorNameEvent.observe { errorName(it) }
        vm.errorEmailEvent.observe { errorEmail(it) }
        vm.errorPasswordEvent.observe { errorPassword(it) }
        vm.successInputEvent.observe { navigateFace() }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            bind.viewRegister -> {
                vm.checkInput(
                    bind.inputViewName.text,
                    bind.inputViewEmail.text,
                    bind.inputViewPassword.text
                )
            }
        }
    }

    private fun errorName(it: String?) {
        bind.inputViewName.error = it
    }

    private fun errorEmail(it: String?) {
        bind.inputViewEmail.error = it
    }

    private fun errorPassword(it: String?) {
        bind.inputViewPassword.error = it
    }

    private fun navigateFace() {
        mainVM.userInfo = RegisterData().apply {
            name = bind.inputViewName.text.toString()
            email = bind.inputViewEmail.text.toString()
            password = bind.inputViewPassword.text.toString()
        }
        navigate(R.id.action_global_faceCaptureFragment)
    }

}