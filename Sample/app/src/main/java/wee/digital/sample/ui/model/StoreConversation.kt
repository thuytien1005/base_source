package wee.digital.sample.ui.model

import wee.digital.library.extension.list

class StoreConversation : ObjectMapper {

    var chatIds: List<String>? = null

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "chatIds" to chatIds
        )
    }

    companion object {

        fun fromMap(map: Map<String, Any>): StoreConversation {
            return StoreConversation().also {
                it.chatIds = map.list("chatIds")
            }
        }
    }
}