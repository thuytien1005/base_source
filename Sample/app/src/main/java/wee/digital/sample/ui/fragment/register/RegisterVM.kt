package wee.digital.sample.ui.fragment.register

import com.google.firebase.auth.AuthResult
import wee.digital.library.extension.SingleLiveData
import wee.digital.sample.shared.auth
import wee.digital.sample.shared.userCollection
import wee.digital.sample.ui.model.StoreUser
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
        if (hasError) return
        val user = StoreUser().also {
            it.firstName = firstName!!
            it.lastName = lastName!!
            it.email = email!!
        }
        createUser(user, password!!)
    }

    private fun createUser(user: StoreUser, password: String) {
        onProgress {
            auth.createUserWithEmailAndPassword(user.email, password)
        }.addOnSuccessListener {
            user.uid = it.user!!.uid
            updateUser(user, password)
        }.addOnFailureListener {
            onRegisterFailure(it)
        }
    }

    private fun updateUser(user: StoreUser, password: String) {
        onProgress {
            userCollection.document(user.uid).set(user)
        }.addOnSuccessListener {
            signIn(user, password)
        }.addOnFailureListener {
            onRegisterFailure(it)
        }
    }

    private fun signIn(user: StoreUser, password: String) {
        onProgress {
            auth.signInWithEmailAndPassword(user.email, password)
        }.addOnSuccessListener {
            onRegisterSuccess(it)
        }.addOnFailureListener {
            onRegisterFailure(it)
        }
    }

    private fun onRegisterSuccess(result: AuthResult) {
        val user = result.user ?: throw NullPointerException()
        log.d("sign in with user provider: %s, uid: %s".format(user.providerId, user.uid))
    }

    private fun onRegisterFailure(e: Exception) {
        log.d("sign in failure")
        passwordErrorLiveData.value = e.message
    }

}
