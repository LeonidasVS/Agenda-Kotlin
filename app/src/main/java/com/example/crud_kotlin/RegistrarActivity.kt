package com.example.crud_kotlin

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crud_kotlin.Modelos.Registro
import com.example.crud_kotlin.databinding.ActivityRegistrarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrarActivity : AppCompatActivity() {

    // Binding
    private lateinit var binding: ActivityRegistrarBinding
    private lateinit var progressDialog: ProgressDialog

    // Firebase Auth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Instancia de ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupCarreraSpinner()

        // Botón Registrar
        binding.botonRegistrar.setOnClickListener {
            validarDatos()
        }
    }

    private var nombre = ""
    private var apellido = ""
    private var correo = ""
    private var carrera = ""
    private var contra = ""

    private fun validarDatos() {
        nombre = binding.inputNombre.text.toString().trim()
        apellido = binding.inputApellido.text.toString().trim()
        correo = binding.inputMail.text.toString().trim()
        carrera = binding.spinnerCarrera.text.toString().trim()
        contra = binding.inputPass.text.toString().trim()

        when {
            nombre.isEmpty() -> {
                binding.inputNombre.error = "Ingrese un Nombre"
                binding.inputNombre.requestFocus()
                return
            }
            apellido.isEmpty() -> {
                binding.inputApellido.error = "Ingrese un Apellido"
                binding.inputApellido.requestFocus()
                return
            }
            correo.isEmpty() -> {
                binding.inputMail.error = "Ingrese un Correo Electrónico"
                binding.inputMail.requestFocus()
                return
            }
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> {
                binding.inputMail.error = "Correo inválido"
                binding.inputMail.requestFocus()
                return
            }
            contra.isEmpty() || contra.length < 6 -> {
                binding.inputPass.error = "La contraseña debe tener al menos 6 caracteres"
                binding.inputPass.requestFocus()
                return
            }
            carrera == "-- Seleccione una carrera --" || carrera.isEmpty() -> {
                Toast.makeText(this, "Selecciona una carrera", Toast.LENGTH_SHORT).show()
                return
            }
        }

        // Mostrar progress mientras se registra
        progressDialog.setMessage("Creando usuario...")
        progressDialog.show()

        // Crear usuario en FirebaseAuth
        auth.createUserWithEmailAndPassword(correo, contra)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val uid = firebaseUser?.uid ?: return@addOnCompleteListener

                    val user = Registro(nombre, apellido, correo, carrera)

                    // Guardar en Realtime Database
                    val dbRef = FirebaseDatabase.getInstance().getReference("users")
                    dbRef.child(uid).setValue(user)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            limpiarCampos()
                            registroExitoso()
                        }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Toast.makeText(this, "Error guardando: ${e.message}", Toast.LENGTH_LONG).show()
                        }

                } else {
                    progressDialog.dismiss()
                    val error = task.exception?.message ?: "Error al registrar"
                    Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun limpiarCampos() {
        binding.inputNombre.setText("")
        binding.inputApellido.setText("")
        binding.inputMail.setText("")
        binding.inputPass.setText("")
        binding.spinnerCarrera.setText("-- Seleccione una carrera --", false)
    }

    private fun registroExitoso() {
        Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupCarreraSpinner() {
        val carreras = arrayOf(
            "-- Seleccione una carrera --",
            "Licenciatura en Psicología",
            "Licenciatura en Nutrición",
            "Licenciatura en Trabajo Social",
            "Licenciatura en Enfermería",
            "Tecnólogo en Enfermería",
            "Técnico en Enfermería",
            "Técnico en Optometría",
            "Técnico en Mercadeo",
            "Técnico en Idioma Inglés",
            "Técnico en Computación",
            "Técnico en Contabilidad",
            "Técnico en Diseño Gráfico",
            "Ingeniería en Sistemas y Computación"
        )

        val autoComplete = findViewById<AutoCompleteTextView>(R.id.spinnerCarrera)

        val adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            carreras
        )
        autoComplete.setAdapter(adapter)

        autoComplete.setOnClickListener {
            autoComplete.showDropDown()
        }
    }
}
