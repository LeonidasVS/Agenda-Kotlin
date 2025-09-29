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

    // Inicializar Firebase
    private lateinit var auth: FirebaseAuth

    // Binding
    private lateinit var binding: ActivityLoginBinding

    // Referencia a la base de datos
    private lateinit var baseReferencia: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializaciones
        auth = FirebaseAuth.getInstance()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        baseReferencia = FirebaseDatabase.getInstance().getReference("users")

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inputs
        val etEmail = findViewById<EditText>(R.id.inputCorreo)
        val etPassword = findViewById<EditText>(R.id.inputPassword)

        // Botón Login
        binding.btnLogin.setOnClickListener {
            val mail = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validar que no estén vacíos
            if (mail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Autenticación en Firebase
            auth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: return@addOnCompleteListener

                        // Obtener datos del usuario desde la base
                        baseReferencia.child(uid).get()
                            .addOnSuccessListener { snapshot ->
                                if (snapshot.exists()) {
                                    // ✅ Usuario válido → Ir al Dashboard
                                    Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, DashboardActivity::class.java)
                                    // 🔹 Esto limpia el historial de Activities anteriores
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
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