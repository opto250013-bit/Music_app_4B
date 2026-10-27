package com.example.music_app4b

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.music_app4b.model.Album
import com.example.music_app4b.model.iTunesResponse
import com.example.music_app4b.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var  tvResultado : TextView
    private lateinit var contenedorIndie : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)


        tvResultado = findViewById(R.id.tvResultado)
        contenedorIndie = findViewById(R.id.contenedorIndie)

        //TODO: Agregar metodo de llamado de album
        obtenerAlbum()


    }

    //METODO PARA OBTENER INFORMACION DEL ALBUM
    private fun obtenerAlbum(){

        // Hacer el llamado
        RetrofitClient.api.obtenerAlbumGenero(
            genero = "Estamos Perdidos",
            pais = "mx",
            media = "music",
            entidad = "album",
            limite = 1
        ).enqueue(object : Callback<iTunesResponse> {

            //METODO EN CASO DE QUE LA RESPUESTA SEA CORRECTA
            override fun onResponse(call: Call<iTunesResponse?>, response: Response<iTunesResponse?>) {

                //Variable para guardar el numero de albumes consultados
                val albumes = response.body()?.results

                //Validar si la variable albumes tiene informacion
                if(!albumes.isNullOrEmpty()){

                    //Variable ara acceder al primer elemento de los albumes encontrados
                    val album = albumes[0]

                    tvResultado.text =
                        "Album: ${ album.collectionName } \n\n" +
                        "Artista: ${ album.artistName } \n\n" +
                        "Numero de canciones: ${ album.trackCount } \n\n"

                    // Mostrar la imagen del album
                    Glide.with(this@HomeActivity)
                        .load(album.obtenerImagenGrande())
                        .into(imgAlbum)

                }else{
                    Toast.makeText(this@HomeActivity, "No hay respuesta del API", Toast.LENGTH_SHORT).show()
                }
            }

            //METODO PARA HACER UNA ACCION SI EL AP FALLA
            override fun onFailure(call: Call<iTunesResponse?>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error en el API", Toast.LENGTH_SHORT).show()
            }

        })

    }

    //METODO PARA MOSTRAR EL LISTADO DE DATOS DE UN API
    private fun mostrarAlbumes(albumes: List<Album>){

        //OBLIGATORIO: Evitar elementos duplicados
        contenedorIndie.removeAllViews()

        //Ciclo para recorrer las multiples peticiones a un API
        for (album in albumes){

            //Usar el LinearLayout con el contenedor
            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album,
                contenedorIndie,
                false
            )

            val imgPortada = vistaAlbum.findViewById<ImageView>(R.id.imgPortadaAlbum)
            val tvNombreAlbum = vistaAlbum.findViewById<TextView>(R.id.tvNombreAlbum)
            val tvNombrertista = vistaAlbum.findViewById<TextView>(R.id.tvNombreArtista)

            // Asignando los nombres obtenidos del album del API
            tvNombreAlbum.text = album.collectionName ?: "Album sin nombre"
            tvNombrertista.text = album.artistName ?: "Artista desconocido"

            // Mostrar la imagen del album
            Glide.with(this@HomeActivity)
                .load(album.obtenerImagenGrande())
                .into(imgPortada)

            //Asignar la vista del contenedor a las tarjetas de albumes


        }
    }
}