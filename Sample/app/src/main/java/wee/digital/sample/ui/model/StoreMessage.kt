package wee.digital.sample.ui.model

import androidx.recyclerview.widget.DiffUtil
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

    fun messageLastType(userLogin: StoreUser): String {
        return when {
            type == "image" && sender == userLogin.uid -> "you send a photo"
            type == "image" && sender != userLogin.uid -> "${userLogin.firstName} ${userLogin.lastName} send a photo"
            else -> text.toString()
        }
    }

    companion object {

        val itemDiffer
            get() = object : DiffUtil.ItemCallback<StoreMessage>() {
                override fun areItemsTheSame(
                    oldItem: StoreMessage,
                    newItem: StoreMessage
                ): Boolean {
                    return oldItem.time === newItem.time
                }

                override fun areContentsTheSame(
                    oldItem: StoreMessage,
                    newItem: StoreMessage
                ): Boolean {
                    return oldItem.sender == newItem.sender
                }

            }

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