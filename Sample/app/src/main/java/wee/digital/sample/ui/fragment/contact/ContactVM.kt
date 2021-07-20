package wee.digital.sample.ui.fragment.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.parse
import wee.digital.library.extension.transform
import wee.digital.sample.data.repository.StoreRepository
import wee.digital.sample.ui.model.ContactData
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.sample.utils.documentToJsObject

class ContactVM : BaseVM() {

    var allListContacts = MutableLiveData<List<StoreUser>>()

    var contactAdapterSelected = StoreUser()

    val contactsSearchLiveData = MutableLiveData<List<StoreUser>?>()

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
                    val list = data.transform { StoreUser.from(it) }
                    contactsSearchLiveData.postValue(list)
                }
            }
        }
    }

    private var methodUpdateContacts = false

    fun syncContactUser(uidAuth: String, uidContact: String) {
        val mapUid = HashMap<String, Any>().apply { put("uid", FieldValue.arrayUnion(uidContact)) }
        StoreRepository.contactsReference(uidAuth).also {
            if (methodUpdateContacts) it.update(mapUid) else it.set(mapUid)
        }
    }

    fun queryUidContacts(uidAuth: String) {
        uidContactQueryListener?.remove()
        uidContactQueryListener =
            StoreRepository.contactsReference(uidAuth).addSnapshotListener { value, error ->
                val data = value?.documentToJsObject().parse(ContactData::class)
                when (data == null || data?.uid.isNullOrEmpty()) {
                    true -> {
                        methodUpdateContacts = false
                        allListContacts.postValue(listOf())
                    }
                    else -> queryFromUidContact(data.uid)
                }
            }
    }

    private fun queryFromUidContact(listUid: List<String>) {
        StoreRepository.userArrayContainUid(listUid)
            .get()
            .addOnSuccessListener {
                val data = it.documents
                val list = mutableListOf<StoreUser>()
                data.forEach {
                    it.documentToJsObject().parse(StoreUser::class).also {
                        it ?: return@also
                        list.add(it)
                    }
                }
                allListContacts.postValue(list)
            }
            .addOnFailureListener {
                allListContacts.postValue(listOf<StoreUser>())
            }
    }

}