package wee.digital.sample.ui.fragment.register

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.ui.usecase.RegisterUseCase
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.widget.extension.isNotEmail

class RegisterVM : BaseVM() {

    val firstNameErrorLiveData = SingleLiveData<String?>()

    val emailErrorLiveData = SingleLiveData<String?>()

    val passwordErrorLiveData = SingleLiveData<String?>()

    private val hasError: Boolean
        get() {
            return firstNameErrorLiveData.value != null ||
                        emailErrorLiveData.value != null ||
                        passwordErrorLiveData.value != null
        }

    fun createUser(firstName: String?, lastName: String?, email: String?, password: String?) {
        validateCredentials(firstName, email, password)
        if(hasError) return
        val user = StoreUser().also {
            it.firstName = firstName!!
            it.lastName = lastName!!
            it.email = email!!
        }
        createUserAndSignIn(user, password!!)
    }

    private fun validateCredentials(firstName: String?, email: String?, password: String?) {
        firstNameErrorLiveData.value = when {
            firstName.isNullOrEmpty() -> "First name is non optional"
            else -> null
        }
        emailErrorLiveData.value = when {
            email.isNotEmail -> "Email incorrect format"
            else -> null
        }
        passwordErrorLiveData.value = when {
            password.isNullOrEmpty() -> "Password is non optional"
            else -> null
        }
    }

    private fun createUserAndSignIn(user: StoreUser, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            RegisterUseCase(user, password,
                onSuccess = {
                    val user = it.user ?: throw NullPointerException()
                    log.d("sign in with user provider: %s, uid: %s".format(user.providerId, user.uid))
                },
                onFailure = {
                    log.d("sign in failure")
                    passwordErrorLiveData.value = it.message
                })
                .createUserWithEmailAndPassword()
        }
    }

}
