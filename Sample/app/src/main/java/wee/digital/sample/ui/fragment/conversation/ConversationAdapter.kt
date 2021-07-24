package wee.digital.sample.ui.fragment.conversation

import androidx.viewbinding.ViewBinding
import wee.digital.library.adapter.BaseListAdapter
import wee.digital.library.adapter.ItemInflating
import wee.digital.sample.databinding.ConversationItemLeftBinding
import wee.digital.sample.databinding.ConversationItemRightBinding
import wee.digital.sample.ui.model.StoreMessage
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.bind
import wee.digital.widget.extension.hide
import wee.digital.widget.extension.show

class ConversationAdapter(user: StoreUser, friends: List<StoreUser>) :
    BaseListAdapter<StoreMessage>(StoreMessage.itemDiffer) {

    private val user = user

    private val friends = friends

    private var uidBindLast = ""

    override fun itemInflating(item: StoreMessage, position: Int): ItemInflating {
        return when (item.sender == user.uid) {
            true -> ConversationItemRightBinding::inflate
            else -> ConversationItemLeftBinding::inflate
        }
    }

    override fun ViewBinding.onBindItem(item: StoreMessage, position: Int) {
        when (this) {
            is ConversationItemRightBinding -> this.onBindMessage(item)
            is ConversationItemLeftBinding -> this.onBindMessage(item)
        }
        uidBindLast = item.sender
    }

    private fun ConversationItemLeftBinding.onBindMessage(item: StoreMessage) {
        when (item.sender == uidBindLast) {
            true -> this.conversationItemLeftAvatar.hide()
            else -> {
                val friend = friends.filter { it.uid == item.sender }
                this.conversationItemLeftAvatar.bind(friend.first())
                this.conversationItemLeftAvatar.show()
            }
        }
        this.conversationItemLeftMessage.text = item.text.toString()
    }

    private fun ConversationItemRightBinding.onBindMessage(item: StoreMessage) {
        when (item.sender == uidBindLast) {
            true -> this.conversationItemRightAvatar.hide()
            else -> {
                this.conversationItemRightAvatar.bind(user)
                this.conversationItemRightAvatar.show()
            }
        }
        this.conversationItemRightMessage.text = item.text.toString()
    }

}