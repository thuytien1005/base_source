package wee.digital.sample.data.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import wee.digital.library.extension.parse
import wee.digital.sample.ui.model.StoreUser
import wee.digital.sample.utils.documentToJsObject

val currentUser get() = auth.currentUser

val auth get() = Firebase.auth

val userLogin: StoreUser
    get() {
        var user = StoreUser()
        var processing = false
        val data = userReference(auth.uid!!).get()
        while(!processing) {
            processing = data.isSuccessful
            if(!processing) continue
            user = data.result?.documentToJsObject().parse(StoreUser::class) ?: StoreUser()
        }
        return user
    }