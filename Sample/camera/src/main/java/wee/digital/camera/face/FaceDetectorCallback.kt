package wee.digital.camera.face

import kotlinx.coroutines.CoroutineScope

interface FaceDetectorCallback {
    fun detectorLifecycleScope(): CoroutineScope
    fun detectorNoFace() = Unit
    fun detectorHasFace() = Unit
    fun detectorFaceInvalid(s: String) = Unit
    fun detectorEligibleFace(faceData: FaceData) = Unit
}
