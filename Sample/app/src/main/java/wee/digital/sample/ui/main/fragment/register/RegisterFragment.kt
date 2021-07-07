package wee.digital.sample.ui.main.fragment.register

import android.view.LayoutInflater
import wee.digital.library.extension.viewModel
import wee.digital.sample.databinding.RegisterBinding
import wee.digital.sample.ui.main.MainFragment

class RegisterFragment : MainFragment<RegisterBinding>() {

    private val vm by viewModel(RegisterVM::class)

    override fun inflating(): (LayoutInflater) -> RegisterBinding {
        return RegisterBinding::inflate
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {
    }
}