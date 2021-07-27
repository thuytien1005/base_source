package wee.digital.sample.ui.fragment.conversation

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.library.extension.parse
import wee.digital.sample.data.repository.chats
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreMessage
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.sample.utils.documentToJsObject

class ConversationVM : BaseVM() {

    val statusInsertChatSingle = SingleLiveData<Boolean>()

    val chatItemSingle = SingleLiveData<StoreChat>()

    private var chatItemListener: ListenerRegistration? = null

    fun listenerItemChat(uidChat: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatItemListener?.remove()
            chatItemListener = chats.document(uidChat)
                .addSnapshotListener { value, _ ->
                    val data = value?.documentToJsObject().parse(StoreChat::class) ?: StoreChat()
                    chatItemSingle.postValue(data)
                }
        }
    }

    fun insertChat(chatId: String, chatMessage: StoreMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            val map =
                HashMap<String, Any>().apply { put("messages", FieldValue.arrayUnion(chatMessage)) }
            chats.document(chatId).get().addOnSuccessListener {
                if (it.exists()) {
                    chats.document(chatId).update(map)
                        .addOnSuccessListener { statusInsertChatSingle.postValue(true) }
                        .addOnFailureListener { statusInsertChatSingle.postValue(false) }
                } else {
                    chats.document(chatId).set(map)
                        .addOnSuccessListener { statusInsertChatSingle.postValue(true) }
                        .addOnFailureListener { statusInsertChatSingle.postValue(false) }
                }
            }
        }
    }

}