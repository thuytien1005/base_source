package wee.digital.sample.ui.fragment.conversation

import android.view.LayoutInflater
import android.view.View
import wee.digital.library.extension.toast
import wee.digital.library.extension.viewModel
import wee.digital.sample.R
import wee.digital.sample.databinding.ConversationBinding
import wee.digital.sample.shared.userLogin
import wee.digital.sample.ui.main.MainDialogFragment
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.bind

class ConversationFragment : MainDialogFragment<ConversationBinding>() {

    override fun dialogStyle(): Int {
        return R.style.App_Dialog_FullScreen_Transparent
    }

    private val vm by viewModel(ConversationVM::class)

    var adapter: ConversationAdapter? = null

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
        configUi()
        handlerClickWidget()
        vm.listenerItemChat(mainVM.chatAdapterSelected.chatId)
    }

    private fun configUi() {
        val friend = mainVM.chatAdapterSelected.listUserInfo!!
        adapter = ConversationAdapter(userLogin, friend)
        adapter?.bind(bind.conversationRecyclerMessage)
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
            toolbar.chatToolbarVector -> navigateUp()
            toolbar.chatToolbarAvatar -> toast("avatar")
            toolbar.chatToolbarVideo -> toast("video")
            toolbar.chatToolbarCall -> toast("call")
            toolbar.chatToolbarMenu -> toast("menu")
        }
    }

    override fun onLiveDataObserve() {
        vm.chatItemSingle.observe {
            adapter?.set(it.messages)
        }
    }

}