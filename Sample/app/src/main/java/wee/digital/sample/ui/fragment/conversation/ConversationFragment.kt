package wee.digital.sample.ui.fragment.conversation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import wee.digital.library.extension.hideKeyboard
import wee.digital.library.extension.toast
import wee.digital.sample.R
import wee.digital.sample.data.repository.auth
import wee.digital.sample.data.repository.userLogin
import wee.digital.sample.databinding.ConversationBinding
import wee.digital.sample.ui.main.MainDialogFragment
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreMessage
import wee.digital.sample.widget.WidgetChatInput


class ConversationFragment : MainDialogFragment<ConversationBinding>(),
    WidgetChatInput.WidgetChatInputListener {

    private var resultGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                handlerGallerySelected(data?.data)
            }
        }

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
        bindData()
        bind.conversationWidgetInput.listener = this
        bind.conversationRecyclerMessage.setOnTouchListener { v, event ->
            hideKeyboard()
            false
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {
            toolbar.chatToolbarVector -> dismiss()
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

    private fun bindData() {
        when (mainVM.chatAdapterSelected == null) {
            true -> ""
            else -> {
                val friend = mainVM.chatAdapterSelected!!.listUserInfo!!
                adapter = ConversationAdapter(userLogin, friend)
                adapter?.bind(bind.conversationRecyclerMessage)
                adapter?.onItemClick = { _, _ -> hideKeyboard() }
                vm.listenerItemChat(mainVM.chatAdapterSelected!!.chatId)
            }
        }
    }

    private fun handlerInsertChat(it: StoreChat?) {
        bind.conversationWidgetInput.text = ""
        when (it == null) {
            true -> toast("insert chat failt")
            else -> {
                mainVM.chatAdapterSelected = it
                bindData()
            }
        }
    }

    private fun handlerGallerySelected(uri: Uri?) {
        uri ?: return
        val chatId = mainVM.chatAdapterSelected?.chatId ?: return
        vm.uploadImageGallery(chatId, uri)
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
        when (mainVM.chatAdapterSelected == null) {
            true -> {
                val uidFriend = mainVM.contactAdapterSelected.uid
                val chat = StoreChat().apply {
                    name = "${auth.uid}-${uidFriend}"
                    messages = listOf(messSend)
                    recipients = arrayListOf(auth.uid.toString(), uidFriend)
                    type = "contact"
                }
                vm.insertChat(auth.uid.toString(), uidFriend, chat)
            }
            else -> {
                val chatId = mainVM.chatAdapterSelected!!.chatId
                vm.insertMessage(chatId, messSend)
            }
        }
    }

    override fun onPhotoClick() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        resultGallery.launch(intent)
    }

}