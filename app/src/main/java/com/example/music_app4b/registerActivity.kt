package com.example.music_app4b

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class registerActivity : AppCompatActivity() {

    //Aqui se declaran los elementos
    private lateinit var etNombre : EditText
    private lateinit var etCorreo : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnRegistrar : Button

    //crear instancia del databasehelper
    private lateinit var dbHelper: DatabaseHelper

    //METODO PRINCIPAL
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        //inicializar los objetos de los componentes
        etNombre = findViewById(R.id.etNombre)
        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPass)
        btnRegistrar = findViewById(R.id.btnRegistrar)


        //inicializar la bd en un objeto
        dbHelper = DatabaseHelper(this)

        //evento para registrar al usuario
        btnRegistrar.setOnClickListener {
            //registrar user
            registrarUsuario()
        }






        val tvIniciarSesion = findViewById<TextView>(R.id.tvIniciarSesion)

        tvIniciarSesion.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)

        }


    }

    //METODOS POR AKI
    private fun registrarUsuario(){
        //variables para guardar los textos

        val nombre = etNombre.text.toString().trim()
        val correo = etCorreo.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }


        //variable q guarda el resultado del registor y llama a la bd
        val registro = dbHelper.registrarUsuario(nombre, correo, password)

        //validar que el resultado de la inserscion sea verdadero o falso
        if (registro){
            //si es correcto
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            //cierra la panatlla actual y regresa a la anterios
            finish()

            //PRUEBA DE GIT LOL
            //prueba de git xddd

        }else{
            Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
        }

    }
}