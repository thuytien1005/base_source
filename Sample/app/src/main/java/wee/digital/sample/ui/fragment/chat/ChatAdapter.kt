package wee.digital.sample.ui.fragment.chat

import android.annotation.SuppressLint
import androidx.viewbinding.ViewBinding
import wee.digital.library.adapter.BaseListAdapter
import wee.digital.library.adapter.ItemInflating
import wee.digital.sample.databinding.ChatItemBinding
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreMessage
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.bind

class ChatAdapter(authId: String) : BaseListAdapter<StoreChat>(StoreChat.itemDiffer) {

    private val myUid: String = authId

    override fun itemInflating(item: StoreChat, position: Int): ItemInflating {
        return ChatItemBinding::inflate
    }

    override fun ViewBinding.onBindItem(item: StoreChat, position: Int) {
        when {
            item.listUserInfo.isNullOrEmpty() -> return
            this is ChatItemBinding -> bindData(item)
        }

    }

    @SuppressLint("SetTextI18n")
    private fun ChatItemBinding.bindData(item: StoreChat) {
        var user = StoreUser()
        when (item.type) {
            "contact" -> {
                user = item.listUserInfo?.first() ?: StoreUser()
                this.itemChatAvatar.bind(user)
                this.itemChatName.text = "${user.firstName} ${user.lastName}"

            }
            "group" -> {
                user = item.listUserInfo?.last() ?: StoreUser()
                this.itemChatAvatar.bind(item.name)
                this.itemChatName.text = item.name
            }
        }
        val messageLast = item.messages?.last() ?: StoreMessage()
        itemChatMessage.text = messageLastType(item.listUserInfo, messageLast)
    }


    private fun messageLastType(userInfo: List<StoreUser>?, message: StoreMessage): String {
        userInfo ?: return ""
        return when {
            message.type == "image" && message.sender != myUid -> {
                val user = userInfo.filter { it.uid == message.sender }.let {
                    if (it.isNullOrEmpty()) StoreUser() else it.first()
                }
                "${user.firstName} ${user.lastName} send a photo"
            }
            message.type == "image" && message.sender == myUid -> "you send a photo"
            else -> message.text.toString()
        }
    }


}