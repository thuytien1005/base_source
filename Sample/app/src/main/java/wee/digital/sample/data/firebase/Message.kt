package wee.digital.sample.data.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import wee.digital.library.extension.toast

class FcmService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Firebase.d("onMessageReceived")
        val sb = StringBuilder()
        remoteMessage.notification?.also {
            sb.append("${it.body}")
        }
        remoteMessage.data.forEach {
            sb.append("\n${it.key}: ${it.value}")
        }
        toast(sb.toString())
    }

    override fun onNewToken(token: String) {
        fcmToken = token
        Firebase.d("NewToken: $token")
    }

}