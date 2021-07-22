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
import wee.digital.sample.ui.model.toDataList
import wee.digital.sample.ui.vm.BaseVM

class ContactVM : BaseVM() {

    var allListContacts = SingleLiveData<List<StoreUser>?>()

    val contactsSearchLiveData = SingleLiveData<List<StoreUser>?>()

    private var searchRegistration: ListenerRegistration? = null

    private var uidContactQueryListener: ListenerRegistration? = null

    fun searchUserByName(name: String?) {
        when {
            name.isNullOrEmpty() -> contactsSearchLiveData.postValue(null)
            else -> userQuery(name)
        }
    }

    private fun userQuery(name: String) {
        log.d("queryUser - name: %s".format(name))
        searchRegistration?.remove()
        searchRegistration = StoreRepository.userQueryBySearchKey(name)
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

    /**
     *
     */
    fun insertContact(selfUid: String, targetUid: String) {
        val mapUid = HashMap<String, Any>().apply { put("uid", FieldValue.arrayUnion(targetUid)) }
        StoreRepository.contactsReference(selfUid)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    StoreRepository.contactsReference(selfUid).update(mapUid)
                } else {
                    StoreRepository.contactsReference(selfUid).set(mapUid)
                }
            }
    }

    fun syncContact(uid: String?) {
        uid ?: return
        uidContactQueryListener?.remove()
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
        uidContactQueryListener = StoreRepository.userQueryByUid(contact.uids!!)
            .addSnapshotListener { value, error ->
                val list = value?.documents.toDataList {
                    StoreUser.fromMap(it)
                }
                allListContacts.postValue(list)
            }

    }

}