package wee.digital.sample.ui.main.vm

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.shared.auth
import wee.digital.sample.shared.currentUser


class FirebaseVM : BaseVM() {

    val userLiveData = SingleLiveData<FirebaseUser>()

    fun onFirebaseAppInit(app: Application) {
        FirebaseApp.initializeApp(app)
        auth.addAuthStateListener {
            userLiveData.value = currentUser
        }
        auth.addIdTokenListener(FirebaseAuth.IdTokenListener {
        })
    }


}