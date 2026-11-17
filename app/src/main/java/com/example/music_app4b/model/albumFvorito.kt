package com.example.music_app4b.model

data class albumFvorito(
    val albumId : Long,
    val nombreAlbum : String,
    val artista : String,
    val genero : String,
    val canciones : Int,
    val fecha: String,
    val imagen : String
)
