package wee.digital.sample.ui.fragment.conversation

import androidx.viewbinding.ViewBinding
import wee.digital.library.adapter.BaseListAdapter
import wee.digital.library.adapter.ItemInflating
import wee.digital.sample.databinding.ConversationItemLeftBinding
import wee.digital.sample.databinding.ConversationItemRightBinding
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.bind
import wee.digital.widget.extension.hide
import wee.digital.widget.extension.show

class ConversationAdapter(val uid: String) : BaseListAdapter<ItemConversationData>(ItemConversationData.itemDiffer) {

    private val authUid = uid

    private var userBindLast: StoreUser? = null

    override fun itemInflating(item: ItemConversationData, position: Int): ItemInflating {
        return when (item.user.uid == authUid) {
            true -> ConversationItemRightBinding::inflate
            else -> ConversationItemLeftBinding::inflate
        }
    }

    override fun ViewBinding.onBindItem(item: ItemConversationData, position: Int) {
        when (this) {
            is ConversationItemRightBinding -> this.onBindMessage(item)
            is ConversationItemLeftBinding -> this.onBindMessage(item)
        }
        userBindLast = item.user
    }

    private fun ConversationItemLeftBinding.onBindMessage(item: ItemConversationData) {
        when (item.user == userBindLast) {
            true -> this.conversationItemLeftAvatar.hide()
            else -> {
                this.conversationItemLeftAvatar.bind(item.user)
                this.conversationItemLeftAvatar.show()
            }
        }
        this.conversationItemLeftMessage.text = item.message
    }

    private fun ConversationItemRightBinding.onBindMessage(item: ItemConversationData) {
        when (item.user == userBindLast) {
            true -> this.conversationItemRightAvatar.hide()
            else -> {
                this.conversationItemRightAvatar.bind(item.user)
                this.conversationItemRightAvatar.show()
            }
        }
        this.conversationItemRightMessage.text = item.message
    }

}