package wee.digital.sample.data.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import wee.digital.library.extension.put
import wee.digital.sample.ui.model.StoreUser

object BackUp {

    private val store get() = Firebase.firestore

    private val conversationCollection get() = store.collection("conversation")

    private val chatCollection get() = store.collection("chat")

    private val userCollection get() = store.collection("users")

    fun getUser() {
        val list = mutableListOf<StoreUser>()
        userCollection.get().result?.documents?.forEach {
            list.add(StoreUser.from(it))
        }
        return
        println("list size %s".format(list.size))
    }

    fun getConversation() {
        val array = mutableListOf<JsonObject>()

        conversationCollection.get().result?.documents?.forEach {

            val colObj = JsonObject()

            val a = it.data;
            val obj = JsonObject()
            //obj.put("chatId",it.get(""))
            colObj.put(it.id, obj)


        }

        println("list size %s".format(array.size))
    }

    fun getChat() {
        val array = mutableListOf<JsonArray>()
        userCollection.get().result?.documents?.forEach {

        }
        println("list size %s".format(array.size))
    }

}