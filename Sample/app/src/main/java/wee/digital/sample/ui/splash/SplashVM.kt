package wee.digital.sample.ui.splash

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.ui.main.vm.BaseVM

class SplashVM : BaseVM() {

    val navigateLiveData = SingleLiveData<Boolean>()

    fun syncAuthUser() {
        flow {
            delay(3000)
            emit(true)
        }.flowOn(Dispatchers.IO).onEach {
            navigateLiveData.postValue(true)
        }.launchIn(viewModelScope)
    }

}