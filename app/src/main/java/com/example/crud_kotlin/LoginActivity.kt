package com.example.crud_kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crud_kotlin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    //Inicializar firebase
    private lateinit var auth: FirebaseAuth

    //Binding
    private lateinit var binding:ActivityLoginBinding

    // Para referenciar la base de datos
    private lateinit var baseReferencia:DatabaseReference

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
    }
}