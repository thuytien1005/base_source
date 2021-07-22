package wee.digital.sample.ui.model

import wee.digital.library.extension.list

class StoreContact : ObjectMapper {

    var uids: List<String>? = null
    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "uids" to uids
        )
    }

    companion object {

        fun fromMap(map: Map<String, Any>): StoreContact {
            return StoreContact().also {
                it.uids = map.list("uids")
            }
        }
    }
}