package wee.digital.sample.ui.fragment.chat

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.library.extension.parse
import wee.digital.sample.data.repository.StoreRepository
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreConversation
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.sample.utils.documentToJsObject

class ChatVM : BaseVM() {

    val listChatStoreSingle = SingleLiveData<List<StoreChat>>()

    val userLoginSingle = SingleLiveData<StoreUser>()

    private var queryCvsIdListener: ListenerRegistration? = null

    private var queryChatListener: ListenerRegistration? = null

    private var uid: String = ""

    private fun syncUserLogin(uid: String) {
        StoreRepository.users.document(uid).get()
            .addOnSuccessListener {
                val user = it.documentToJsObject().parse(StoreUser::class) ?: StoreUser()
                userLoginSingle.postValue(user)
            }
    }

    fun queryConversationId(uid: String) {
        viewModelScope.launch {
            syncUserLogin(uid)
            this@ChatVM.uid = uid
            queryCvsIdListener?.remove()
            queryCvsIdListener =
                StoreRepository.conversations.document(uid).addSnapshotListener { value, error ->
                    val idConversation = value?.documentToJsObject().parse(StoreConversation::class)
                    when (idConversation == null || idConversation.chatIds.isNullOrEmpty()) {
                        true -> ""
                        else -> {
                            queryChat(idConversation.chatIds!!)
                        }
                    }
                }
        }
    }

    private fun queryChat(listMessage: List<String>) {
        viewModelScope.launch {
            queryChatListener?.remove()
            queryChatListener = StoreRepository.chats.whereIn("chatId", listMessage)
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
            StoreRepository.userQueryByUid(listUid)
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