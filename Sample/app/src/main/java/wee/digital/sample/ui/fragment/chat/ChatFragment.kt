package wee.digital.sample.ui.fragment.chat

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.toast
import wee.digital.sample.databinding.ChatBinding
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.main.MainFragment

class ChatFragment : MainFragment<ChatBinding>() {

    private val adapter = ChatAdapter(auth.uid.toString())

    private val toolbar get() = bind.chatBar

    override fun inflating(): (LayoutInflater) -> ChatBinding {
        return ChatBinding::inflate
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
        adapter.set(createChatListId())
        adapter.bind(bind.chatRecyclerMessage)
    }

    private fun handlerClickWidget() {
        bind.chatWidgetInput.onAddClick { toast("add") }
        bind.chatWidgetInput.onPhotoClick { toast("photo") }
        bind.chatWidgetInput.onCameraClick { toast("camera") }
        bind.chatWidgetInput.onMicClick { toast("mic") }
        bind.chatWidgetInput.onEmojiClick { toast("emoji") }
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

    override fun onLiveDataObserve() {}

}