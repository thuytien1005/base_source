package wee.digital.ml.ui

import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import wee.digital.ml.base.GraphicOverlay


interface FaceDetectorInterface {

    fun lifecycleOwner(): LifecycleOwner

    fun cameraProvider(): ProcessCameraProvider

    fun previewView(): PreviewView

    fun graphicOverlay(): GraphicOverlay

}