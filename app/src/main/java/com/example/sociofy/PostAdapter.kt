package com.example.sociofy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sociofy.daos.PostDao
import com.example.sociofy.models.Post
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class PostAdapter(options: FirestoreRecyclerOptions<Post>,val listener: IPostAdapter) : FirestoreRecyclerAdapter<Post, PostAdapter.PostViewHolder>(
    options
) {

    class PostViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val userImage : ImageView = itemView.findViewById<ImageView>(R.id.userImage)
        val postTitle : TextView = itemView.findViewById<TextView>(R.id.postTitle)
        val likeCount : TextView = itemView.findViewById<TextView>(R.id.likeCount)
        val likeButton: ImageView = itemView.findViewById<ImageView>(R.id.likeButton)
        val userName : TextView = itemView.findViewById<TextView>(R.id.userName)
        val createdAt : TextView = itemView.findViewById<TextView>(R.id.createdAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
       val viewHolder = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_post,parent,false))
//        viewHolder.likeButton.setOnClickListener {
//            //PostDao().updateLikes()
//            listener.onLikeClicked(snapshots.getSnapshot(viewHolder.adapterPosition).id)
//        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {

        holder.userName.text = model.createdBy.displayName
        holder.postTitle.text = model.text
        holder.likeCount.text = model.likedBy.size.toString()
        Glide.with(holder.userImage.context).load(model.createdBy.photoUrl).circleCrop().into(holder.userImage)
        holder.createdAt.text = Utils.getTimeAgo(model.createdAt)

        val currentuserId = Firebase.auth.currentUser!!.uid
        val isLiked = model.likedBy.contains(currentuserId)
        if(isLiked){
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_liked))
        }
        else{
            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_unliked))
        }

        holder.likeButton.setOnClickListener {
            PostDao().updateLikes(snapshots.getSnapshot(holder.adapterPosition).id)
        }
    }
}

interface IPostAdapter{
    fun onLikeClicked(postID : String)
}