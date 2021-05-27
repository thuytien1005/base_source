package wee.digital.ml.face

import androidx.camera.core.Preview
import wee.digital.ml.camera.cameraOptions

/**
 * -------------------------------------------------------------------------------------------------
 * @Project: Sample
 * @Created: Huy 2021/05/27
 * @Organize: Wee Digital
 * @Description: ...
 * All Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
class FaceCapturePreview(private val view: FaceCapture.ViewInterface) {

    private val useCase: Preview = cameraOptions.preview

    fun bindUseCase() {
        view.cameraProvider.unbind(useCase)
        useCase?.previewSurfaceProvider = view.previewView.previewSurfaceProvider
        view.cameraProvider.bindToLifecycle(view.lifecycleOwner, cameraOptions.selector, useCase)
    }

    fun unBindUseCase() {
        view.cameraProvider.unbind(useCase)
    }

}