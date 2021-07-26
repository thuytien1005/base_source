package wee.digital.sample.ui.usecase

import com.google.firebase.auth.AuthResult
import wee.digital.library.util.Logger
import wee.digital.sample.data.repository.auth
import wee.digital.sample.shared.onProgress

class SignInUseCase(
    private val email: String,
    private val password: String,
    private val onSuccess: (AuthResult) -> Unit,
    private val onFailure: (Exception) -> Unit
) {

    private val log by lazy { Logger(this::class) }

    fun signInWithEmailAndPassword() {
        log.d("sign in with email: %s , password: %s".format(email, password))
        onProgress {
            auth.signInWithEmailAndPassword(email, password)
        }.addOnSuccessListener(onSuccess)
            .addOnFailureListener(onFailure)
    }
}