package pl.edu.pja.proj1.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.edu.pja.proj1.database.dao.ExpansesDao
import pl.edu.pja.proj1.model.dto.ExpanseDto
import pl.edu.pja.proj1.utils.Converters

private const val  DATABASE_FILENAME = "expanses"

@Database(entities = [ExpanseDto::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract val expanses: ExpansesDao

    companion object {
        fun open(context: Context) = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_FILENAME).fallbackToDestructiveMigration().build()
    }

}