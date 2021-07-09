package wee.digital.sample.shared

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

val auth get() = FirebaseAuth.getInstance()

val fireStore get() = Firebase.firestore

val fireStorage = Firebase.storage.reference

val currentUser get() = auth.currentUser
