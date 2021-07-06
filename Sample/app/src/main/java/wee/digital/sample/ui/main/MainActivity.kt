package wee.digital.sample.ui.main

import android.view.LayoutInflater
import androidx.navigation.NavController
import androidx.navigation.findNavController
import wee.digital.library.extension.viewModel
import wee.digital.sample.R
import wee.digital.sample.databinding.MainBinding
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.main.vm.DialogVM
import wee.digital.sample.ui.main.vm.MainVM

class MainActivity : BaseActivity<MainBinding>(), MainView {

    override val mainActivity: MainActivity? get() = this

    override val mainVM by viewModel(MainVM::class)

    override val dialogVM by viewModel(DialogVM::class)

    override fun navController(): NavController? {
        return findNavController(R.id.fragment)
    }

    override fun inflating(): (LayoutInflater) -> MainBinding {
        return MainBinding::inflate
    }

    override fun onViewCreated() {
    }

    override fun onLiveDataObserve() {

    }

}






