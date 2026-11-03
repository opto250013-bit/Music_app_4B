package com.example.music_app4b

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//crear clase DatabaseHeler que hereda de la clase SQLite
class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION,
){
    //DATOS DE LA BD
    companion object{

        //Informacion de la BD
        private const val DATABASE_NAME = "musica.db"
        private const val  DATABASE_VERSION = 3

        //Infomación de las tablas en la BD
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"

        //Informacion de la tabla de favoritos
        private const val TABLE_FAVORITOS = "favorits"
        private const val COL_ID_FAVORIO = "id"
        private const val COL_ALBUM_ID = "album_id"
        private const val COL_NOMBRE_ALBUM = "nombre_album"
        private const val COL_ARTISTA = "artista"
        private const val COL_CANCIONES = "num_canciones"
        private const val COL_GENERO = "genero"
        private const val COL_FECHA = "fecha"
        private const val COL_IMAGEN = "imagen"

    }

    //Metodo oncreate - crear la bd
    override fun onCreate(db: SQLiteDatabase?) {
        //crear la tabla
        val crearTablaUsuarios = """
            CREATE TABLE $TABLE_USUARIOS(
            $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_NOMBRE TEXT NOT NULL,
            $COL_CORREO TEXT NOT NULL,
            $COL_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        //crear tabla de usuarios
        db?.execSQL(crearTablaUsuarios)
    }

    //Metodo onupgrade - actualizar la bd
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    //Metodo para crear un usuario
    fun registrarUsuario(nombre : String, correo : String, password : String) : Boolean{

        //Crear variable para escribir en la bd
        val db = writableDatabase

        //insertar los datos
        val datos = ContentValues().apply {
            put(COL_NOMBRE, nombre)
            put(COL_CORREO, correo)
            put(COL_PASSWORD, password)
        }

        //ejecutar insercion de datod
        val resultado = db.insert(TABLE_USUARIOS, null, datos)

        //cerrar conexion de la bd
        db.close()


        //regresar el valor obtenido de la insersion (verdadero o falso)
        return resultado != -1L
    }

    //METODO PARA INiCIAR SESION (CONSULTA)
    fun iniciarSesion(correo : String, password : String) : Boolean{

        //Variable que lee la base de datos
        val db = readableDatabase

        //Crear consulta
        val quey_sql= """
            SELECT * FROM $TABLE_USUARIOS
            WHERE $COL_CORREO = ? AND $COL_PASSWORD = ?
        """.trimIndent()

        //Crear un cursor que permite recorrer todos los registros
        val cursor = db.rawQuery(
            quey_sql, arrayOf(correo, password)
        )

        //Verificar cuantos resultados encuentra el cursor
        val usuarioEncontrado = cursor.count > 0

        cursor.close()
        db.close()

        return  usuarioEncontrado
    }

}