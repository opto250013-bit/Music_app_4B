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
    //DATOS DE LA bd
    companion object{

        //informacion de la BD
        private const val DATABASE_NAME = "musica.db"
        private const val  DATABASE_VERSION = 2

        //Infomación de las tablas en la BD
        private const val TABLE_USUARIOS = "usuarios"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre"
        private const val COL_CORREO = "correo"
        private const val COL_PASSWORD = "password"

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

}