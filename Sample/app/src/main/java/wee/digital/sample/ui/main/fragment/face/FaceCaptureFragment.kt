package wee.digital.sample.ui.main.fragment.face

import android.view.LayoutInflater
import wee.dev.camerax.Detection
import wee.dev.camerax.FacePointData
import wee.digital.sample.databinding.FaceCaptureBinding
import wee.digital.sample.ui.main.MainFragment

class FaceCaptureFragment : MainFragment<FaceCaptureBinding>(), Detection.DetectionCallBack {

    override fun inflating(): (LayoutInflater) -> FaceCaptureBinding {
        return FaceCaptureBinding::inflate
    }

    override fun onViewCreated() {
        bind.faceCaptureCamera.createCamera(this)
    }

    override fun onLiveDataObserve() {
    }

    /**
     * camera result
     */
    override fun faceEligible(bm: ByteArray, faceData: FacePointData) {
        print("")
    }

    override fun onResume() {
        super.onResume()
        bind.faceCaptureCamera.resumeCamera()
    }

    override fun onPause() {
        super.onPause()
        bind.faceCaptureCamera.pauseCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        bind.faceCaptureCamera.destroyCamera()
    }

}