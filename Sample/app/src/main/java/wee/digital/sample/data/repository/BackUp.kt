package wee.digital.sample.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import wee.digital.library.extension.obj
import wee.digital.library.extension.parse
import wee.digital.library.extension.readJsonFromAssets
import wee.digital.library.util.Logger
import wee.digital.sample.ui.model.StoreChat
import wee.digital.sample.ui.model.StoreContact
import wee.digital.sample.ui.model.StoreConversation
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.documentToJsObject

object Backup {

    val backupLog = Logger("backup")

    val restoreLog = Logger("restore")

    private val store get() = Firebase.firestore

    fun runBackupData() {
        val obj = JsonObject()
        runBackup("chats", obj) {
            runBackup("contacts", obj) {
                runBackup("conversations", obj) {
                    runBackup("users", obj) {
                        val o = obj
                        println("")
                    }
                }
            }
        }
    }

    private fun runBackup(refName: String, obj: JsonObject, onCompleted: () -> Unit) {
        val documentObj = JsonObject()
        val ref = store.collection(refName)
        ref.get().addOnSuccessListener { querySnapshot: QuerySnapshot ->
            querySnapshot.documents.forEach { documentSnapshot: DocumentSnapshot ->
                val obj = documentSnapshot.documentToJsObject()
                documentObj.add(documentSnapshot.id, obj)
            }
            obj.add(ref.id, documentObj)
            onCompleted()
        }
    }

    fun runRestoreData() {
        store.runBatch { batch: WriteBatch ->
            val dataJson = readJsonFromAssets("data.json")!!
            batch.runRestore(dataJson, "chats") {
                it.asJsonObject.parse(StoreChat::class)!!
            }
           /* batch.runRestore(dataJson,"contacts"){
                it.asJsonObject.parse(StoreContact::class)!!.toMap()
            }
            batch.runRestore(dataJson,"conversations"){
                it.asJsonObject.parse(StoreConversation::class)!!.toMap()
            }
            batch.runRestore(dataJson,"users"){
                it.asJsonObject.parse(StoreUser::class)!!.toMap()
            }*/
        }.addOnCompleteListener {

        }
    }

    fun <T : Any> WriteBatch.runRestore(obj: JsonObject, refName: String, block: (JsonElement) -> T) {
        val collectionRef = store.collection(refName)
        obj.obj(refName)?.entrySet()?.forEach {
            restoreLog.d("collection: $refName - document: ${it.key}")
            val documentRef = collectionRef.document(it.key)
            val a = block(it.value)
            val documentMap = a
            this.set(documentRef, documentMap)
        }
    }
}