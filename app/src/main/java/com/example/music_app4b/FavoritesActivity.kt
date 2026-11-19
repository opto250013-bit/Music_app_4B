package com.example.music_app4b

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.PrepareGetCredentialResponse
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.music_app4b.model.albumFvorito
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() {
    private lateinit var bottomNavigation : BottomNavigationView

    private lateinit var auth : FirebaseAuth
    private lateinit var credentialManager: CredentialManager
    private lateinit var gridFavoritos : GridLayout
    private lateinit var tvEstadoFavoritos : TextView
    private lateinit var dbHelper : DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)


        bottomNavigation = findViewById(R.id.bottomNavigation) // segun esta qui el error
        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

        gridFavoritos = findViewById(R.id.gridFavoritos)
        tvEstadoFavoritos = findViewById(R.id.tvEstadoFavoritos)
        dbHelper = DatabaseHelper(this)

        menuNavegacion()
        consultarFavoritos()

    }

    override fun onResume(){
        super.onResume()
        consultarFavoritos()
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

    //METODO PARA CONSULTAR LA INFORMACION DESDE LA BD
    private fun mostrarAlbumesFavoritos(favoritos : List<albumFvorito>){

        //Ciclo para recorrer la consulta de la BD
        for (album in favoritos){

            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_favorito,
                gridFavoritos,
                false
            )

            val imgAlbum = vistaAlbum.findViewById<ImageView>(R.id.imgFavorito)

            Glide.with(this)
                .load(album.imagen)
                .centerCrop()
                .into(imgAlbum)

            //Evento para que el usuario acceda a los albumes individuales
            vistaAlbum.setOnClickListener {

                val intent = Intent(this, DetailsActivity::class.java)

                intent.putExtra("ID_ALBUM", album.albumId)
                intent.putExtra("NOMBRE_ALBUM", album.albumId)
                intent.putExtra("ARTISTA_ALBUM", album.albumId)
                intent.putExtra("GENERO_ALBUM", album.albumId)
                intent.putExtra("CANCIONES_ALBUM", album.albumId)
                intent.putExtra("FECHA_ALBUM", album.albumId)
                intent.putExtra("IMAGEN_ALBUM", album.albumId)

                startActivity(intent)
            }

            gridFavoritos

        }

    }

    //METODO PARA LA CONSULTA
    private fun consultarFavoritos(){

        val favoritos = dbHelper.obtenerAlbumesFavoritos()

        //Mostrar los albumes y sus imagenes en el GridLayout
        gridFavoritos.removeAllViews()

        //Validar que los elementos tengan un registro en la BD
        if (favoritos.isEmpty()){
            tvEstadoFavoritos.visibility = View.VISIBLE
            tvEstadoFavoritos.text = "No tienes ningun album en Favoritos"
        }else{
            tvEstadoFavoritos.visibility = View.GONE
            mostrarAlbumesFavoritos(favoritos)
        }

    }
}