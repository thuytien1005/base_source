package wee.digital.sample.ui.fragment.plash

import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.R
import wee.digital.sample.shared.currentUser
import wee.digital.sample.ui.vm.BaseVM

class SplashVM : BaseVM() {

    val nextDestinationLiveData = SingleLiveData<Int>()

    fun syncCurrentUser(user: FirebaseUser? = currentUser) {
        viewModelScope.launch(Dispatchers.IO) {
            delay(1000)
            nextDestinationLiveData.postValue(
                when (user) {
                    null -> R.id.action_global_loginFragment
                    else -> R.id.action_global_homeFragment
                }
            )
        }
    }
}