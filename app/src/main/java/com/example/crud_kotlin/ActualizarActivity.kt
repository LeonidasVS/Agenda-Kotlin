package com.example.crud_kotlin

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
import com.example.crud_kotlin.databinding.ActivityActualizarBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ActualizarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityActualizarBinding
    private lateinit var fireAuth: FirebaseAuth
    private lateinit var db:FirebaseDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityActualizarBinding.inflate(layoutInflater)
        fireAuth=FirebaseAuth.getInstance()
        db=FirebaseDatabase.getInstance()

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        //Rellenar el spiner de carreras universitarias
        setupCarreraSpinner()

        //Metodo para cargar usuarios
        cargarUsuario()

        binding.botonActualizarCuenta.setOnClickListener {
            validarYactualizar()
        }
    }

    private var nombre = ""
    private var apellido = ""
    private var correo = ""
    private var carrera = ""

    private fun validarYactualizar() {
        validarCampos()
        val user = fireAuth.currentUser
        if (user != null) {
            val uid = user.uid
            val ref = db.getReference("users").child(uid)

            val userMap = mapOf(
                "nombre" to nombre,
                "apellido" to apellido,
                "email" to correo,
                "carrera" to carrera
            )

            // 🔹 Actualiza primero el correo en FirebaseAuth
            user.updateEmail(correo)
                .addOnSuccessListener {
                    // 🔹 Luego actualiza los datos en Realtime Database
                    ref.updateChildren(userMap)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, DashboardActivity::class.java)
                            // 🔹 Esto limpia el historial de Activities anteriores
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error al actualizar en la base de datos", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al actualizar el correo en FirebaseAuth", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun validarCampos() {
        nombre = binding.inputNombre.text.toString().trim()
        apellido = binding.inputApellido.text.toString().trim()
        correo = binding.inputMail.text.toString().trim()
        carrera = binding.spinnerCarrera.text.toString().trim()

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

            carrera == "-- Seleccione una carrera --" || carrera.isEmpty() -> {
                Toast.makeText(this, "Selecciona una carrera", Toast.LENGTH_SHORT).show()
                return
            }
        }
    }

    private fun cargarUsuario() {
        val user = fireAuth.currentUser
        if (user != null) {
            val uid = user.uid
            val ref = db.getReference("users").child(uid)

            ref.get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        nombre = snapshot.child("nombre").value.toString()
                        apellido = snapshot.child("apellido").value.toString()
                        correo = snapshot.child("email").value.toString()
                        carrera = snapshot.child("carrera").value.toString()

                        binding.inputNombre.setText(nombre)
                        binding.inputApellido.setText(apellido)
                        binding.inputMail.setText(correo)
                        binding.spinnerCarrera.setText(carrera, false)
                    } else {
                        Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
        }
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

        val adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            carreras
        )
        binding.spinnerCarrera.setAdapter(adapter)

        binding.spinnerCarrera.setOnClickListener {
            binding.spinnerCarrera.showDropDown()
        }
    }
}