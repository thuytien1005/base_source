package wee.digital.sample.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import wee.digital.library.extension.put
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.documentToJsObject

object BackUp {

    private val store get() = Firebase.firestore

    private val conversationCollection get() = store.collection("conversation")

    private val chatCollection get() = store.collection("chat")

    private val userCollection get() = store.collection("users")

    fun getUser() {
        userCollection.get().addOnSuccessListener { querySnapshot: QuerySnapshot ->
            val collectionObj = JsonObject()
            querySnapshot.documents.forEach { documentSnapshot: DocumentSnapshot ->
                val obj = documentSnapshot.documentToJsObject()
                collectionObj.add(documentSnapshot.id, obj)
            }
            println(collectionObj.toString())
        }

    }

    fun getConversation() {
        conversationCollection.get().addOnSuccessListener { querySnapshot: QuerySnapshot ->
            val collectionObj = JsonObject()
            querySnapshot.documents.forEach { documentSnapshot: DocumentSnapshot ->
                val obj = documentSnapshot.documentToJsObject()
                collectionObj.add(documentSnapshot.id, obj)
            }
            println(collectionObj.toString())
        }
    }

    fun getChat() {
        chatCollection.get().addOnSuccessListener { querySnapshot: QuerySnapshot ->
            val collectionObj = JsonObject()
            querySnapshot.documents.forEach { documentSnapshot: DocumentSnapshot ->
                val obj = documentSnapshot.documentToJsObject()
                collectionObj.add(documentSnapshot.id, obj)
            }
            println(collectionObj.toString())
        }
    }

}