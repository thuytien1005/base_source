package wee.digital.sample.ui.fragment.conversation

import android.graphics.BitmapFactory
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.adapter.BaseListAdapter
import wee.digital.library.adapter.ItemInflating
import wee.digital.sample.databinding.ConversationImageLeftBinding
import wee.digital.sample.databinding.ConversationImageRightBinding
import wee.digital.sample.databinding.ConversationItemLeftBinding
import wee.digital.sample.databinding.ConversationItemRightBinding
import wee.digital.sample.ui.model.StoreMessage
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.bind
import wee.digital.widget.extension.hide
import wee.digital.widget.extension.load
import wee.digital.widget.extension.show
import java.net.URL


class ConversationAdapter(user: StoreUser, friends: List<StoreUser>) :
    BaseListAdapter<StoreMessage>(StoreMessage.itemDiffer) {

    private val user = user

    private val friends = friends

    private var uidBindLast = ""

    override fun itemInflating(item: StoreMessage, position: Int): ItemInflating {
        return when {
            item.sender != user.uid && item.type == "image" -> ConversationImageLeftBinding::inflate
            item.sender == user.uid && item.type == "image" -> ConversationImageRightBinding::inflate
            item.sender != user.uid && item.type.isNullOrEmpty() -> ConversationItemLeftBinding::inflate
            else -> ConversationItemRightBinding::inflate
        }
    }

    override fun ViewBinding.onBindItem(item: StoreMessage, position: Int) {
        when (this) {
            is ConversationImageLeftBinding -> this.onBindImage(item)
            is ConversationImageRightBinding -> this.onBindImage(item)
            is ConversationItemRightBinding -> this.onBindText(item)
            is ConversationItemLeftBinding -> this.onBindText(item)
        }
        uidBindLast = item.sender
    }

    /**
     * handler ui left
     */
    private fun ConversationItemLeftBinding.onBindText(item: StoreMessage) {
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

    private fun ConversationImageLeftBinding.onBindImage(item: StoreMessage) {
        when (item.sender == uidBindLast) {
            true -> this.conversationImageLeftAvatar.hide()
            else -> {
                val friend = friends.filter { it.uid == item.sender }
                this.conversationImageLeftAvatar.bind(friend.first())
                this.conversationImageLeftAvatar.show()
            }
        }
        this.conversationImageLeft.load(item.text.toString())
    }

    /**
     * handler ui right
     */
    private fun ConversationItemRightBinding.onBindText(item: StoreMessage) {
        this.conversationItemRightMessage.text = item.text.toString()
    }

    private fun ConversationImageRightBinding.onBindImage(item: StoreMessage) {
        this.conversationImageRight.load(item.text.toString())
    }

}