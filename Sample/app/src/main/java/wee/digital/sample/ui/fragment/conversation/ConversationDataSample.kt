package wee.digital.sample.ui.fragment.conversation

import wee.digital.sample.shared.auth
import wee.digital.sample.ui.model.StoreUser

class ItemConversationData {

    var user: StoreUser = StoreUser()

    var message: String = ""

    constructor()

    constructor(user: StoreUser, message: String) {
        this.user = user
        this.message = message
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