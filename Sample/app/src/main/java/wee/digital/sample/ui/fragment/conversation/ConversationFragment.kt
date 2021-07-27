package wee.digital.sample.ui.fragment.conversation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.hideKeyboard
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated() {
        addClickListener(toolbar.chatToolbarVector)
        bind.conversationWidgetInput.listener = this
        bind.conversationRecyclerMessage.setOnTouchListener { v, event ->
            hideKeyboard()
            false
        }
        when (mainVM.chatAdapterSelected == null) {
            true -> ""
            else -> {
                val friend = mainVM.chatAdapterSelected!!.listUserInfo!!
                adapter = ConversationAdapter(userLogin, friend)
                adapter?.bind(bind.conversationRecyclerMessage)
                vm.listenerItemChat(mainVM.chatAdapterSelected!!.chatId)
            }
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            toolbar.chatToolbarVector -> dismiss()
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
        if (mess.isEmpty()) return

        when (mainVM.chatAdapterSelected == null) {
            true -> ""
            else -> {
                val chatId = mainVM.chatAdapterSelected!!.chatId
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