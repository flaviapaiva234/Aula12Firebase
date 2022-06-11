package com.example.aula12firebase

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import com.example.aula12firebase.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var mSignIn = 123456
    private var mAuth: FirebaseAuth? = null

    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        //criar o metodo Request
        createRequest()

        binding.btnSignIn.setOnClickListener{
            signIn() //criar a função signIn
        }
    }

       private fun createRequest() {
        //Configura Google Sign In
        val googleSignInOp = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

           //Todos os dispositivos tem um token
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("Fetching FCM registration token failed")
                return@OnCompleteListener
            }

            val token = task.result

            Log.d("My token",token) // foi pedido para imprimir o token   // na parte inferior esquerda, run, precionar Ctrl +f para pesquisar o My token, copiar esse token e colar lá no site da firebase para enviar menssagens mesmo como app em uso
        })

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOp)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, mSignIn) //validação da senha
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //Vai validar a senha
        if (requestCode == mSignIn) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                //Se a validação ocorrer com sucesso, enãto vai autenticar com o Firebase
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account) // precisa criar esse método, eles tem na documentação da google, para validar o requestCode
                }
            } catch (e: ApiException) {
                //se der errado, vai passar essa menssagem de erro catch,
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT)
                    .show()  //e vai ter o Tost avisando que teve o erro
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount){  // está como parametro a conta que está fazendo o login
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null) // cria uma variavel credencial que é do provedor de Autenticação GoogleAuthProvider, e vai passar a credencial com o Token
        mAuth!!.signInWithCredential(credential)  // mAuth vai passar a credencial
            .addOnCompleteListener(  //vai passar o Listener para poder ouvir o que está vindo para ele
                this  // vai passar o contesto que é o this
            ) { task ->  // vai testar
                if (task.isSuccessful) {  //se der certo
                    //se a validação ocorrer com sucesso
                    val user = mAuth!!.currentUser  // vai dar  um upDdate com o usuário informado
                    val intent = Intent(applicationContext, ProfileActivity::class.java) // vai passar uma intent, e vai para a segunda activity que é a ProfileActivity
                    startActivity(intent) // da inicio a essa intenção
                } else{   // se não der certo
                    Toast.makeText(this, "Sorry auth failed. Please, try again!", Toast.LENGTH_SHORT) // vai dar mais um Toast curto com a menssagem
                        .show() // só mostra a menssagem

                }

            }
    }



}