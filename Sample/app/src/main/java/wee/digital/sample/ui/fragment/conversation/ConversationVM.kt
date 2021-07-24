package wee.digital.sample.ui.fragment.conversation

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.library.extension.parse
import wee.digital.sample.data.repository.StoreRepository
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.sample.utils.documentToJsObject

class ConversationVM : BaseVM() {

    val chatItemSingle = SingleLiveData<StoreChat>()

    private var chatItemListener: ListenerRegistration? = null

    fun listenerItemChat(uidChat: String) {
        viewModelScope.launch {
            chatItemListener?.remove()
            chatItemListener = StoreRepository.chats.document(uidChat)
                .addSnapshotListener { value, error ->
                    val data = value?.documentToJsObject().parse(StoreChat::class) ?: StoreChat()
                    chatItemSingle.postValue(data)
                }
        }
    }

}