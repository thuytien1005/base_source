package wee.digital.sample.ui.job

import com.google.firebase.firestore.FieldValue
import wee.digital.sample.data.firebase.selfContactRef

class ContactAddJob(
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