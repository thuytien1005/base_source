package wee.digital.sample.ui.fragment.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.*
import wee.digital.library.extension.transform
import wee.digital.sample.shared.store
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.widget.extension.normalizer

class ContactVM : BaseVM() {

    val contactsLiveData = MutableLiveData<List<StoreUser>?>()

    private var searchJob: Job? = null

    private var searchRegistration: ListenerRegistration? = null

    fun searchUserByName(name: String?) {
        when {
            name.isNullOrEmpty() -> contactsLiveData.postValue(null)
            else -> queryUsers(name)
        }
    }

    private fun queryUsers(name: String) {
        log.d("queryUser - name: %s".format(name))

        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            val task: Task<QuerySnapshot> = store.collection("users")
                .whereEqualTo("searchKey", name.normalizer())
                .get()
                .addOnSuccessListener {
                    log.d("OnSuccess")
                    onQueryUserResult(it.documents)
                }
                .addOnFailureListener {
                    contactsLiveData.postValue(null)
                }
        }
//        searchJob?.cancel()
        /*searchJob = viewModelScope.launch(Dispatchers.IO){
            delay(500)
            searchRegistration = userCollection
                .whereEqualTo("searchKey", name.normalizer())
                .addSnapshotListener(object : EventListener<QuerySnapshot> {
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {

                    }

                })
        }*/

    }

    private fun onQueryUserResult(data: MutableList<DocumentSnapshot>) {
        log.d("onQueryUserResult")
        when {
            data.isEmpty() -> contactsLiveData.postValue(listOf())
            else -> {
                val list = data.transform { StoreUser.from(it) }
                contactsLiveData.postValue(list)
            }
        }

    }

}