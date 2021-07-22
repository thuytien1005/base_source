package wee.digital.sample.ui.fragment.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.transform
import wee.digital.sample.data.repository.StoreRepository
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.vm.BaseVM

class ContactVM : BaseVM() {

    val contactsLiveData = MutableLiveData<List<StoreUser>?>()

    private var searchRegistration: ListenerRegistration? = null

    fun searchUserByName(name: String?) {
        when {
            name.isNullOrEmpty() -> contactsLiveData.postValue(null)
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
                data.isNullOrEmpty() -> contactsLiveData.postValue(null)
                else -> {
                    val list = data.transform { snapshot: DocumentSnapshot ->
                        snapshot.data?.also {
                            return@transform StoreUser.fromMap(it)
                        }
                        return@transform null
                    }
                    contactsLiveData.postValue(list)
                }
            }
        }
    }

}