package com.example.music_app4b

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import org.w3c.dom.Text

class DetailsActivity : AppCompatActivity() {

    private lateinit var imgPortadaDetalle : ImageView
    private lateinit var btnFavorito : ImageButton
    private lateinit var tvNombreAlbumDetalle : TextView
    private lateinit var tvArtistaAlbumDetalle : TextView
    private lateinit var tvAlbumDetalle : TextView
    private lateinit var db : DatabaseHelper

    private var albumFavorito = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_details)

        imgPortadaDetalle = findViewById(R.id.imgPortadaDetalle)
        btnFavorito = findViewById(R.id.btnFavorito)
        tvNombreAlbumDetalle = findViewById(R.id.tvNombreAlbumDetalle)
        tvArtistaAlbumDetalle = findViewById(R.id.tvArtistaAlbumDetalle)
        tvAlbumDetalle = findViewById(R.id.tvAlbumDetalle)
        db = DatabaseHelper(this)

        mostrarInformacionAlbum()



    }

    //METODO PARA MOSTRAR LA INFORMACION DE UN ALBUM
    private fun mostrarInformacionAlbum(){

        val idAlbum = intent.getLongExtra("ID_ALBUM", 1)
        val nombreAlbum = intent.getStringExtra("NOMBRE_ALBUM" ) ?: "Album sin nombre"
        val artista = intent.getStringExtra("ARTISTA_ALBUM") ?: "Artista desconocido"
        val genero= intent.getStringExtra("GENERO_ALBUM") ?: "Genero desconocido"
        val canciones = intent.getIntExtra("CANCIONES_ALBUM", 1)
        val fecha = intent.getStringExtra("FECHA_ALBUM") ?: "Fecha desconocida"
        val imagen = intent.getStringExtra("IMAGEN_ALBUM") ?: ""

        tvNombreAlbumDetalle.text = nombreAlbum
        tvArtistaAlbumDetalle.text = artista
        tvAlbumDetalle.text = genero + " - " + "$canciones canciones"

        Glide.with(this)
            .load(imagen)
            .into(imgPortadaDetalle)

        //Verificar que el album exista como favorito
        if (idAlbum != -1L){
            albumFavorito = db.existeAlbumFavorito(idAlbum)
        }

        btnFavorito.setOnClickListener {

            // albumFavorito = !albumFavorito

            if (albumFavorito){
                //btnFavorito.setImageResource(R.drawable.favorito)
                //Toast.makeText(this, "Album $nombreAlbum agregado a favoritos", Toast.LENGTH_SHORT).show()

                btnFavorito.setImageResource(R.drawable.no_favorito)
                val eliminar = db.eliminarAlbumFavorito(idAlbum)

                if (eliminar){
                    albumFavorito = !albumFavorito
                    Toast.makeText(this, "Album $nombreAlbum eliminado de favoritos", Toast.LENGTH_SHORT).show()
                }

            }else{
                //btnFavorito.setImageResource(R.drawable.no_favorito)
                //Toast.makeText(this, "Album $nombreAlbum eliminado de favoritos", Toast.LENGTH_SHORT).show()
                btnFavorito.setImageResource(R.drawable.favorito)

                val guardado = db.guardarAlbumFavoritos(
                    albumId = idAlbum,
                    nombreAlbum = nombreAlbum,
                    artista = artista,
                    genero = genero,
                    canciones = canciones,
                    fecha = fecha,
                    imagen = imagen
                )

                //Evaluar si la insercion fue correcta
                if(guardado){
                    albumFavorito = !albumFavorito

                    Toast.makeText(this, "Album $nombreAlbum agregado a favoritos", Toast.LENGTH_SHORT).show()

                }else{
                    Toast.makeText(this, "Album $nombreAlbum ya se encuentra en favoritos", Toast.LENGTH_SHORT).show()

                }

            }

        }

    }

}