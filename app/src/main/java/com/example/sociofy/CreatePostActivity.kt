package com.example.sociofy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sociofy.daos.PostDao
import kotlinx.android.synthetic.main.activity_create_post.*

class CreatePostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        createPost.setOnClickListener {
            val text = postInput.text.toString().trim()
            if(!text.isEmpty()){
               PostDao().addPost(text)
                finish()
            }
        }
    }
}