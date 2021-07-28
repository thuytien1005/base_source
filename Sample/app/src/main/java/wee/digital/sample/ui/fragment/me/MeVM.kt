package wee.digital.sample.ui.fragment.me

import android.net.Uri
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.data.repository.storage
import wee.digital.sample.data.repository.users
import wee.digital.sample.ui.vm.BaseVM

class MeVM : BaseVM() {

    private var authId = ""

    val updateAvatarSingle = SingleLiveData<Boolean>()

    fun uploadAvatar(uri: Uri, auth: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authId = auth
            val path = "images/$auth"
            storage.child(path).putFile(uri)
                .addOnSuccessListener {
                    getUrlAvatar(path)
                }
        }
    }

    private fun getUrlAvatar(path: String) {
        viewModelScope.launch(Dispatchers.IO) {
            storage.child(path).downloadUrl.addOnSuccessListener {
                updateUserAvatar(it.toString())
            }
        }
    }

    private fun updateUserAvatar(url: String) {
        users.document(authId).update("photoDisplay", url).addOnSuccessListener {
            updateAvatarSingle.postValue(true)
        }.addOnFailureListener {
            updateAvatarSingle.postValue(false)
        }
    }

}