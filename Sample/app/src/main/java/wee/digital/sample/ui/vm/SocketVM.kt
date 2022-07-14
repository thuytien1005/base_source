package wee.digital.sample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.log
import wee.digital.sample.ui.base.BaseVM
import java.io.*
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket


class SocketVM : BaseVM() {

    val socketUrlLiveData = MutableLiveData("172.16.16.181:9090")
    val connectionLiveData = MutableLiveData(SocketStatus.closed)
    val sendLiveData = SingleLiveData<String?>()
    val messageLiveData = SingleLiveData<String?>()
    val errorLiveData = MutableLiveData<String?>()

    val isConnected: Boolean get() = socket != null
    private var socket: Socket? = null
    private var printWriter: PrintWriter? = null
    private var dataOutputStream : DataOutputStream?=null
    private var bufferedReader: BufferedReader? = null
    private var mRun: Boolean = false

    fun connect(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            connectionLiveData.postValue(SocketStatus.connecting)
            socket = Socket()
            try {
                val endpoint = url.substringAfter("/").substringBeforeLast(":").replace("/", "")
                val port = url.substringAfterLast(":").toInt()
                val inetAddress: InetAddress = InetAddress.getByName(endpoint)
                socket?.connect(InetSocketAddress(inetAddress, port), 5000)
                /*printWriter = PrintWriter(
                    BufferedWriter(OutputStreamWriter(socket!!.getOutputStream())),
                    true
                )*/
                dataOutputStream = DataOutputStream(socket!!.getOutputStream())
                bufferedReader = BufferedReader(InputStreamReader(socket!!.getInputStream()))
                mRun = true
                connectionLiveData.postValue(SocketStatus.opened)
                while (mRun) {
                    bufferedReader?.readLine()?.also {
                        messageLiveData.postValue(it)
                    }
                    delay(50)
                }

            } catch (e: Exception) {
                errorLiveData.postValue(e.message)
            } finally {
                disconnect()
            }
        }
    }

    fun disconnect() {
        mRun = false
        printWriter = null
        dataOutputStream = null
        bufferedReader = null
        socket?.close()
        socket == null
        connectionLiveData.postValue(SocketStatus.closed)
    }

    fun send(message: String) {
        log.d("socket send String: $message")
    }

    fun send(message: ByteArray) {
        //log.d("socket send ByteArray: ${message.size}")
        sendLiveData.postValue("${message.size}")
        viewModelScope.launch(Dispatchers.IO){
           try {
               dataOutputStream?.write(message)
               dataOutputStream?.flush()
           }catch (e: Exception){

           }
        }
    }

}