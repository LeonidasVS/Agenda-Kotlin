package com.example.crud_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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

        // Botón Login
        binding.btnLogin.setOnClickListener {
            validarUsuario()
        }
    }

    private var password=""
    private var correo=""

    private fun validarUsuario(){

        correo=binding.inputCorreo.text.toString().trim()
        password=binding.inputPassword.text.toString().trim()

        if (correo.isEmpty()) {
            binding.inputCorreo.error = "Ingrese un Correo Electrónico"
            binding.inputCorreo.requestFocus()
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.inputCorreo.error = "Ingrese un Correo Electronico Valido"
            binding.inputCorreo.requestFocus()
            return
        }else if (password.length < 6) {
            binding.inputPassword.error = "Ingrese una contraseña de al menos 6 caracteres"
            binding.inputPassword.requestFocus()
            return
        }

        // Autenticación en Firebase
        auth.signInWithEmailAndPassword(correo, password)
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