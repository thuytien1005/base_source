package wee.digital.sample.ui.model

import com.google.firebase.Timestamp
import wee.digital.library.extension.long
import wee.digital.library.extension.str
import java.util.*

class StoreMessage : ObjectMapper {

    var sender: String = ""

    var time: Long = 0

    var text: String? = null

    var type: String? = null

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "sender" to sender,
            "time" to Timestamp(Date(time)),
            "text" to text,
            "type" to type,
        )
    }

    companion object {

        fun from(m: Map<String, Any>): StoreMessage {
            return StoreMessage().also {
                it.sender = m.str("sender")
                it.time = m.long("time")
                it.text = m.str("text")
                it.type = m.str("type")
            }
        }

    }
}