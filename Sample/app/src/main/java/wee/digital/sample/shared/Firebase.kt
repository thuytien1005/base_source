package wee.digital.sample.shared

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

val auth get() = FirebaseAuth.getInstance()

val firestore get() = Firebase.firestore

val currentUser get() = auth.currentUser
