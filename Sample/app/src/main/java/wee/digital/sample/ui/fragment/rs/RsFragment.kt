package wee.digital.sample.ui.fragment.rs

import wee.digital.camera.rs.FrameSnapshot
import wee.digital.camera.rs.RsController
import wee.digital.camera.rs.RsStreamer
import wee.digital.sample.databinding.RsBinding
import wee.digital.sample.ui.base.Inflating
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.ui.vm.SocketStatus
import wee.digital.sample.ui.vm.SocketVM
import java.text.SimpleDateFormat
import java.util.*


class RsFragment : MainFragment<RsBinding>(),
    RsStreamer.StreamListener {

    private val socketVM by lazyActivityVM(SocketVM::class)
    private val timeFmt = SimpleDateFormat("hh:mm:ss:SSS")

    override fun inflating(): Inflating = RsBinding::inflate

    override fun onViewCreated() {
        RsController.initContext()
        RsController.startPipeline()
    }


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

    override fun onResume() {
        super.onResume()
        RsController.streamListener = this
    }

    override fun onPause() {
        super.onPause()
        RsController.streamListener = null

    }
    override fun onDestroyView() {
        super.onDestroyView()
        socketVM.disconnect()
        RsController.delayToStopPipeline()
    }

    private var isProcessing: Boolean = false


    /**
     * [RsStreamer.StreamListener] implements
     */
    override fun onRsFrameSnapshot(snapshot: FrameSnapshot) {
       vb.rsSurfaceView.setBitmap(snapshot.colorData.bitmap)
    }


    private fun sendData() {
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




}