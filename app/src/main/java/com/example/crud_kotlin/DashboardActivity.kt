package com.example.crud_kotlin

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crud_kotlin.Fragmentos.FragmentCalendario
import com.example.crud_kotlin.Fragmentos.FragmentContacto
import com.example.crud_kotlin.Fragmentos.FragmentRecordatorio
import com.example.crud_kotlin.Modelos.Registro
import com.example.crud_kotlin.databinding.ActivityDashboardBinding
import com.example.crud_kotlin.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlin.random.Random

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityDashboardBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bottomNV.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.item_nota->{
                    verFragmentoNotas()
                    true

                }

                R.id.item_recordatorios->{
                    verFragmentoRecordatorio()
                    true

                }

                R.id.item_contactos->{
                    verFragmentoContacto()
                    true

                }

                R.id.item_calendario->{
                    verFragmentoCalendario()
                    true

                }
                else->{
                    false
                }
            }
        }

        binding.btnConfiguracion.setOnClickListener {
            startActivity(Intent(applicationContext, PerfilActivity::class.java))
        }

        binding.tvBtnConfiguracion.setOnClickListener {
            startActivity(Intent(applicationContext, PerfilActivity::class.java))
        }


        //Metodo para letras del usuario como google
        circulName()



    }



    private fun circulName() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user?.photoUrl != null) {
            user.photoUrl?.let { uri ->
                Picasso.get().load(uri).into(binding.btnConfiguracion)
                binding.tvBtnConfiguracion.visibility = View.GONE
            }
        } else {
            val nombre = when {
                !user?.displayName.isNullOrEmpty() -> user?.displayName?.first().toString().uppercase()
                !user?.email.isNullOrEmpty() -> user?.email?.first().toString().uppercase()
                else -> "U"
            }

            binding.tvBtnConfiguracion.text = nombre
            binding.tvBtnConfiguracion.setTextColor(Color.WHITE)

            val r = Random.nextInt(50, 256)
            val g = Random.nextInt(50, 256)
            val b = Random.nextInt(50, 256)
            val color = Color.rgb(r, g, b)

            val shape = GradientDrawable()
            shape.shape = GradientDrawable.OVAL
            shape.setColor(color)
            binding.tvBtnConfiguracion.background = shape
        }
    }













    private fun verFragmentoNotas(){
        binding.tvTitulo.text = "StudyPlanner"

        //Volver a la dashboard principal
        val intent = Intent(this, DashboardActivity::class.java).apply {
            // Si ya existe una instancia en la pila, la trae y limpia la de encima
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        startActivity(intent)
        true
    }

    private fun verFragmentoRecordatorio(){
        binding.tvTitulo.text = "Recordatorios"

        val fragment_recordatorio = FragmentRecordatorio()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment_recordatorio, "Recordatorios")
        fragmentTransaction.commit()
    }

    private fun verFragmentoContacto(){
        binding.tvTitulo.text = "Contacto"

        val fragment_contacto = FragmentContacto()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment_contacto, "Frgment PErfil")
        fragmentTransaction.commit()
    }

    private fun verFragmentoCalendario(){
        binding.tvTitulo.text = "Calendario"

        val fragment_calendario = FragmentCalendario()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.fragmentoFL.id, fragment_calendario, "Frgment PErfil")
        fragmentTransaction.commit()
    }
}