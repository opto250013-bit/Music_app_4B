package com.example.music_app4b

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {
    private lateinit var bottomNavigation : BottomNavigationView

    private lateinit var auth : FirebaseAuth
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)


        bottomNavigation = findViewById(R.id.bottomNavigation) // segun esta qui el error
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)



    }

    private fun menuNavegacion(){

        //Verificar la opcion seleccionada del menu
        bottomNavigation.selectedItemId = R.id.nav_favoritos

        bottomNavigation.setOnItemSelectedListener { item ->

            when(item.itemId){

                R.id.nav_inicio -> {
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    true
                }

                R.id.nav_favoritos -> {
                    true
                }

                R.id.nav_salir ->{
                    cerrarSesionGoogle()
                    true
                }

                else -> false
            }

        }

    }

    private fun cerrarSesionGoogle(){
        auth.signOut()

        lifecycleScope.launch {

            try {

                credentialManager.clearCredentialState(
                    ClearCredentialStateRequest()
                )

            } catch (e : Exception){
                Toast.makeText(this@FavoritesActivity, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
            }

            val intent = Intent(this@FavoritesActivity, MainActivity::class.java)

            //Configurar el Intent para que inicie la app desde cero
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }
}