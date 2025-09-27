package com.example.crud_kotlin

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.SpinnerAdapter
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

    var carrerasUNAB: Array<String?> = arrayOf<String?>(
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

        //Metodo de carga de select
        val adapter = ArrayAdapter(
            this,  // Contexto (Activity)
            android.R.layout.simple_spinner_item, // Layout del item seleccionado
            carrerasUNAB
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCarrera.adapter = adapter

        loadFieldsForEndingRegister()


        // Apuntnado a cada ID del layout activity_registrar
        val etNombre = findViewById<EditText>(R.id.inputNombre)
        val etApellido = findViewById<EditText>(R.id.inputApellido)
        val etCorreo = findViewById<EditText>(R.id.inputMail)
        val etPassword = findViewById<EditText>(R.id.inputPass)
        val etCarrera = findViewById<Spinner>(R.id.spinnerCarrera)


        // Metodo del boton
        binding.botonRegistrar.setOnClickListener {

            val nombre=etNombre.text.toString().trim()
            val apellido=etApellido.text.toString().trim()
            val correo=etCorreo.text.toString().trim()
            val carrera=etCarrera.selectedItem.toString().trim()

            val contra=etPassword.text.toString().trim()
            val user = FirebaseAuth.getInstance().currentUser



            //Validando campos
            if(nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || carrera.isEmpty()){
                Toast.makeText(this, "¡Completa los campos vacios!", Toast.LENGTH_LONG).show()
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
                                    Toast.makeText(this, "Registro exitoso.", Toast.LENGTH_LONG).show()

                                    //Limpiar los campos
                                    etNombre.setText("")
                                    etApellido.setText("")
                                    etCorreo.setText("")
                                    etPassword.setText("")
                                    etCarrera.setSelection(0)

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



            enableInputFields()
        }
    }





    private fun loadFieldsForEndingRegister() {

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val uid = user.uid                 // ID único de usuario
            val email = user.email             // correo del usuario
            val displayName = user.displayName.toString() // nombre completo (solo si lo configuraste)
            val photoUrl = user.photoUrl       // URL de la foto de perfil
            val isEmailVerified = user.isEmailVerified

            val partes = displayName.trim().split(" ")

            val nombre = if (partes.isNotEmpty()) partes[0] else ""
            val apellido = if (partes.size > 1) partes.drop(1).joinToString(" ") else ""


            binding.inputNombre.setText(nombre)
            binding.inputApellido.setText(apellido)
            binding.inputMail.setText(email)
            disableInputFields()


        }



    }



    private fun disableInputFields() {

        binding.inputNombre.isEnabled = false
        binding.inputApellido.isEnabled = false
        binding.inputMail.isEnabled = false
        binding.inputPass.isEnabled = false


    }

    private fun enableInputFields() {

        binding.inputNombre.isEnabled = true
        binding.inputApellido.isEnabled = true
        binding.inputMail.isEnabled = true
        binding.inputPass.isEnabled = true
        binding.spinnerCarrera.isEnabled = true
    }


}