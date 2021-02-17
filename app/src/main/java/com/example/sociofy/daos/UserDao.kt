package com.example.sociofy.daos

import com.example.sociofy.models.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class UserDao {

    val db = FirebaseFirestore.getInstance()
    val usersCollection = db.collection("users")

    fun addUser(user: Users?){
        user?.let {
           GlobalScope.launch (Dispatchers.IO){
               usersCollection.document(user.uid).set(it)
           }

        }
    }

    fun getUserById(userId : String) : Task<DocumentSnapshot> {
        val user = usersCollection.document(userId).get()
        return user
    }

}