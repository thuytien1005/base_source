package wee.digital.sample.ui.usecase

import com.google.firebase.firestore.FieldValue
import wee.digital.sample.data.repository.selfContactRef

class AddContactUseCase(
    private val uid: String,
    private val onSuccess: () -> Unit,
    private val onFailure: (Exception) -> Unit
) {

    private val mapUid = HashMap<String, Any>().apply {
        put("uids", FieldValue.arrayUnion(uid))
    }

    fun insertContact() {
        selfContactRef.get()
            .addOnSuccessListener {
                when {
                    it.exists() -> updateContact()
                    else -> setContact()
                }
            }
            .addOnFailureListener(onFailure)
    }

    private fun updateContact() {
        selfContactRef.update(mapUid)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onFailure)
    }

    private fun setContact() {
        selfContactRef.set(mapUid)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener(onFailure)
    }
}