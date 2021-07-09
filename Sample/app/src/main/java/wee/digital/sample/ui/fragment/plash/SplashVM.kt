package wee.digital.sample.ui.fragment.plash

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.R
import wee.digital.sample.shared.currentUser
import wee.digital.sample.ui.main.vm.BaseVM

class SplashVM : BaseVM() {

    val nextDestinationLiveData = SingleLiveData<Int>()

    val nextDestination
        get() = when (currentUser) {
            null -> R.id.action_global_loginFragment
            else -> R.id.action_global_homeFragment
        }

    fun syncCurrentUser() {
        flow {
            delay(1000)
            emit(nextDestination)
        }.flowOn(Dispatchers.IO).onEach {
            nextDestinationLiveData.postValue(it)
        }.launchIn(viewModelScope)
    }


}