package wee.digital.sample.ui.fragment.conversation

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.toast
import wee.digital.sample.databinding.ConversationBinding
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.main.MainFragment

class ConversationFragment : MainFragment<ConversationBinding>() {

    private val adapter = ConversationAdapter(auth.uid.toString())

    private val toolbar get() = bind.conversationBar

    override fun inflating(): (LayoutInflater) -> ConversationBinding {
        return ConversationBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(
            toolbar.chatToolbarVector,
            toolbar.chatToolbarAvatar,
            toolbar.chatToolbarVideo,
            toolbar.chatToolbarCall,
            toolbar.chatToolbarMenu
        )
        handlerClickWidget()
        adapter.set(createConversationList())
        adapter.bind(bind.conversationRecyclerMessage)
    }

    private fun handlerClickWidget() {
        bind.conversationWidgetInput.onAddClick { toast("add") }
        bind.conversationWidgetInput.onPhotoClick { toast("photo") }
        bind.conversationWidgetInput.onCameraClick { toast("camera") }
        bind.conversationWidgetInput.onMicClick { toast("mic") }
        bind.conversationWidgetInput.onEmojiClick { toast("emoji") }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            toolbar.chatToolbarVector -> toast("vector")
            toolbar.chatToolbarAvatar -> toast("avatar")
            toolbar.chatToolbarVideo -> toast("video")
            toolbar.chatToolbarCall -> toast("call")
            toolbar.chatToolbarMenu -> toast("menu")
        }
    }

    override fun onLiveDataObserve() {
    }

}