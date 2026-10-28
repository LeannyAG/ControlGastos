package edu.umg.controlgastos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingresos")
data class IngresoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val monto: Double,
    val categoria: String,
    val fecha: String
)