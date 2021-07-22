package wee.digital.sample.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.JsonObject
import wee.digital.library.extension.obj
import wee.digital.library.extension.parse
import wee.digital.library.extension.readJsonFromAssets
import wee.digital.library.util.Logger
import wee.digital.sample.ui.model.*
import wee.digital.sample.utils.documentToJsObject
import kotlin.reflect.KClass

object Backup {

    private val log = Logger("Backup")

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
            batch.runRestore(dataJson, "chats") { StoreChat::class }
            batch.runRestore(dataJson, "contacts") { StoreContact::class }
            batch.runRestore(dataJson, "conversations") { StoreConversation::class }
            batch.runRestore(dataJson, "users") { StoreUser::class }
        }
    }

    private fun <T : ToMap> WriteBatch.runRestore(obj: JsonObject, refName: String, block: () -> KClass<T>) {
        val collectionRef = store.collection(refName)
        obj.obj(refName)?.entrySet()?.forEach {
            log.d("collection: $refName - document: ${it.key}")
            val documentRef = collectionRef.document(it.key)
            it.value.asJsonObject.parse(block())?.also { any ->
                this.set(documentRef, any.toMap())
            }
        }

    }

}