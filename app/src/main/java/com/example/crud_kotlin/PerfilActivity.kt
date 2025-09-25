package com.example.crud_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crud_kotlin.Modelos.Registro
import com.example.crud_kotlin.databinding.ActivityPerfilBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPerfilBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)




        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        if (uid != null) {
            val database = FirebaseDatabase.getInstance().reference
            binding.tvNombre.visibility = View.GONE
            binding.tvEmail.visibility = View.GONE
            binding.tvCarrera.visibility = View.GONE
            database.child("users").child(uid).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val usuario = snapshot.getValue(Registro::class.java)
                        usuario?.let {
                            // Ejemplo: mostrar en TextViews
                            binding.tvNombre.text= it.nombre
                            binding.tvEmail.text = it.email
                            binding.tvCarrera.text = it.carrera
                            binding.tvNombre.visibility = View.VISIBLE
                            binding.tvEmail.visibility = View.VISIBLE
                            binding.tvCarrera.visibility = View.VISIBLE

                        }
                        val user = FirebaseAuth.getInstance().currentUser
                        user?.photoUrl?.let { uri ->
                            Picasso.get().load(uri).into(binding.imgPerfil)
                        }

                    } else {
                        Toast.makeText(this, "No se encontraron datos del usuario", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No hay usuario logueado", Toast.LENGTH_SHORT).show()
        }


        binding.btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }




    }




    fun cerrarSesion() {
        // Cierra sesión de Firebase Auth
        FirebaseAuth.getInstance().signOut()

        // Si también usas Google Sign-In, cierra sesión allí
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut()

        // Redirige al LoginActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }





}