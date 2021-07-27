package wee.digital.sample.ui.fragment.conversation

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.library.extension.parse
import wee.digital.sample.data.repository.chats
import wee.digital.sample.data.repository.users
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreMessage
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.sample.utils.documentToJsObject

class ConversationVM : BaseVM() {

    val statusInsertMessageSingle = SingleLiveData<Boolean>()

    val messageItemSingle = SingleLiveData<StoreChat>()

    val statusInsertChatSingle = SingleLiveData<StoreChat?>()

    private var chatItemListener: ListenerRegistration? = null

    fun listenerItemChat(uidChat: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatItemListener?.remove()
            chatItemListener = chats.document(uidChat)
                .addSnapshotListener { value, _ ->
                    val data = value?.documentToJsObject().parse(StoreChat::class) ?: StoreChat()
                    messageItemSingle.postValue(data)
                }
        }
    }

    fun insertMessage(chatId: String, chatMessage: StoreMessage) {
        viewModelScope.launch(Dispatchers.IO) {
            val map =
                HashMap<String, Any>().apply { put("messages", FieldValue.arrayUnion(chatMessage)) }
            chats.document(chatId).get().addOnSuccessListener {
                if (it.exists()) {
                    chats.document(chatId).update(map)
                        .addOnSuccessListener { statusInsertMessageSingle.postValue(true) }
                        .addOnFailureListener { statusInsertMessageSingle.postValue(false) }
                } else {
                    chats.document(chatId).set(map)
                        .addOnSuccessListener { statusInsertMessageSingle.postValue(true) }
                        .addOnFailureListener { statusInsertMessageSingle.postValue(false) }
                }
            }
        }
    }

    fun insertChat(uidFriend: String, chat: StoreChat) {
        viewModelScope.launch(Dispatchers.IO) {
            val document = chats.document()
            chat.chatId = document.id
            document.set(chat).addOnSuccessListener {
                getFriendInfo(uidFriend, chat)
            }.addOnFailureListener {
                statusInsertChatSingle.postValue(null)
            }
        }
    }

    private fun getFriendInfo(authUid: String, chat: StoreChat) {
        viewModelScope.launch(Dispatchers.IO) {
            users.document(authUid).get().addOnSuccessListener {
                val user = it.documentToJsObject().parse(StoreUser::class) ?: StoreUser()
                chat.listUserInfo = listOf(user)
                statusInsertChatSingle.postValue(chat)
            }.addOnFailureListener {
                statusInsertChatSingle.postValue(null)
            }
            /*val data =
                HashMap<String, Any>().apply { put("chatIds", FieldValue.arrayUnion(chat.chatId)) }
            conversations.document(authUid).get().addOnSuccessListener {
                when (it.exists()) {
                    true -> {
                        conversations.document(authUid).update(data)
                            .addOnSuccessListener { statusInsertChatSingle.postValue(chat) }
                            .addOnFailureListener { statusInsertChatSingle.postValue(null) }
                    }
                    else -> {
                        conversations.document(authUid).set(data)
                            .addOnSuccessListener { statusInsertChatSingle.postValue(chat) }
                            .addOnFailureListener { statusInsertChatSingle.postValue(null) }
                    }
                }
            }*/
        }
    }

}