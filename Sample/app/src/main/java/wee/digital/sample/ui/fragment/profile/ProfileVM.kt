package wee.digital.sample.ui.fragment.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.library.extension.parse
import wee.digital.library.extension.toast
import wee.digital.sample.data.firebase.conversationByUid
import wee.digital.sample.data.firebase.userByUids
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.job.ContactAddJob
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.sample.utils.documentToJsObject

class ProfileVM : BaseVM() {

    val userLiveData = MutableLiveData<StoreUser>()

    val addContactSuccessLiveData = SingleLiveData<Boolean>()

    val chatStoreSingle = SingleLiveData<StoreChat>()

    val chatStoreEmptySingle = SingleLiveData<Boolean>()

    private var authUid = ""

    fun insertContact() {
        ContactAddJob(
            uid = userLiveData.value!!.uid,
            onSuccess = {
                addContactSuccessLiveData.value = true
            },
            onFailure = {
                toast(it.message)
            })
            .insertContact()
    }

    fun checkConversationExists(myUid: String, uidContact: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authUid = myUid
            conversationByUid(listOf(uidContact, myUid)).get().addOnSuccessListener {
                when (it.documents.isNullOrEmpty()) {
                    true -> checkListOpposite(listOf(myUid, uidContact))
                    else -> {
                        val chat =
                            it.documents.first().documentToJsObject().parse(StoreChat::class)
                        queryListInfoUser(chat ?: StoreChat())
                    }
                }
            }
        }
    }

    private fun checkListOpposite(list: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            conversationByUid(list).get().addOnSuccessListener {
                when (it.documents.isNullOrEmpty()) {
                    true -> chatStoreEmptySingle.postValue(true)
                    else -> {
                        val chat =
                            it.documents.first().documentToJsObject().parse(StoreChat::class)
                        queryListInfoUser(chat ?: StoreChat())
                    }
                }
            }
        }
    }

    private fun queryListInfoUser(chat: StoreChat) {
        val listUid = chat.recipients?.filter { it != authUid } ?: listOf()
        userByUids(listUid).get().addOnSuccessListener {
            val listUser = mutableListOf<StoreUser>()
            it.documents.forEach {
                val user = it.documentToJsObject().parse(StoreUser::class)
                listUser.add(user ?: StoreUser())
            }
            chat.listUserInfo = listUser
            chatStoreSingle.postValue(chat)
        }
    }

}