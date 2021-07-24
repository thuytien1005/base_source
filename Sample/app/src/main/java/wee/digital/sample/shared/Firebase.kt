package wee.digital.sample.shared

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import wee.digital.library.extension.parse
import wee.digital.sample.data.repository.StoreRepository
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.documentToJsObject

val auth get() = Firebase.auth

val storeRef = Firebase.storage.reference

var isDev = true

val currentUser get() = auth.currentUser

val userLogin: StoreUser
    get() {
        var user = StoreUser()
        var processing = false
        val data = StoreRepository.userReference(auth.uid.toString()).get()
        while (!processing) {
            processing = data.isSuccessful
            if (!processing) continue
            user = data.result?.documentToJsObject().parse(StoreUser::class) ?: StoreUser()
        }
        return user
    }





