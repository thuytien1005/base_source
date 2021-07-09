package wee.digital.sample.ui.main.fragment.face

import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.util.EventLiveData
import wee.digital.sample.repository.model.RegisterData
import wee.digital.sample.repository.model.UserData
import wee.digital.sample.shared.auth
import wee.digital.sample.shared.fireStorage
import wee.digital.sample.shared.fireStore
import wee.digital.sample.ui.main.vm.BaseVM

class FaceVM : BaseVM() {

    val addUserSuccessEvent = EventLiveData<String>()

    val errorAccountEvent = EventLiveData<String>()

    val getLinkAvatar = EventLiveData<Uri>()

    private var userData: RegisterData = RegisterData()

    fun createAccount(user: RegisterData) {
        userData = user
        viewModelScope.launch(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(user.email, user.password)
                .addOnSuccessListener {
                    signInAccount()
                }
                .addOnFailureListener {
                    log.d(it.message.toString())
                    errorAccountEvent.postValue(it.message.toString())
                }
        }
    }

    private fun signInAccount() {
        viewModelScope.launch(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(userData.email, userData.password)
                .addOnSuccessListener {
                    updateImage()
                }
                .addOnFailureListener {
                    log.d(it.message.toString())
                    errorAccountEvent.postValue(it.message.toString())
                }
        }
    }

    private fun updateImage() {
        val path = "images/${System.currentTimeMillis()}.jpg"
        fireStorage.child(path)
            .putBytes(userData.face!!)
            .addOnSuccessListener {
                getUrlUpload(path)

            }
            .addOnFailureListener {
                log.d(it.message.toString())
                errorAccountEvent.postValue(it.message.toString())
            }
    }

    private fun getUrlUpload(path: String) {
        fireStorage.child(path).downloadUrl.addOnSuccessListener {
            getLinkAvatar.postValue(it)
            userData.urlAvatar = it.toString()
            addUser()
        }
            .addOnFailureListener {
                errorAccountEvent.postValue(it.message.toString())
            }
    }

    private fun addUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val uuid = auth.currentUser?.uid ?: ""
            fireStore.collection("users").document(uuid)
                .set(UserData(name = userData.name, url = userData.urlAvatar))
                .addOnSuccessListener {
                    addUserSuccessEvent.postValue("")
                }
                .addOnFailureListener {
                    log.d(it.message.toString())
                    errorAccountEvent.postValue(it.message.toString())
                }
        }
    }

}