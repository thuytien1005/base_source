package wee.digital.sample.ui.fragment.cam

import android.annotation.SuppressLint
import android.media.Image
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import wee.digital.camera.*
import com.quickbirdstudios.yuv2mat.UnsupportedImageFormatException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.onGrantedPermission
import wee.digital.library.extension.toast
import wee.digital.sample.databinding.CamBinding
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.ui.vm.SocketStatus
import wee.digital.sample.ui.vm.SocketVM
import wee.digital.sample.ui.vm.WebSocketVM
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws


class CamFragment : MainFragment<CamBinding>(),
    CameraController.Interface {

    private lateinit var cameraController: CameraController
    private val socketVM by lazyActivityVM(SocketVM::class)

    override fun inflating(): Inflating = CamBinding::inflate

    override fun onViewCreated() {
        vb.viewSend.addClickListener {
            socketVM.send("hello world")
        }
        cameraController = CameraController(this)
        onGrantedPermission(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            onGranted = {
                cameraController.start(lifecycleOwner)
            }, onDenied = {
                toast("camera permission denied")
            })
    }

    private val timeFmt = SimpleDateFormat("hh:mm:ss:SSS")
    override fun onLiveDataObserve() {
        socketVM.socketUrlLiveData.observe {
            vb.editTextUrl.setText(it)
            launch(100) {
                vb.editTextUrl.setSelection(it.length)
            }
        }
        socketVM.sendLiveData.observe {
            vb.textViewTimeSend.text = timeFmt.format(Date())
            vb.textViewMessageSend.text = it
        }
        socketVM.messageLiveData.observe {
            vb.textViewTimeReceive.text = timeFmt.format(Date())
            vb.textViewMessageReceive.text = it
        }
        socketVM.errorLiveData.observe {
            vb.textViewError.text = it
        }
        socketVM.connectionLiveData.observe {
            vb.textViewStatus.text = it
            when (it) {
                SocketStatus.connecting -> {
                    vb.viewConnect.setOnClickListener(null)
                }
                SocketStatus.opened -> {
                    vb.viewConnect.text = "disconnect"
                    vb.viewConnect.addClickListener {
                        socketVM.disconnect()
                    }
                }
                SocketStatus.closed -> {
                    vb.viewConnect.text = "connect"
                    vb.viewConnect.addClickListener {
                        val s = vb.editTextUrl.text?.toString()
                        if (!s.isNullOrEmpty()) {
                            socketVM.connect(s)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        socketVM.disconnect()
    }

    /**
     * [CameraController.Interface]
     */
    override fun cameraPreviewView(): PreviewView {
        return vb.previewView
    }


    private var isProcessing: Boolean = false


    override fun onImageAnalysis(imageProxy: ImageProxy) {
        if (isProcessing || !socketVM.isConnected) {
            imageProxy.close()
            return
        }
        isProcessing = true
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                //showImage(imageProxy)
                sendData(imageProxy)

                delay(1000)
                isProcessing = false
                imageProxy.close()

            } catch (e: Exception) {
                log.d(e.message)
                imageProxy.close()
            }
        }

    }

    @SuppressLint("UnsafeOptInUsageError")
    @Throws(UnsupportedImageFormatException::class)
    private fun sendData(imageProxy: ImageProxy) {

        val image: Image = imageProxy.image ?: throw NullPointerException()


        val imageData: ByteArray = byteArrayOf()


        log.d("data --------------------------------------------------")

        val sizeData: ByteArray = numberToByteArray(imageData.size.toUInt())

        log.d("send SIZE_DATA: ${sizeData.toHexString()}")
        log.d("send IMAGE_DATA length: ${imageData.size}")
        log.d("\n")

        socketVM.send(sizeData + imageData)
    }

    private fun numberToByteArray(data: UInt, size: Int = 4): ByteArray {
        return ByteArray(size) { i -> (data.toLong() shr (i * 8)).toByte() }
    }

    private fun ByteArray.toHexString(
        separator: CharSequence = " ",
        prefix: CharSequence = "[",
        postfix: CharSequence = "]"
    ): String {
        return this.joinToString(separator, prefix, postfix) {
            "0x%02X".format(it)
        }
    }


    private fun showImage(imageProxy: ImageProxy) {
        try {
           /* @SuppressLint("UnsafeOptInUsageError")
            val image: Image = imageProxy.image ?: throw NullPointerException()
            val bitmap = image.yuv420toBitmap().rotate(imageProxy.imageInfo.rotationDegrees)
            vb.imageViewSend.setImageBitmap(bitmap)*/
        } catch (e: Exception) {
            log.d(e.message)
        }
    }


}