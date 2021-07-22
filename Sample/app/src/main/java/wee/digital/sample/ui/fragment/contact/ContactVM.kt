package wee.digital.sample.ui.fragment.contact

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.library.extension.transform
import wee.digital.sample.data.repository.StoreRepository
import wee.digital.sample.ui.model.StoreContact
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.vm.BaseVM

class ContactVM : BaseVM() {

    var allListContacts = SingleLiveData<List<StoreUser>?>()

    val contactsSearchLiveData = SingleLiveData<List<StoreUser>?>()

    private var searchRegistration: ListenerRegistration? = null

    private var uidContactQueryListener: ListenerRegistration? = null

    fun searchUserByName(name: String?) {
        when {
            name.isNullOrEmpty() -> contactsSearchLiveData.postValue(null)
            else -> queryUsers(name)
        }
    }

    private fun queryUsers(name: String) {
        log.d("queryUser - name: %s".format(name))
        searchRegistration?.remove()
        searchRegistration = StoreRepository.userSearch(name)
            .addSnapshotListener { value, error ->
                onQueryUserResult(value?.documents)
            }

    }

    private fun onQueryUserResult(data: List<DocumentSnapshot>?) {
        viewModelScope.launch(Dispatchers.IO) {
            when {
                data.isNullOrEmpty() -> contactsSearchLiveData.postValue(null)
                else -> {
                    val list = data.transform { StoreUser.fromMap(it.data!!) }
                    contactsSearchLiveData.postValue(list)

                }
            }
        }
    }

    fun syncContactUser(uidAuth: String, uidContact: String) {
        val mapUid = HashMap<String, Any>().apply { put("uid", FieldValue.arrayUnion(uidContact)) }
        StoreRepository.contactsReference(uidAuth).get()
            .addOnSuccessListener {
                if (it.exists()) {
                    StoreRepository.contactsReference(uidAuth).update(mapUid)
                } else {
                    StoreRepository.contactsReference(uidAuth).set(mapUid)
                }
            }
    }

    fun queryUidContacts(uid: String?) {
        uid ?: return
        StoreRepository.contactsReference(uid)
            .get()
            .addOnSuccessListener {
                val contact = StoreContact.fromMap(it.data!!)
                when {
                    contact.uids.isNullOrEmpty() -> allListContacts.postValue(null)
                    else -> queryFromUidContact(contact)
                }
            }
    }

    private fun queryFromUidContact(contact: StoreContact) {
        StoreRepository.userArrayContainUid(contact.uids!!)
            .get()
            .addOnSuccessListener {
                val list = mutableListOf<StoreUser>()
                it.documents.forEach { doc ->
                    list.add(StoreUser.fromMap(doc.data!!))
                }
                allListContacts.postValue(list)
            }
            .addOnFailureListener {
                allListContacts.postValue(listOf())
            }
    }

}