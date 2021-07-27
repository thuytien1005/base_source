package wee.digital.sample.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import wee.digital.sample.ui.model.StoreUser

val store get() = Firebase.firestore

val conversations get() = store.collection("conversations")

val chats get() = store.collection("chats")

val users get() = store.collection("users")

val contacts get() = store.collection("contacts")

val selfUserRef: DocumentReference get() = users.document(auth.uid!!)

val selfContactRef: DocumentReference get() = contacts.document(auth.uid!!)

val selfConversationRef: DocumentReference get() = conversations.document(auth.uid!!)

fun userReference(uid: String): DocumentReference {
    return users.document(uid)
}

fun updateUser(user: StoreUser): Task<Void> {
    return userReference(user.uid).set(user)
}

fun userByUids(uids: List<String>): Query {
    return users.whereIn("uid", uids)
}

fun conversationByUid(listUid: List<String>): Query {
    return chats.whereEqualTo("recipients", listUid)
}