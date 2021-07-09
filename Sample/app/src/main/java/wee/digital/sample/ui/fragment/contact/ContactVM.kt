package wee.digital.sample.ui.fragment.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import wee.digital.library.extension.transform
import wee.digital.sample.repository.model.UserData
import wee.digital.sample.shared.fireStore
import wee.digital.sample.ui.vm.BaseVM

class ContactVM : BaseVM() {

    val usersLiveData = MutableLiveData<List<UserData>?>()

    private var searchJob: Job? = null

    fun searchUserByName(name: String?) {
        when{
            name.isNullOrEmpty()->usersLiveData.postValue(null)
            else -> queryUsers(name)
        }
    }

    private fun queryUsers(name: String){
        log.d("queryUser - name: %s".format(name))
        searchJob?.cancel()
        fireStore.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnSuccessListener {
                    onQueryUserResult(it.documents)
                }
                .addOnFailureListener {
                    usersLiveData.postValue(null)
                }
    }

    private fun onQueryUserResult(data: MutableList<DocumentSnapshot>) {
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            when (data.isEmpty()) {
                true -> usersLiveData.postValue(listOf())
                else -> {
                    val list = data.transform { UserData(it["name"].toString(), it["url"].toString()) }
                    usersLiveData.postValue(list)
                }
            }
        }

    }

}