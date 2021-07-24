package wee.digital.sample.ui.fragment.chat

import android.view.LayoutInflater
import wee.digital.library.extension.viewModel
import wee.digital.sample.databinding.ChatBinding
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.main.MainFragment

class ChatFragment : MainFragment<ChatBinding>() {

    private val adapter = ChatAdapter()

    private val vm by viewModel(ChatVM::class)

    override fun inflating(): (LayoutInflater) -> ChatBinding {
        return ChatBinding::inflate
    }

    override fun onViewCreated() {
        vm.queryConversationId(auth.uid.toString())
        adapter.bind(bind.chatRecyclerMessage)
    }

    override fun onLiveDataObserve() {
        vm.listChatStoreSingle.observe {
            adapter.set(it)

        }
    }

}