package wee.digital.sample.ui.fragment.chat

import androidx.viewbinding.ViewBinding
import wee.digital.library.adapter.BaseListAdapter
import wee.digital.library.adapter.ItemInflating
import wee.digital.sample.databinding.ChatItemBinding
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.utils.bind

class ChatAdapter : BaseListAdapter<StoreChat>(StoreChat.itemDiffer) {

    override fun itemInflating(item: StoreChat, position: Int): ItemInflating {
        return ChatItemBinding::inflate
    }

    override fun ViewBinding.onBindItem(item: StoreChat, position: Int) {
        when {
            item.listUserInfo.isNullOrEmpty() -> return
            this is ChatItemBinding -> bindData(item)
        }

    }

    private fun ChatItemBinding.bindData(item: StoreChat) {
        when (item.type) {
            "contact" -> {
                val user = item.listUserInfo?.first()!!
                this.itemChatAvatar.bind(user)
                this.itemChatName.text = "${user.firstName} ${user.lastName}"
                this.itemChatMessage.text = item.messages?.last()?.text.toString()
            }
            "group" -> {
                this.itemChatAvatar.bind(item.name)
                this.itemChatName.text = item.name
                this.itemChatMessage.text = item.messages?.last()?.text.toString()
            }
        }
    }

}