package wee.digital.sample.ui.model

import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.firestore.Exclude
import wee.digital.library.extension.list
import wee.digital.library.extension.str

class StoreChat : ObjectMapper, Comparable<StoreChat> {

    var chatId: String = ""

    var name: String = ""

    var type: String = ""

    var recipients: List<String>? = null

    var messages: List<StoreMessage>? = null

    @Exclude
    var listUserInfo: List<StoreUser>? = null

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "chatId" to chatId,
            "name" to name,
            "type" to type,
            "recipients" to recipients,
            "messages" to messages.toMapList()
        )
    }

    companion object {

        val itemDiffer
            get() = object : DiffUtil.ItemCallback<StoreChat>() {
                override fun areItemsTheSame(oldItem: StoreChat, newItem: StoreChat): Boolean {
                    return oldItem.chatId === newItem.chatId
                }

                override fun areContentsTheSame(oldItem: StoreChat, newItem: StoreChat): Boolean {
                    return oldItem.messages == newItem.messages
                }

            }

        fun fromMap(map: Map<String, Any>): StoreChat {
            return StoreChat().also {
                it.chatId = map.str("chatId")
                it.name = map.str("name")
                it.type = map.str("type")
                it.recipients = map.list("recipients")
                it.messages = map.list("messages", StoreMessage::from)
            }
        }
    }

    override fun compareTo(other: StoreChat): Int {
        return when {
            messages?.last()?.time ?: 0 > other.messages?.last()?.time ?: 0 -> -1
            messages?.last()?.time ?: 0 < other.messages?.last()?.time ?: 0 -> 1
            else -> 0
        }
    }
}