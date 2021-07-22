package wee.digital.sample.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import wee.digital.sample.shared.auth
import wee.digital.sample.ui.model.StoreUser
import wee.digital.widget.extension.normalizer


object StoreRepository {

    val store get() = Firebase.firestore

    val conversations get() = store.collection("conversations")

    val chats get() = store.collection("chats")

    val users get() = store.collection("users")

    val contacts get() = store.collection("contacts")

    private val contactsCollection by lazy { store.collection("contacts") }

    fun updateUser(user: StoreUser): Task<Void> {
        return this.users.document(user.uid).set(user)
    }

    fun userReference(uid: String): DocumentReference {
        return users.document(uid)
    }


    fun userSearch(searchText: String): Query {
        return users.whereEqualTo("searchKey", searchText.normalizer())
    }

    fun contactsReference(uid : String): DocumentReference{
        return contactsCollection.document(uid)
    }

    fun userArrayContainUid(listUid : List<String>): Query{
        return users.whereIn("uids", listUid)
    }

}