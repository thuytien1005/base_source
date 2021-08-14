package wee.digital.sample.ui.fragment.chat

import android.view.LayoutInflater
import wee.digital.sample.R
import wee.digital.sample.data.firebase.auth
import wee.digital.sample.databinding.ChatBinding
import wee.digital.sample.ui.main.MainFragment
import java.util.*

class ChatFragment : MainFragment<ChatBinding>() {

    private val adapter = ChatAdapter(auth.uid.toString())

    private val vm by lazyViewModel(ChatVM::class)

    override fun inflating(): (LayoutInflater) -> ChatBinding {
        return ChatBinding::inflate
    }

    override fun onViewCreated() {
        vm.queryConversationId(auth.uid.toString())
        adapter.bind(bind.chatRecyclerMessage)
        adapter.onItemClick = { chat, _ ->
            mainVM.chatAdapterSelected = chat
            navigate(R.id.action_global_conversationFragment) {
                setLaunchSingleTop()
            }
        }
    }

    override fun onLiveDataObserve() {
        vm.listChatStoreSingle.observe {
            Collections.sort(it)
            adapter.set(it)
        }
    }

}