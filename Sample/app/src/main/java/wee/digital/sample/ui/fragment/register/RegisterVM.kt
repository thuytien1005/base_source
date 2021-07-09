package wee.digital.sample.ui.fragment.register

import wee.digital.library.util.EventLiveData
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.widget.extension.isEmail
import wee.digital.widget.extension.notNullOrEmpty

class RegisterVM : BaseVM() {

    val errorNameEvent = EventLiveData<String?>()

    val errorEmailEvent = EventLiveData<String?>()

    val errorPasswordEvent = EventLiveData<String?>()

    val successInputEvent = EventLiveData<Boolean>()

    private val hasError get() = errorEmailEvent.value != null || errorPasswordEvent.value != null

    fun checkInput(name : String?, email: String?, password: String?) {
        errorNameEvent.value = if(name.notNullOrEmpty()) null else "Name is non optional"
        errorEmailEvent.value = if (email.isEmail) null else "Email incorrect format"
        errorPasswordEvent.value =
            if (password.notNullOrEmpty()) null else "Password is non optional"
        if (hasError) return
        successInputEvent.value = true
    }

}
