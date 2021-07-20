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

    private val store get() = Firebase.firestore

    private val conversationCollection get() = store.collection("conversation")

    private val userCollection get() = store.collection("users")

    private val contactsCollection by lazy { store.collection("contacts") }

    fun updateUser(user: StoreUser): Task<Void> {
        user.searchKey = "%s %s".format(user.firstName, user.lastName.toString()).normalizer() ?: ""
        return userCollection.document(user.uid).set(user)
    }

    fun userReference(uid: String = auth.uid.toString()): DocumentReference {
        return userCollection.document(uid)
    }

    fun userSearch(searchText: String): Query {
        return userCollection.whereEqualTo("searchKey", searchText.normalizer())
    }

    fun contactsReference(uid : String): DocumentReference{
        return contactsCollection.document(uid)
    }

    fun userArrayContainUid(listUid : List<String>): Query{
        return userCollection.whereIn("uid", listUid)
    }

}