package wee.digital.sample.ui.fragment.chat

import android.view.LayoutInflater
import wee.digital.library.extension.viewModel
import wee.digital.sample.R
import wee.digital.sample.databinding.ChatBinding
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.main.MainFragment
import java.util.*

class ChatFragment : MainFragment<ChatBinding>() {

    private val adapter = ChatAdapter()

    private val vm by viewModel(ChatVM::class)

    override fun inflating(): (LayoutInflater) -> ChatBinding {
        return ChatBinding::inflate
    }

    override fun onViewCreated() {
        vm.queryConversationId(auth.uid.toString())
        adapter.bind(bind.chatRecyclerMessage)
        adapter.onItemClick = { chat, _ ->
            mainVM.chatAdapterSelected = chat
            navigate(R.id.action_global_conversationFragment)
        }
    }

    override fun onLiveDataObserve() {
        vm.listChatStoreSingle.observe {
            Collections.sort(it)
            adapter.set(it)
        }
        vm.userLoginSingle.observe {
            mainVM.userLogin = it
        }
    }

}