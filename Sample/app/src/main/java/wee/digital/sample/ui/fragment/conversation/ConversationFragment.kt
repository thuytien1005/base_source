package wee.digital.sample.ui.fragment.conversation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.data.repository.auth
import wee.digital.sample.data.repository.userLogin
import wee.digital.sample.databinding.ConversationBinding
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.ui.model.Media
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreMessage
import wee.digital.sample.utils.bind
import wee.digital.sample.widget.WidgetChatInput

class ConversationFragment : MainFragment<ConversationBinding>(),
    WidgetChatInput.WidgetChatInputListener {

    private val vm by lazyViewModel(ConversationVM::class)

    private var adapter: ConversationAdapter? = null

    private val toolbar get() = bind.conversationBar

    private var chatInfo
        get() = mainVM.chatAdapterSelected
        set(value) {
            mainVM.chatAdapterSelected = value
        }

    private var contactInfo
        get() = mainVM.contactAdapterSelected
        set(value) {
            mainVM.contactAdapterSelected = value
        }

    override fun inflating(): (LayoutInflater) -> ConversationBinding {
        return ConversationBinding::inflate
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated() {
        addClickListener(toolbar.chatToolbarVector)
        bindInfoToolbar()
        bindDataMessage()
        bind.conversationWidgetInput.setupStubGallery(requireActivity())
        bind.conversationWidgetInput.listener = this
    }

    override fun onViewClick(v: View?) {
        when (v) {
            toolbar.chatToolbarVector -> navigate(R.id.action_global_homeFragment) { setLaunchSingleTop() }
        }
    }

    override fun onLiveDataObserve() {
        vm.messageItemSingle.observe {
            adapter?.set(it.messages)
            bind.conversationRecyclerMessage.smoothScrollToPosition(
                bind.conversationRecyclerMessage.adapter!!.itemCount
            )
        }
        vm.statusInsertMessageSingle.observe {
            bind.conversationWidgetInput.text = ""
        }
        vm.statusInsertChatSingle.observe {
            handlerInsertChat(it)
        }
        vm.urlImageGallerySingle.observe {
            this.onSendClick(it, "image")
        }
    }

    private fun bindInfoToolbar() {
        when (chatInfo != null) {
            true -> {
                val name = toolbar.chatToolbarAvatar.bind(chatInfo!!)
                toolbar.chatToolbarName.text = name
            }
            else -> {
                toolbar.chatToolbarAvatar.bind(contactInfo)
                toolbar.chatToolbarName.text = "${contactInfo.firstName} ${contactInfo.lastName}"
            }
        }
    }

    private fun bindDataMessage() {
        when (chatInfo == null) {
            true -> ""
            else -> {
                val friend = chatInfo!!.listUserInfo!!
                adapter = ConversationAdapter(userLogin, friend)
                adapter?.bind(bind.conversationRecyclerMessage)
                vm.listenerItemChat(chatInfo!!.chatId)
            }
        }
    }

    private fun handlerInsertChat(it: StoreChat?) {
        bind.conversationWidgetInput.text = ""
        when (it == null) {
            true -> toast("insert chat failt")
            else -> {
                chatInfo = it
                bindDataMessage()
            }
        }
    }

    /**
     * handler click widget input
     */
    override fun onSendClick(mess: String, typeData: String?) {
        if (mess.isEmpty()) return
        val messSend = StoreMessage().apply {
            sender = auth.uid.toString()
            time = System.currentTimeMillis()
            text = mess
            type = typeData
        }
        when (chatInfo == null) {
            true -> {
                val uidFriend = contactInfo.uid
                val chat = StoreChat().apply {
                    name = "${auth.uid}-${uidFriend}"
                    messages = listOf(messSend)
                    recipients = arrayListOf(auth.uid.toString(), uidFriend)
                    type = "contact"
                }
                vm.insertChat(auth.uid.toString(), uidFriend, chat)
            }
            else -> {
                val chatId = chatInfo!!.chatId
                vm.insertMessage(chatId, messSend)
            }
        }
    }

    override fun onPhotoClick(media: Media) {
        media.uri ?: return
        val chatId = chatInfo?.name.let {
            if (it.isNullOrEmpty()) "${auth.uid}-${contactInfo.uid}" else it
        }
        vm.uploadImageGallery(chatId, media.uri!!)
    }

}