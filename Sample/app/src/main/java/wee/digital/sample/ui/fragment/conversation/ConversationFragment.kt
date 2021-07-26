package wee.digital.sample.ui.fragment.conversation

import android.view.LayoutInflater
import android.view.View
import wee.digital.sample.R
import wee.digital.sample.data.repository.auth
import wee.digital.sample.data.repository.userLogin
import wee.digital.sample.databinding.ConversationBinding
import wee.digital.sample.ui.main.MainDialogFragment
import wee.digital.sample.ui.model.StoreMessage
import wee.digital.sample.widget.WidgetChatInput

class ConversationFragment : MainDialogFragment<ConversationBinding>(),
    WidgetChatInput.WidgetChatInputListener {

    private val vm by lazyViewModel(ConversationVM::class)

    private var adapter: ConversationAdapter? = null

    private val toolbar get() = bind.conversationBar

    override fun dialogStyle(): Int {
        return R.style.App_Dialog_FullScreen_Transparent
    }

    override fun inflating(): (LayoutInflater) -> ConversationBinding {
        return ConversationBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener(toolbar.chatToolbarVector)
        configUi()
        bind.conversationWidgetInput.listener = this
        vm.listenerItemChat(mainVM.chatAdapterSelected.chatId)
    }

    private fun configUi() {
        val friend = mainVM.chatAdapterSelected.listUserInfo!!
        adapter = ConversationAdapter(userLogin, friend)
        adapter?.bind(bind.conversationRecyclerMessage)
    }

    override fun onViewClick(v: View?) {
        when (v) {
            toolbar.chatToolbarVector -> navigateUp()
        }
    }

    override fun onLiveDataObserve() {
        vm.chatItemSingle.observe {
            adapter?.set(it.messages)
            bind.conversationRecyclerMessage.smoothScrollToPosition(
                bind.conversationRecyclerMessage.adapter!!.itemCount
            )
        }
        vm.statusInsertChatSingle.observe {
            bind.conversationWidgetInput.text = ""
        }
    }

    /**
     * handler click widget input
     */
    override fun onSendClick(mess: String) {
        when (mess.isEmpty()) {
            true -> ""
            else -> {
                val chatId = mainVM.chatAdapterSelected.chatId
                val messSend = StoreMessage().apply {
                    sender = auth.uid.toString()
                    time = System.currentTimeMillis()
                    text = mess
                    type = null
                }
                vm.insertChat(chatId, messSend)
            }
        }
    }

}