package com.example.crud_kotlin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crud_kotlin.Modelos.Registro
import com.example.crud_kotlin.databinding.ActivityPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        firebaseAuth = FirebaseAuth.getInstance()
        val uid=firebaseAuth.currentUser?.uid



        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(uid!=null){
            val database=FirebaseDatabase.getInstance().reference
            binding.NombreUsuario.visibility=View.GONE
            binding.carreraUsuario.visibility=View.GONE
            binding.correoUsuario.visibility=View.GONE
            database.child("users").child(uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val usuario = snapshot.getValue(Registro::class.java)
                        usuario?.let {
                            // Ejemplo: mostrar en TextViews
                            binding.NombreUsuario.text= it.nombre + " " + it.apellido
                            binding.correoUsuario.text = it.email
                            binding.carreraUsuario.text = it.carrera
                            binding.NombreUsuario.visibility = View.VISIBLE
                            binding.carreraUsuario.visibility = View.VISIBLE
                            binding.correoUsuario.visibility = View.VISIBLE

                        }

                    }

                    else {
                        Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                    }
                }

                .addOnFailureListener {
                    Toast.makeText(this, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
                }
        }else{
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show()
        }

        // Botón cerrar sesión
        binding.cerrarSesion.setOnClickListener {
            cerrarSesion()
        }


    }

    private fun cerrarSesion() {
        if (firebaseAuth.currentUser != null) {
            firebaseAuth.signOut()
        }
        irAlLogin()
    }

    private fun irAlLogin() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
