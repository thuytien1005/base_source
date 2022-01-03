package wee.digital.sample.ui.splash

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.ui.base.BaseVM

class SplashVM : BaseVM() {

    val authSuccessLiveData = SingleLiveData(false)

    val authErrorLiveData = SingleLiveData<String?>()

    fun onAuthUser() {
        viewModelScope.launch {
            delay(5000)
            authSuccessLiveData.postValue(true)
        }
    }


}