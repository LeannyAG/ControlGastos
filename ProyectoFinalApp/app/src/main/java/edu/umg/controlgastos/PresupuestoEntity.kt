package edu.umg.controlgastos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "presupuesto")
data class PresupuestoEntity(
    @PrimaryKey val id: Int = 1,
    val monto: Double
)