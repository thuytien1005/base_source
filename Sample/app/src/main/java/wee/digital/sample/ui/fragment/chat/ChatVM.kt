package wee.digital.sample.ui.fragment.chat

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.library.extension.parse
import wee.digital.sample.data.repository.*
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreConversation
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.sample.utils.documentToJsObject

class ChatVM : BaseVM() {

    val listChatStoreSingle = SingleLiveData<List<StoreChat>>()

    private var queryCvsIdListener: ListenerRegistration? = null

    private var queryChatListener: ListenerRegistration? = null

    private var uid: String = ""

    fun queryConversationId(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            this@ChatVM.uid = uid
            queryCvsIdListener?.remove()
            queryCvsIdListener =
                conversations.document(uid).addSnapshotListener { value, _ ->
                    val conversation = StoreConversation.fromMap(value?.data)
                    when {
                        conversation.chatIds.isNotEmpty() -> queryChat(conversation.chatIds)
                    }
                }
        }
    }

    private fun queryChat(listMessage: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            queryChatListener?.remove()
            queryChatListener = chats.whereIn("chatId", listMessage)
                .addSnapshotListener { value, error ->
                    val listChat = mutableListOf<StoreChat>()
                    value?.documents?.forEach {
                        val chat = it.documentToJsObject().parse(StoreChat::class)
                        when (chat == null) {
                            true -> ""
                            else -> listChat.add(chat)
                        }
                    }
                    queryListInfoUser(listChat)
                }
        }
    }

    private fun queryListInfoUser(list: List<StoreChat>) {
        list.forEach { chat ->
            val listUid = chat.recipients?.filter { it != uid } ?: listOf()
            userByUids(listUid)
                .get()
                .addOnSuccessListener {
                    val listUser = mutableListOf<StoreUser>()
                    it.documents.forEach {
                        val user = it.documentToJsObject().parse(StoreUser::class)
                        listUser.add(user ?: StoreUser())
                    }
                    chat.listUserInfo = listUser
                    when (chat == list.last()) {
                        true -> listChatStoreSingle.postValue(list)
                    }
                }
        }
    }

}