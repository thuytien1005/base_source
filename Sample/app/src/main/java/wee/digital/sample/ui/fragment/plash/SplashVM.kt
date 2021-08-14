package wee.digital.sample.ui.fragment.plash

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.R
import wee.digital.sample.data.firebase.currentUser
import wee.digital.sample.ui.vm.BaseVM

class SplashVM : BaseVM() {

    val nextDestinationLiveData = SingleLiveData<Int>()

    fun syncCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            nextDestinationLiveData.postValue(
                when(currentUser) {
                    null -> R.id.action_global_loginFragment
                    else -> R.id.action_global_homeFragment
                }
            )
        }
    }
}