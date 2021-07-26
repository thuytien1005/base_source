package wee.digital.sample.ui.usecase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ListenerRegistration
import wee.digital.sample.data.repository.auth
import wee.digital.sample.data.repository.selfContactRef
import wee.digital.sample.data.repository.userByUids
import wee.digital.sample.ui.model.StoreContact
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.model.toDataList

class SelfContactUseCase(
    private val contactsLiveData: MutableLiveData<List<StoreUser>?>
) {

    private var selfContactsReg: ListenerRegistration? = null

    private var userContactReg: ListenerRegistration? = null

    fun syncContact() {
        selfContactsReg?.remove()
        selfContactsReg = selfContactRef.addSnapshotListener { value, error ->
            userContactReg?.remove()
            val contact = StoreContact.fromMap(value?.data)
            when {
                contact.uids.isNullOrEmpty() -> contactsLiveData.postValue(null)
                else -> listenUserContacts(contact)
            }
        }
    }

    private fun listenUserContacts(contact: StoreContact) {
        userContactReg = userByUids(contact.uids!!)
            .addSnapshotListener { value, _ ->
                val list = value?.documents
                    .toDataList {
                        StoreUser.fromMap(it)
                    }.filter {
                        it.uid != auth.uid
                    }
                contactsLiveData.postValue(list)
            }
    }
}