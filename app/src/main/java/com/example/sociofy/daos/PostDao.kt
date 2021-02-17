package com.example.sociofy.daos

import com.example.sociofy.models.Post
import com.example.sociofy.models.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PostDao {

    val db = FirebaseFirestore.getInstance()
    val postCollection = db.collection("posts")
    val auth = Firebase.auth

    fun addPost(text:String){
        val uid = auth.currentUser!!.uid
        GlobalScope.launch(Dispatchers.IO){
            val user = UserDao().getUserById(uid).await().toObject(Users::class.java)!!
            val currentTime = System.currentTimeMillis()
            val post = Post(text , user , currentTime)
            postCollection.document().set(post)
        }

    }

    fun getPostById(postId : String) : Task<DocumentSnapshot> {
        return postCollection.document(postId).get()
    }
    fun updateLikes(postId : String){

        GlobalScope.launch {

            val uid = auth.currentUser!!.uid
            val post = getPostById(postId).await().toObject(Post::class.java)!!
            val isLike = post.likedBy.contains(uid)

            if(isLike){
                post.likedBy.remove(uid)
            }
            else{
                post.likedBy.add(uid)
            }

            postCollection.document(postId).set(post)

        }
    }
}