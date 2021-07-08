package wee.digital.sample.ui.main.vm

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import wee.digital.library.util.EventLiveData
import wee.digital.sample.repository.model.RegisterData

class MainVM : BaseVM() {

    /**
     * View model on initialized
     */
    open fun onStart() {
    }

    /**
     * Trigger on view model initialized with network available and connectivity change to state available
     */
    open fun onNetworkAvailable() {
        flow {
            delay(1000)
            emit(1)
        }.flowOn(Dispatchers.IO).onStart {

        }.onCompletion {

        }.onEach {
            println(it)
        }.launchIn(viewModelScope)

    }

    private val registerDataEvent = EventLiveData<RegisterData>()
    var userInfo: RegisterData
        get() = registerDataEvent.value ?: RegisterData()
        set(value) {
            registerDataEvent.value = value
        }

}