package wee.digital.sample.repository

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import wee.digital.sample.repository.model.RegisterData


object CloudRepository {

    private val db = Firebase.firestore

    fun addUser(user : RegisterData){
        db.collection("users")
            .add(user)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }

}