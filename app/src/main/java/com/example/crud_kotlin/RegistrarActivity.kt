package com.example.crud_kotlin

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crud_kotlin.Modelos.Registro
import com.example.crud_kotlin.databinding.ActivityDashboardBinding
import com.example.crud_kotlin.databinding.ActivityRegistrarBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistrarActivity : AppCompatActivity() {

    // Variable para inicializar el binding
    private lateinit var binding: ActivityRegistrarBinding

    //Variable para iniciar FirebaseAuth
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupCarreraSpinner()

        // Apuntnado a cada ID del layout activity_registrar
        val etNombre = findViewById<EditText>(R.id.inputNombre)
        val etApellido = findViewById<EditText>(R.id.inputApellido)
        val etCorreo = findViewById<EditText>(R.id.inputMail)
        val etPassword = findViewById<EditText>(R.id.inputPass)
        val etCarrera = findViewById<EditText>(R.id.spinnerCarrera)

        // Metodo del boton
        binding.botonRegistrar.setOnClickListener {

            val nombre=etNombre.text.toString().trim()
            val apellido=etApellido.text.toString().trim()
            val correo=etCorreo.text.toString().trim()
            val carrera=etCarrera.text.toString().trim()
            val contra=etPassword.text.toString().trim()

            //Validando campos
            if(nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || carrera.isEmpty() || carrera=="-- Seleccione una carrera --"){
                Toast.makeText(this, "¡Completa los campos vacios!", Toast.LENGTH_SHORT).show()
            }else if(contra.length<6){
                Toast.makeText(this, "¡La contraseña debe tener mas de 6 digitos!", Toast.LENGTH_LONG).show()
            }else{

                // Creando usuario en FireBase
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
                                    Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_SHORT).show()

                                    //Limpiar los campos
                                    etNombre.setText("")
                                    etApellido.setText("")
                                    etCorreo.setText("")
                                    etPassword.setText("")
                                    etCarrera.setText("")

                                    val intent = Intent(this, DashboardActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error guardando: ${e.message}", Toast.LENGTH_LONG).show()
                                }

                        } else {
                            val error = task.exception?.message ?: "Error al registrar"
                            Toast.makeText(this, error, Toast.LENGTH_LONG).show()
                        }
                    }
            }
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

        val autoComplete = findViewById<AutoCompleteTextView>(R.id.spinnerCarrera)

        // Crear adapter
        val adapter = ArrayAdapter(
            this,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            carreras
        )
        autoComplete.setAdapter(adapter)

        // Configurar comportamiento como spinner
        autoComplete.setOnClickListener {
            autoComplete.showDropDown()
        }

        // Manejar selección
        autoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedCarrera = carreras[position]
            // Procesar selección
            Toast.makeText(this, "Seleccionado: $selectedCarrera", Toast.LENGTH_SHORT).show()
        }
    }
}