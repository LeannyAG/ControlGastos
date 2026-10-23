
package edu.umg.controlgastos.modelo

data class Categoria(
    val id: Int,
    val nombre: String,
    val predeterminada: Boolean = false
)