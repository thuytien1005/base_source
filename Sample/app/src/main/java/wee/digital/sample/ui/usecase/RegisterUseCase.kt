package wee.digital.sample.ui.usecase

import com.google.firebase.auth.AuthResult
import wee.digital.library.util.Logger
import wee.digital.sample.data.repository.auth
import wee.digital.sample.data.repository.updateUser
import wee.digital.sample.shared.onProgress
import wee.digital.sample.ui.model.StoreUser

class RegisterUseCase(
    private val user: StoreUser,
    private val password: String,
    private val onSuccess: (AuthResult) -> Unit,
    private val onFailure: (Exception) -> Unit
) {

    private val log by lazy { Logger(this::class.java.name) }

    fun createUserWithEmailAndPassword() {
        log.d("createUser")
        onProgress {
            auth.createUserWithEmailAndPassword(user.email!!, password)
        }.addOnSuccessListener {
            user.uid = it.user!!.uid
            insertUserDocument()
        }.addOnFailureListener(onFailure)
    }

    private fun insertUserDocument() {
        log.d("updateUserData")
        onProgress {
            updateUser(user)
        }.addOnSuccessListener {
            signInUser()
        }.addOnFailureListener(onFailure)
    }

    private fun signInUser() {
        onProgress {
            auth.signInWithEmailAndPassword(user.email!!, password)
        }.addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }

}