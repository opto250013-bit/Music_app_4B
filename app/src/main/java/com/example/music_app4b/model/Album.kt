package com.example.music_app4b.model

//Archivo para obtener la informacion que regresa la api

data class Album(

    val wrapperType: String?,
    val collectionType: String?,

    val artistId: Long?,
    val collectionId: Long?,

    val artistName: String?,
    val collectionName: String?,

    val artistViewUrl: String?,
    val collectionViewUrl: String?,

    val artworkUrl100: String?,

    val collectionPrice: Double?,
    val trackCount: Int?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val currency: String?

) {
    //METODO PARA OBTENER LA IMAGEN MAS GRANDE
    fun obtenerImagenGrande() : String{
        return artworkUrl100?.replace("100x100.jpg", "600x600.jpg") ?: ""
    }
}
