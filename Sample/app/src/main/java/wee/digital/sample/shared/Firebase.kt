package wee.digital.sample.shared

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

val auth get() = Firebase.auth

val store get() = Firebase.firestore

val storeRef = Firebase.storage.reference

val userCollection get() = store.collection("users")

val currentUser get() = auth.currentUser






