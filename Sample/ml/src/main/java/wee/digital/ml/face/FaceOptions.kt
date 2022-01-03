package wee.digital.ml.face

import com.google.mlkit.vision.face.FaceDetectorOptions

val faceOptions by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    FaceOptions()
}

class FaceOptions {

    // High-accuracy landmark detection and face classification
    var detectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
}