package wee.digital.sample.ui.fragment.sign_in

import com.google.firebase.auth.AuthResult
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.vm.BaseVM
import wee.digital.widget.extension.isNotEmail

class SignInVM : BaseVM() {

    val emailErrorLiveData = SingleLiveData<String?>()

    val passwordErrorLiveData = SingleLiveData<String?>()

    private val hasError get() = null != emailErrorLiveData.value || null != passwordErrorLiveData.value

    fun signIn(email: String?, password: String?) {
        emailErrorLiveData.value = when {
            email.isNotEmail -> "Email incorrect format"
            else -> null
        }
        passwordErrorLiveData.value = when {
            password.isNullOrEmpty() -> "Password is non optional"
            else -> null
        }
        if (hasError) return
        onSignIn(email!!, password!!)
    }

    private fun onSignIn(email: String, password: String) {
        log.d("login with email: %s , password: %s".format(email, password))
        onProgress {
            auth.signInWithEmailAndPassword(email, password)
        }.addOnSuccessListener {
            onSignInSuccess(it)
        }.addOnFailureListener {
            onSignInFailure(it)
        }
    }

    private fun onSignInSuccess(result: AuthResult) {
        val user = result.user ?: throw NullPointerException()
        log.d("sign in with user provider: %s, uid: %s".format(user.providerId, user.uid))
    }

    private fun onSignInFailure(e: Exception) {
        log.d("sign in failure")
        passwordErrorLiveData.value = e.message
    }


}
