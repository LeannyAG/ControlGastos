package edu.umg.controlgastos.modelo

class Usuario {


    data class Usuario(
        val id: Int,
        val nombre: String,
        val apellido: String,
        val correo: String
    )
}