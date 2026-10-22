package com.example.music_app4b.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//ARCHIVO QUE PERMITE TENER UN OBJETO PAR LA CONEXION A INTERNET

object RetrofitClient {

    //Constante para tener la base url
    private const val BASE_URL = "https://itunes.apple.com/"

    // Variable que hereda de "lazy"
    val api : iTunesApi by lazy {
        // Construir la URL completa
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApi::class.java)
    }
}