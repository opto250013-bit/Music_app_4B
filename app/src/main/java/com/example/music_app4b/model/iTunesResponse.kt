package com.example.music_app4b.model

//ARCHIVO PARA RECIBIR LA RESPUESTA DE CONFIRMACION DE LA API

data class iTunesResponse(
    val resultCount : Int,
    val results : List<Album>

)
