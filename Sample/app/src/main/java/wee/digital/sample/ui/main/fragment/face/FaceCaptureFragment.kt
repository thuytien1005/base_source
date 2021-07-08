package wee.digital.sample.ui.main.fragment.face

import android.util.Base64
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.toast
import wee.digital.library.extension.viewModel
import wee.digital.sample.R
import wee.digital.sample.databinding.FaceCaptureBinding
import wee.digital.sample.ui.main.MainFragment
import wee.digital.widget.extension.hide
import wee.digital.widget.extension.show

class FaceCaptureFragment : MainFragment<FaceCaptureBinding>() {

    private val vm by viewModel(FaceVM::class)

    override fun inflating(): (LayoutInflater) -> FaceCaptureBinding {
        return FaceCaptureBinding::inflate
    }

    override fun onViewCreated() {
        bind.faceCapture.setOnClickListener {
            bind.faceCaptureCamera.getImageCapture { captureSuccess(it) }
        }
    }

    override fun onLiveDataObserve() {
        vm.addUserStatusEvent.observe {
            if (it.isEmpty()) {
                navigate(R.id.homeFragment){setLaunchSingleTop()}
            } else {
                toast(it)
                bind.faceCapture.show()
                bind.faceCaptureProgress.hide()
                bind.faceCaptureCamera.resetCamera()
            }
        }
    }

    private fun captureSuccess(byte: ByteArray?) {
        MainScope().launch {
            byte ?: return@launch
            bind.faceCapture.hide()
            bind.faceCaptureProgress.show()
            val user = mainVM.userInfo.apply {
                face = Base64.encodeToString(byte, Base64.NO_WRAP)
            }
            vm.addUser(user)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            delay(500)
            bind.faceCaptureCamera.resumeCamera()
        }
    }

    override fun onPause() {
        super.onPause()
        bind.faceCaptureCamera.pauseCamera()
    }

}