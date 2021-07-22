package wee.digital.sample.ui.model

import wee.digital.library.extension.list
import wee.digital.library.extension.str

class StoreChat : ToMap {

    var chatId: String = ""

    var name: String = ""

    var type: String = ""

    var recipients: List<String>? = null

    var messages: List<StoreMessage>? = null

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "chatId" to chatId,
            "name" to name,
            "type" to type,
            "recipients" to recipients,
            "messages" to listMap(messages)
        )
    }

    companion object {

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
}