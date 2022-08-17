package wee.digital.sample.data.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

val currentUser get() = auth.currentUser

val auth get() = Firebase.auth


