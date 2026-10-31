package edu.umg.controlgastos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        GastoEntity::class,
        IngresoEntity::class,
        UsuarioEntity::class,
        PresupuestoEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gastoDao(): GastoDao
    abstract fun ingresoDao(): IngresoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun presupuestoDao(): PresupuestoDao

    companion object {

        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS ingresos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        descripcion TEXT NOT NULL,
                        monto REAL NOT NULL,
                        categoria TEXT NOT NULL,
                        fecha TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS usuarios (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL,
                        correo TEXT NOT NULL,
                        fechaRegistro TEXT NOT NULL
                    )
                """.trimIndent())
            }
        }

        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS presupuesto (
                        id INTEGER NOT NULL PRIMARY KEY,
                        monto REAL NOT NULL
                    )
                """.trimIndent())
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun obtenerBaseDeDatos(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "control_gastos_db"
                )
                    .addMigrations(
                        MIGRACION_1_2,
                        MIGRACION_2_3,
                        MIGRACION_3_4
                    )
                    .build()

                INSTANCE = instancia
                instancia
            }
        }
    }
}