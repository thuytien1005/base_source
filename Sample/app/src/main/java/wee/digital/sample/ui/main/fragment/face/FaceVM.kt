package wee.digital.sample.ui.main.fragment.face

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.util.EventLiveData
import wee.digital.sample.repository.model.RegisterData
import wee.digital.sample.shared.firestore
import wee.digital.sample.ui.main.vm.BaseVM

class FaceVM : BaseVM() {

    val addUserStatusEvent = EventLiveData<String>()
    fun addUser(user: RegisterData) {
        viewModelScope.launch(Dispatchers.IO) {
            firestore.collection("users").add(user)
                .addOnSuccessListener {
                    addUserStatusEvent.postValue("")
                }
                .addOnFailureListener {
                    addUserStatusEvent.postValue(it.message.toString())
                }
        }
    }

}