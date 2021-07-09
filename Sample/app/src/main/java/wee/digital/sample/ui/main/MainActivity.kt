package wee.digital.sample.ui.main

import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.navigation.NavController
import androidx.navigation.findNavController
import wee.digital.library.extension.isGranted
import wee.digital.library.extension.viewModel
import wee.digital.sample.R
import wee.digital.sample.app
import wee.digital.sample.databinding.MainBinding
import wee.digital.sample.shared.progressLiveData
import wee.digital.sample.ui.base.BaseActivity
import wee.digital.sample.ui.fragment.progress.ProgressDialog
import wee.digital.sample.ui.vm.DialogVM
import wee.digital.sample.ui.vm.FirebaseVM

class MainActivity : BaseActivity<MainBinding>(), MainView {

    private val mainVM by viewModel(MainVM::class)

    private val dialogVM by viewModel(DialogVM::class)

    private val firebaseVM by viewModel(FirebaseVM::class)

    private var progressDialog: ProgressDialog? = null

    override fun navController(): NavController? {
        return findNavController(R.id.fragment)
    }

    override fun inflating(): (LayoutInflater) -> MainBinding {
        return MainBinding::inflate
    }

    override fun onViewCreated() {
        firebaseVM.onFirebaseAppInit(app)
    }

    override fun onLiveDataObserve() {
        progressLiveData.observe {
            when (it) {
                true -> {
                    progressDialog?.dismiss()
                    progressDialog = ProgressDialog()
                    progressDialog?.show(supportFragmentManager, "progress")
                }
                else -> {
                    progressDialog?.dismiss()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isGranted(android.Manifest.permission.CAMERA)
    }

    fun setBackground(@ColorInt color: Int){
        bind.layoutContent.setBackgroundColor(color)
    }
}






