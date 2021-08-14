package wee.digital.sample.ui.job

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import wee.digital.sample.data.firebase.users
import wee.digital.widget.extension.isEmail
import wee.digital.widget.extension.normalizer

class SearchJob(
    private val onResult: (List<DocumentSnapshot>?) -> Unit
) {

    private var searchRegistration: ListenerRegistration? = null

    fun search(s: String) {
        searchRegistration?.remove()
        when {
            s.isEmail -> searchUserByEmail(s)
            s.isNotEmpty() -> searchUserByName(s)
        }
    }

    private fun searchUserByName(s: String) {
        searchRegistration = users
            .whereEqualTo("searchKey", s.normalizer())
            .addSnapshotListener { value, _ ->
                onResult(value?.documents)
            }
    }

    private fun searchUserByEmail(s: String) {
        searchRegistration = users
            .whereEqualTo("email", s)
            .addSnapshotListener { value, _ ->
                onResult(value?.documents)
            }
    }

}