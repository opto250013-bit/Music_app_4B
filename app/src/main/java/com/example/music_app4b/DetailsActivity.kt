package com.example.music_app4b

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import org.w3c.dom.Text

class DetailsActivity : AppCompatActivity() {
    //para las notificaciones
    companion object{
        private const val CANAL_ID = "canal_general"
        private const val NOTIFICACION_ID = 1
        private const val CODIGO_PERMISO = 100
    }

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

    //METODO PARA CREAR CANAL DE NOTIFICACIONES
    private fun crearCanalNoti(){

        //Evaluar el permiso del usuario para manejar notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val canal = NotificationChannel(
                CANAL_ID,
                "Notificaciones generales",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            canal.description = "Canal de enío de notificaciones sobre la BD"

            //Indicar el uso de notificaciones
            val adminNotificaciones = getSystemService(NotificationManager::class.java)

            adminNotificaciones.createNotificationChannel(canal)
        }
    }

    //METODO PARA MOSTRAR LA NOTIFICCION
    private fun mostrarNoti(){

        //VERIFICAR LOS PERMISOS SELECCIONADOS POR EL USUARIO
        //Si el usuario no ha dado ermiso de enviar notificaciones
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
            this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ){

            //Volver a pedir permiso al usuario
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), CODIGO_PERMISO)
            Toast.makeText(this, "Autoriza el envio de notificaciones", Toast.LENGTH_SHORT).show()

            return
        }

        val notificacion = NotificationCompat.Builder(this, CANAL_ID)
            .setSmallIcon(R.drawable.baseline_email_24)
            .setContentTitle("Music-App")
            .setContentText("Un album fue añadido a la base de datos")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

    }

}