package wee.digital.sample.ui.fragment.conversation

import androidx.recyclerview.widget.DiffUtil
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreUser

class ItemConversationData {

    var user: StoreUser = StoreUser()

    var message: String = ""

    constructor()

    constructor(user: StoreUser, message: String) {
        this.user = user
        this.message = message
    }

    companion object{

        val itemDiffer
            get() = object : DiffUtil.ItemCallback<ItemConversationData>() {
                override fun areItemsTheSame(oldItem: ItemConversationData, newItem: ItemConversationData): Boolean {
                    return oldItem.user === newItem.user
                }

                override fun areContentsTheSame(oldItem: ItemConversationData, newItem: ItemConversationData): Boolean {
                    return oldItem.user == newItem.user
                }

            }

    }

}

fun createConversationList(): List<ItemConversationData> {

    return listOf(
        ItemConversationData(
            user = StoreUser().apply {
                uid = auth.uid.toString()
                firstName = "Bao"
                lastName = "Bao"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = auth.uid.toString()
                firstName = "Bao"
                lastName = "Bao"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = auth.uid.toString()
                firstName = "Bao"
                lastName = "Bao"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = "uidtestchatexamplenjadn"
                firstName = "Nguyen"
                lastName = "Map"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = "uidtestchatexamplenjadn"
                firstName = "Nguyen"
                lastName = "Map"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = "uidtestchatexamplenjadn"
                firstName = "Nguyen"
                lastName = "Map"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = "uidtestchatexamplenjadn"
                firstName = "Nguyen"
                lastName = "Map"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = auth.uid.toString()
                firstName = "Bao"
                lastName = "Bao"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = "uidtestchatexamplenjadn"
                firstName = "Nguyen"
                lastName = "Map"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = auth.uid.toString()
                firstName = "Bao"
                lastName = "Bao"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = auth.uid.toString()
                firstName = "Bao"
                lastName = "Bao"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = "uidtestchatexamplenjadn"
                firstName = "Nguyen"
                lastName = "Map"
            },
            message = "dat la test 1"
        ),
        ItemConversationData(
            user = StoreUser().apply {
                uid = "uidtestchatexamplenjadn"
                firstName = "Nguyen"
                lastName = "Map"
            },
            message = "dat la test 1"
        )
    )

}