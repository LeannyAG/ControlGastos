package edu.umg.controlgastos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PresupuestoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(presupuesto: PresupuestoEntity)

    @Query("SELECT * FROM presupuesto WHERE id = 1")
    fun obtenerPresupuesto(): Flow<PresupuestoEntity?>
}