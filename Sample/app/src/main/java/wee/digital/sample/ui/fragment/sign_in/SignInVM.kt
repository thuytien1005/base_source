package wee.digital.sample.ui.fragment.sign_in

import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.ui.job.SignInJob
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.widget.extension.isNotEmail

class SignInVM : BaseVM() {

    val emailErrorLiveData = SingleLiveData<String?>()

    val passwordErrorLiveData = SingleLiveData<String?>()

    private val hasError get() = null != emailErrorLiveData.value || null != passwordErrorLiveData.value

    fun signIn(email: String?, password: String?) {
        validateCredentials(email, password)
        if(hasError) return
        onSignIn(email!!, password!!)
    }

    private fun validateCredentials(email: String?, password: String?) {
        emailErrorLiveData.value = when {
            email.isNotEmail -> "Email incorrect format"
            else -> null
        }
        passwordErrorLiveData.value = when {
            password.isNullOrEmpty() -> "Password is non optional"
            else -> null
        }
    }

    private fun onSignIn(email: String, password: String) {
        SignInJob(email, password,
            onSuccess = {
                val user = it.user ?: throw NullPointerException()
                log.d("sign in with user provider: %s, uid: %s".format(user.providerId, user.uid))
            },
            onFailure = {
                log.d("sign in failure")
                passwordErrorLiveData.value = it.message
            })
            .signInWithEmailAndPassword()
    }

}
