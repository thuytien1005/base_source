package wee.digital.sample.shared

import com.google.firebase.auth.FirebaseAuth

val auth get() = FirebaseAuth.getInstance()

val currentUser get() = auth.currentUser
