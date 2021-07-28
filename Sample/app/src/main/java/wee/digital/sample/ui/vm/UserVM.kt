package wee.digital.sample.ui.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.MapValueNullException
import wee.digital.library.extension.toast
import wee.digital.sample.data.repository.auth
import wee.digital.sample.data.repository.selfUserRef
import wee.digital.sample.ui.model.StoreUser

class UserVM : BaseVM() {

    val firebaseUserLiveData = MutableLiveData<FirebaseUser?>()

    fun onFirebaseAppInit(app: Application) {
        FirebaseApp.initializeApp(app)
        auth.addAuthStateListener {
            userRegistration?.remove()
            val user = it.currentUser
            firebaseUserLiveData.value = user
            if(null != user) {
                syncUser()
            } else {
                userRegistration?.remove()
            }
        }
        auth.addIdTokenListener(FirebaseAuth.IdTokenListener {
        })
    }

    val storeUserLiveData = MutableLiveData<StoreUser>()

    private var userRegistration: ListenerRegistration? = null

    fun syncUser() {
        userRegistration = selfUserRef
            .addSnapshotListener { snapshot, _ ->
                snapshot?.data?.also {
                    syncUser(it)
                }
            }
    }

    private fun syncUser(map: Map<String, Any>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = StoreUser.fromMap(map)
                storeUserLiveData.postValue(user)
            } catch(e: MapValueNullException) {
                toast(e.message)
            }
        }
    }


}