package wee.digital.camera.util

import android.content.Context
import org.opencv.android.BaseLoaderCallback
import org.opencv.android.InstallCallbackInterface
import org.opencv.android.LoaderCallbackInterface
import org.opencv.android.OpenCVLoader


object OpenCV {

    private var openCVInitialized : Boolean = false

    private var openCVInitializing : Boolean = false

    fun initLoader(context: Context) {
        if (openCVInitialized || openCVInitializing) return
        openCVInitializing = true
        val loaderCallback = object : BaseLoaderCallback(context) {
            override fun onManagerConnected(status: Int) {
                openCVInitializing = false
                when (status) {
                    LoaderCallbackInterface.SUCCESS -> {
                        openCVInitialized = true
                    }
                    else -> {
                        openCVInitialized = false
                        super.onManagerConnected(status)
                    }
                }
            }

            override fun onPackageInstall(operation: Int, callback: InstallCallbackInterface?) {
                super.onPackageInstall(operation, callback)
                openCVInitializing = false
            }
        }

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, context, loaderCallback)
        } else {
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS)
        }
    }

}