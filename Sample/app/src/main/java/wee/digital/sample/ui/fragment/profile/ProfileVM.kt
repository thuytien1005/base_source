package wee.digital.sample.ui.fragment.profile

import androidx.lifecycle.MutableLiveData
import wee.digital.library.extension.SingleLiveData
import wee.digital.library.extension.toast
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.usecase.AddContactUseCase
import wee.digital.sample.ui.vm.BaseVM

class ProfileVM : BaseVM() {

    val userLiveData = MutableLiveData<StoreUser>()

    val addContactSuccessLiveData = SingleLiveData<Boolean>()

    fun insertContact() {
        AddContactUseCase(
            uid = userLiveData.value!!.uid,
            onSuccess = {
                addContactSuccessLiveData.value = true
            },
            onFailure = {
                toast(it.message)
            })
            .insertContact()
    }

}