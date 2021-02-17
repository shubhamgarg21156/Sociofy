package com.example.sociofy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.sociofy.daos.UserDao
import com.example.sociofy.models.Users
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.auth.User
import com.squareup.okhttp.Dispatcher
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private val RC_SIGN_IN: Int = 123
    private val TAG = "Sign In Activity Tag"
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        SignInButton.setOnClickListener {
            signIn()
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser= auth.currentUser
        updateUI(currentUser)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            HandleSignInResult(task)
        }
    }

    private fun HandleSignInResult(task: Task<GoogleSignInAccount>?) {
        try {
            val account = task?.getResult(ApiException::class.java)!!
            Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {

            Log.w(TAG, "Google sign in failed", e)

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        progressbar.visibility = View.VISIBLE
        SignInButton.visibility = View.GONE
        GlobalScope.launch(Dispatchers.IO){

            val auth = auth.signInWithCredential(credential).await()
            val firebaseUser = auth.user
            withContext(Dispatchers.Main){

                updateUI(firebaseUser)
            }

        }
    }

    private fun updateUI(firebaseUser: FirebaseUser?) {
        if(firebaseUser!=null){
            val user   = Users(firebaseUser.uid,firebaseUser.displayName,firebaseUser.photoUrl.toString())
            UserDao().addUser(user)
            var signInIntent = Intent(this,MainActivity::class.java)
            startActivity(signInIntent)
            finish()
        }
        else{
            progressbar.visibility = View.GONE
            SignInButton.visibility = View.VISIBLE
        }

    }

}

