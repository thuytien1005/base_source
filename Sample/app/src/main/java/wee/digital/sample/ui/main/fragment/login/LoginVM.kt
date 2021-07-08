package wee.digital.sample.ui.main.fragment.login

import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.shared.auth
import wee.digital.sample.shared.currentUser
import wee.digital.sample.ui.main.vm.BaseVM
import wee.digital.widget.extension.isEmail
import wee.digital.widget.extension.notNullOrEmpty

class LoginVM : BaseVM() {

    val emailErrorLiveData = SingleLiveData<String?>()

    val passwordErrorLiveData = SingleLiveData<String?>()

    private val hasError get() = null != emailErrorLiveData.value || null != passwordErrorLiveData.value

    fun onLogin(email: String?, password: String?) {
        emailErrorLiveData.value = if (email.isEmail) null else "Email incorrect format"
        passwordErrorLiveData.value = if (password.notNullOrEmpty()) null else "Password is non optional"
        if (hasError) return
        login(email!!, password!!)
    }

    private fun login(email: String, password: String) {
        log.d("login with email: %s , password: %s".format(email, password))
        auth.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener {
                    when {
                        it.isSuccessful -> onLoginSuccess()
                        else -> onLoginFailure(it.exception!!)
                    }
                }
    }

    private fun onLoginSuccess() {
        log.d("onLoginSuccess with user providerId: %s".format(currentUser?.providerId))
    }

    private fun onLoginFailure(e: Exception) {
        log.d("onLoginFailure")
        when (e) {
            else -> passwordErrorLiveData.value = e.message
        }
    }


}
