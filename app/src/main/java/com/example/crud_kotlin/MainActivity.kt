package com.example.crud_kotlin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crud_kotlin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null){
            irDashboard()
        }

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener {

            if (firebaseAuth.currentUser != null){
                irDashboard()
            }
            else{
                startActivity(Intent(applicationContext,LoginActivity::class.java))
            }
        }

        binding.btnRegistrar.setOnClickListener {
            startActivity(Intent(applicationContext,RegistrarActivity::class.java))
        }

    }

    private fun irDashboard(){
        startActivity(Intent(applicationContext, DashboardActivity::class.java))
    }
}