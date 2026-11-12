package com.example.music_app4b

import android.content.Intent
import android.credentials.CredentialManager
import android.credentials.GetCredentialException
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var etCorreo : EditText
    private lateinit var etPassword : EditText
    private lateinit var btnIngresar : Button

    private lateinit var dbHelper : DatabaseHelper

    //crear objetos para el funcionamiento con google
    private lateinit var auth: FirebaseAuth
    private lateinit var credentialManager: androidx.credentials.CredentialManager
    private lateinit var btnGoogle : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val tvCrearCuenta = findViewById<TextView>(R.id.tvCrearCuenta)

        etCorreo = findViewById(R.id.etCorreo)
        etPassword = findViewById(R.id.etPass)
        btnIngresar = findViewById(R.id.btnIngresar)

        dbHelper = DatabaseHelper(this)

        btnGoogle = findViewById(R.id.btnGoogle)
        auth = FirebaseAuth.getInstance()
        credentialManager = androidx.credentials.CredentialManager.create(this)

        //Evento para iniciar sesion
        btnIngresar.setOnClickListener {
            //Llamar metodo para iniciar sesion
            iniciarSesion()
        }

        btnGoogle.setOnClickListener {
            iniciarSesionGoogle()
        }

        tvCrearCuenta.setOnClickListener {
            val intent = Intent(this, registerActivity::class.java)

            startActivity(intent)

        }


    }

    //Metodo para iniiar sesion
    private fun iniciarSesion(){
        val correo = etCorreo.text.toString().trim()
        val pass = etPassword.text.toString().trim()

        if (correo.isEmpty() || pass.isEmpty()){
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        //Llamar al metodo de inicio de sesion en la BD
        val resultado = dbHelper.iniciarSesion(correo, pass)

        //Validar si el incio de sesion es correcto
        if(resultado){
            //El resultado es verdadero
            Toast.makeText(this, "Bienvenido :D", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()

        }else{
            //El resultado es falso
            Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
        }

    }

    // 3. MÉTODO PARA AUTENTICAR EN FIRBASE
    private fun autenticaFirebase(idToken: String){

        val credencialFirebase = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credencialFirebase).addOnCompleteListener(this) {

                inicio ->

            //Valiadar si el inicio de sesion es correcto
            if(inicio.isSuccessful){

                //Crear variable para guardar el usuario actual
                val usuario = auth.currentUser

                Toast.makeText(this, "Bienvenido ${usuario?.displayName}", Toast.LENGTH_SHORT).show()

                //Enviar al usuario a home activity
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)

                finish()
            }else{
                Toast.makeText(this, "Error al iniciar con firebase", Toast.LENGTH_SHORT).show()

            }

        }

    }

    // 4. MÉTODO PARA PROCESAR LA CREDENCIAL
    private fun procesarCredencial(credential: Credential){

        if(credential is CustomCredential && credential.type ==
            GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){

            try {

                //Validar la credencial del proveedor
                val credencialGoogle = GoogleIdTokenCredential.createFrom(
                    credential.data
                )

                //Iniciar proceso de autenticacón
                autenticaFirebase(credencialGoogle.idToken)

            }catch (e : GoogleIdTokenParsingException){

                Toast.makeText(this, "Credencial invalida", Toast.LENGTH_SHORT).show()

            }
        }
    }

    // 5. MÉTODO PARA INICIAR SESIÓN
    private fun iniciarSesionGoogle(){

        val inicioGoogle = GetSignInWithGoogleOption.Builder(
            getString(R.string.default_web_client_id)).build()

        val solicitud = androidx.credentials.GetCredentialRequest.Builder()
            .addCredentialOption(inicioGoogle)
            .build()

        //Proceso para iniciar con Firebase
        lifecycleScope.launch {

            try {

                //Verificar que el resultado sea correcto
                val resultado = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = solicitud
                )

                //Llamar al metodo que permite procesar la credencial
                procesarCredencial(resultado.credential)

            }catch (e: androidx.credentials.exceptions.GetCredentialException){

                Toast.makeText(this@MainActivity, "Error al iniciar", Toast.LENGTH_SHORT).show()

            }

        }

    }
}