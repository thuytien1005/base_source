package com.example.camera.face

interface FaceDetectorCallback {

    fun detectedHasFace()

    fun detectedNoFace()

    fun detectedHadEligible(faceData: FaceData)

    fun detectedNoEligible()

}
