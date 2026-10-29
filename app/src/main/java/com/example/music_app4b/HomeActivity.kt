package com.example.music_app4b

import android.content.Intent
import android.os.Bundle
import android.view.View
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
    private lateinit var contenedorRock : LinearLayout
    private lateinit var contenedorArtista : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)


        tvResultado = findViewById(R.id.tvResultado)
        contenedorIndie = findViewById(R.id.contenedorIndie)
        contenedorRock = findViewById(R.id.contenedorRock)
        contenedorArtista = findViewById(R.id.contenedorArtista)


        //TODO: Agregar metodo de llamado de album
        obtenerAlbum("Indie", contenedorIndie)
        obtenerAlbum("Rock", contenedorRock)
        obtenerAlbum("te vi en un planetario", contenedorArtista)


    }

    //METODO PARA OBTENER INFORMACION DEL ALBUM
    private fun obtenerAlbum(genero : String, contenedor : LinearLayout){

        // Hacer el llamado
        RetrofitClient.api.obtenerAlbumGenero(
            genero = genero,
            pais = "mx",
            media = "music",
            entidad = "album",
            limite = 10
        ).enqueue(object : Callback<iTunesResponse> {

            //METODO EN CASO DE QUE LA RESPUESTA SEA CORRECTA
            override fun onResponse(call: Call<iTunesResponse?>, response: Response<iTunesResponse?>) {

                //Variable para guardar el numero de albumes consultados
                //Variable para que guarde una lista de tipo Album
                val albumes = response.body()?.results ?: emptyList()

                //Validar si la variable albumes tiene informacion
                if(!albumes.isNullOrEmpty()){

                    tvResultado.visibility = View.GONE
                    mostrarAlbumes(albumes, contenedor)

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
    private fun mostrarAlbumes(albumes: List<Album>, contenedor: LinearLayout){

        //OBLIGATORIO: Evitar elementos duplicados
        contenedor.removeAllViews()

        //Ciclo para recorrer las multiples peticiones a un API
        for (album in albumes){

            //Usar el LinearLayout con el contenedor
            val vistaAlbum = layoutInflater.inflate(
                R.layout.item_album,
                contenedor,
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


            //EVENTO
            vistaAlbum.setOnClickListener {

                //Necesitamos obtener el ID
                val idAlbum = album.collectionId

                //Validacion para saber que se cuenta con ID
                if(idAlbum != null){

                    val intent = Intent(this@HomeActivity, DetailsActivity::class.java)

                    //Enviar datos
                    intent.putExtra("ID_ALBUM", idAlbum)
                    intent.putExtra("NOMBRE_ALBUM", album.collectionName)
                    intent.putExtra("ARTISTA_ALBUM", album.artistName)
                    intent.putExtra("GENERO_ALBUM", album.primaryGenreName)
                    intent.putExtra("CANCIONES_ALBUM", album.trackCount)
                    intent.putExtra("IMAGEN_ALBUM", album.obtenerImagenGrande())

                    startActivity(intent)
                }
            }

            //Asignar la vista del contenedor a las tarjetas de albumes
            contenedor.addView(vistaAlbum)
        }
    }
}