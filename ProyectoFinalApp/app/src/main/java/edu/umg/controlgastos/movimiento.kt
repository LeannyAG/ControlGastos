package edu.umg.controlgastos



data class Movimiento(
    val id: Int,
    val descripcion: String,
    val monto: Double,
    val categoria: String,
    val fecha: String,
    val esIngreso: Boolean
)