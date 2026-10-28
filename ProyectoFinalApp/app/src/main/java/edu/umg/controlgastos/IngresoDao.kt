package edu.umg.controlgastos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IngresoDao {

    @Insert
    suspend fun insertar(ingreso: IngresoEntity)

    @Query("SELECT * FROM ingresos ORDER BY id DESC")
    fun listarTodos(): Flow<List<IngresoEntity>>

    @Query("SELECT * FROM ingresos WHERE id = :id")
    suspend fun buscarPorId(id: Int): IngresoEntity?

    @Update
    suspend fun actualizar(ingreso: IngresoEntity)

    @Delete
    suspend fun eliminar(ingreso: IngresoEntity)
}