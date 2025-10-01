package com.example.crud_kotlin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crud_kotlin.Fragmentos.FragmentCalendario
import com.example.crud_kotlin.Fragmentos.FragmentContacto
import com.example.crud_kotlin.Fragmentos.FragmentRecordatorio
import com.example.crud_kotlin.Objetos.Avatar
import com.example.crud_kotlin.databinding.ActivityDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding:ActivityDashboardBinding
    //private lateinit var auth:FirebaseAuth

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

        ObtenerLetra()

        binding.btnPerfil.setOnClickListener {
            startActivity(Intent(this, PerfilActivity::class.java))
        }
    }

    private fun ObtenerLetra() {
        val user = FirebaseAuth.getInstance().currentUser

        // Obtener la primera letra
        val displayName = user?.email
        Avatar.letra = displayName?.firstOrNull()?.toString()?.uppercase() ?: "?"
        binding.btnPerfil.text = Avatar.letra

        // Generar color aleatorio solo si no existe
        if (Avatar.color == null) {
            val random = java.util.Random()
            Avatar.color = android.graphics.Color.rgb(
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256)
            )
        }

        // Crear un drawable circular con el color del singleton
        val drawable = android.graphics.drawable.GradientDrawable()
        drawable.shape = android.graphics.drawable.GradientDrawable.OVAL
        drawable.setColor(Avatar.color!!)

        // Asignar el drawable como fondo del TextView
        binding.btnPerfil.background = drawable

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