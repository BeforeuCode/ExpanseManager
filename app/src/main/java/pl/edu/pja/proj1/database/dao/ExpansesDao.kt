package pl.edu.pja.proj1.database.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import pl.edu.pja.proj1.model.Expanse
import pl.edu.pja.proj1.model.dto.ExpanseDto
import java.util.*

@Dao
interface ExpansesDao {

    @Query("SELECT * FROM expanse")
    fun selectAll(): Cursor

    @Query("SELECT * FROM expanse WHERE expanse.id = :id")
    fun selectById(id: Long): Cursor

    @Insert
    fun insert(expanse: ExpanseDto?): Long

    @Query("DELETE FROM expanse WHERE id = :id")
    fun removeById(id: Long): Int

    @Update
    fun update(expanse: ExpanseDto?): Int

    @Query("SELECT * FROM expanse;")
    fun getAll(): List<ExpanseDto>

    @Insert
    fun addExpanse(expanse: ExpanseDto)

    @Query("DELETE FROM expanse WHERE id = :id")
    fun deleteById(id: Long)

    @Query("SELECT SUM(amount) as sum from expanse")
    fun getBalance(): Double

    @Query("SELECT * FROM expanse WHERE id = :id")
    fun getById(id: Long): ExpanseDto

    @Update
    fun editExpanse(expanseDto: ExpanseDto)

}