package wee.digital.sample.ui.fragment.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.transform
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.job.SearchJob
import wee.digital.sample.ui.job.ContactSelfJob
import wee.digital.sample.ui.vm.BaseVM

class ContactVM : BaseVM() {

    val contactsLiveData = MutableLiveData<List<StoreUser>?>()

    val searchLiveData = MutableLiveData<List<StoreUser>?>()

    private val selfContactUseCase = ContactSelfJob(contactsLiveData)

    private val searchUseCase = SearchJob {
        viewModelScope.launch(Dispatchers.IO) {
            val list = it.transform { snapshot -> StoreUser.fromMap(snapshot.data!!) }
            searchLiveData.postValue(list)
        }
    }

    init {
        selfContactUseCase.syncContact()
    }

    fun search(name: String?) {
        when {
            name.isNullOrEmpty() -> searchLiveData.postValue(null)
            else -> searchUseCase.search(name)
        }
    }

}