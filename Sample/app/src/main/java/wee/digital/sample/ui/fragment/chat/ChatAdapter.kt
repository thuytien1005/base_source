package wee.digital.sample.ui.fragment.chat

import androidx.viewbinding.ViewBinding
import wee.digital.library.adapter.BaseBindRecyclerAdapter
import wee.digital.library.adapter.ItemInflating
import wee.digital.sample.databinding.ChatItemLeftBinding
import wee.digital.sample.databinding.ChatItemRightBinding
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.bind
import wee.digital.widget.extension.hide
import wee.digital.widget.extension.show

class ChatAdapter(val uid: String) : BaseBindRecyclerAdapter<ItemChatData>() {

    private val authUid = uid

    private var userBindLast: StoreUser? = null

    override fun itemInflating(item: ItemChatData, position: Int): ItemInflating {
        return when (item.user.uid == authUid) {
            true -> ChatItemRightBinding::inflate
            else -> ChatItemLeftBinding::inflate
        }
    }

    override fun ViewBinding.onBindItem(item: ItemChatData, position: Int) {
        when (this) {
            is ChatItemRightBinding -> this.onBindMessage(item)
            is ChatItemLeftBinding -> this.onBindMessage(item)
        }
        userBindLast = item.user
    }

    private fun ChatItemLeftBinding.onBindMessage(item: ItemChatData) {
        when (item.user == userBindLast) {
            true -> this.chatItemLeftAvatar.hide()
            else -> {
                this.chatItemLeftAvatar.bind(item.user)
                this.chatItemLeftAvatar.show()
            }
        }
        this.chatItemLeftMessage.text = item.message
    }

    private fun ChatItemRightBinding.onBindMessage(item: ItemChatData) {
        when (item.user == userBindLast) {
            true -> this.chatItemRightAvatar.hide()
            else -> {
                this.chatItemRightAvatar.bind(item.user)
                this.chatItemRightAvatar.show()
            }
        }
        this.chatItemRightMessage.text = item.message
    }

}