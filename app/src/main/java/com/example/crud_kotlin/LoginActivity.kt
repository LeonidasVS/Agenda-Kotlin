package com.example.crud_kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crud_kotlin.Modelos.Registro
import com.example.crud_kotlin.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    //Inicializar firebase
    private lateinit var auth: FirebaseAuth

    //Binding
    private lateinit var binding:ActivityLoginBinding

    // Para referenciar la base de datos
    private lateinit var baseReferencia:DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // Código para identificar la respuesta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Lamando las variables
        auth=FirebaseAuth.getInstance()
        binding=ActivityLoginBinding.inflate(layoutInflater)
        baseReferencia = FirebaseDatabase.getInstance().getReference("users")

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Apuntando y obteniendo el id de los input del login
        val etEmail=findViewById<EditText>(R.id.inputCorreo)
        val etPassword=findViewById<EditText>(R.id.inputPassword)

        // Boton logearse
        binding.btnLogin.setOnClickListener {
            val mail=etEmail.text.toString().trim()
            val password=etPassword.text.toString().trim()

            //Validar que no hayan campos vacios
            if(mail.isEmpty() || password.isEmpty()){
                Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
            }
            else{

                //Buscar en la base de datos o en el metodo de mail y contraseña de FireBase
                auth.signInWithEmailAndPassword(mail, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                            // Obtener datos del usuario desde Realtime Database
                            baseReferencia.child(uid).get()
                                .addOnSuccessListener { snapshot ->
                                    if (snapshot.exists()) {
                                        // Ir al Dashboard
                                        val intent = Intent(this, DashboardActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
                                }

                        } else {
                            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }


        //Metodo del inicio de session con google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // tu web client ID de Firebase
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        binding.btnLoginGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        }






    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error Google Sign-In: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Usuario logueado
                    val user = FirebaseAuth.getInstance().currentUser
                    val uid = user?.uid
                    val database = FirebaseDatabase.getInstance().reference.child("users")
                    if (uid != null) {
                        val registro = Registro(
                            nombre = user.displayName.toString(),
                            email = user.email.toString()
                        )
                        database.child(uid).setValue(registro)
                    }

                    checkEmailExists(user?.email.toString()) { exists, error ->
                        if (error != null) {
                            // manejar error
                        } else if (exists) {
                            // el correo ya está registrado
                            // Ir al Dashboard
                            startActivity(Intent(this, DashboardActivity::class.java))
                            Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                            finish()

                        } else {
                            // el correo NO está registrado
                            // Ir al registro para terminar el registro
                            startActivity(Intent(this, RegistrarActivity::class.java))
                            Toast.makeText(this, "Debes Completar el registro", Toast.LENGTH_SHORT).show()
                            finish()
                        }

                    }

                } else {
                    Toast.makeText(this, "Error Firebase Auth Google", Toast.LENGTH_SHORT).show()
                }
            }
    }


    fun checkEmailExists(email: String, onResult: (exists: Boolean, errorMsg: String?) -> Unit) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    val exists = !signInMethods.isNullOrEmpty()
                    onResult(exists, null)
                } else {
                    onResult(false, task.exception?.localizedMessage ?: "Error desconocido")
                }
            }
    }



}