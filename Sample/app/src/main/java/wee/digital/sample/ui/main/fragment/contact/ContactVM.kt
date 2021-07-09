package wee.digital.sample.ui.main.fragment.contact

import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.util.EventLiveData
import wee.digital.sample.repository.model.UserData
import wee.digital.sample.shared.fireStore
import wee.digital.sample.ui.main.vm.BaseVM

class ContactVM : BaseVM() {

    val listCustomerEvent = EventLiveData<List<UserData>>()
    fun queryCustomer(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            log.d("query")
            fireStore.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener {
                    querySuccess(it.documents)
                }
                .addOnFailureListener {
                    listCustomerEvent.postValue(listOf())
                }
        }
    }

    private fun querySuccess(data: MutableList<DocumentSnapshot>) {
        when (data.isEmpty()) {
            true -> listCustomerEvent.postValue(listOf())
            else -> {
                val list = mutableListOf<UserData>()
                data.forEach { list.add(UserData(it["name"] as String, it["url"] as String)) }
                listCustomerEvent.postValue(list)
            }
        }
    }

}