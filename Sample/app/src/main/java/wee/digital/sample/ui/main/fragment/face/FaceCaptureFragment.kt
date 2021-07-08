package wee.digital.sample.ui.main.fragment.face

import android.graphics.Bitmap
import android.view.LayoutInflater
import wee.dev.camerax.CameraLib
import wee.digital.sample.databinding.FaceCaptureBinding
import wee.digital.sample.ui.main.MainFragment

class FaceCaptureFragment : MainFragment<FaceCaptureBinding>(), CameraLib.CameraListener {

    override fun inflating(): (LayoutInflater) -> FaceCaptureBinding {
        return FaceCaptureBinding::inflate
    }

    override fun onViewCreated() {
        bind.faceCaptureCamera.listener = this
    }

    override fun onLiveDataObserve() {
    }

    override fun onCapture(bm: Bitmap) {

    }

    override fun onResume() {
        super.onResume()
        bind.faceCaptureCamera.resumeCamera()
    }

    override fun onPause() {
        super.onPause()
        bind.faceCaptureCamera.pauseCamera()
    }

}