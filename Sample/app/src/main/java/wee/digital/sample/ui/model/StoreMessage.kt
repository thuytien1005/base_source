package wee.digital.sample.ui.model

import com.google.firebase.Timestamp
import wee.digital.library.extension.long
import wee.digital.library.extension.str
import java.util.*

class StoreMessage : ToMap {

    var sender: String = ""

    var time: Long = 0

    var text: String? = null

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "sender" to sender,
            "time" to Timestamp(Date(time)),
            "text" to text
        )
    }

    companion object {

        fun from(m: Map<String, Any>): StoreMessage {
            return StoreMessage().also {
                it.sender = m.str("sender")
                it.time = m.long("time")
                it.text = m.str("text")
            }
        }

    }
}