package com.example.aula12firebase

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.aula12firebase.databinding.ActivityProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null

    private lateinit var binding: ActivityProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = binding.tvName
        val email2 = binding.tvEmail

        val acct = GoogleSignIn.getLastSignedInAccount(this)  //verificar essa conta

        if (acct != null) {
            name.text = acct.displayName
            email2.text = acct.email

            binding.btnGoToSignIn.setOnClickListener {

                FirebaseAuth.getInstance().signOut()
                val intent = Intent(
                    applicationContext,
                    MainActivity::class.java
                )  // intensão de sair de conta, vai voltat para a MainActyvity
                startActivity(intent)
            }

            binding.btnErro.setOnClickListener {

                throw Exception("Deu erro")
            }

            binding.btnLogOut.setOnClickListener {

                mGoogleSignInClient?.signOut()
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(
                    applicationContext,
                    MainActivity::class.java
                )  // intensão de sair de conta, vai voltat para a MainActyvity
                startActivity(intent)
            }

            val googleSignInOp = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOp)

        }
    }
}