package pl.edu.pja.proj1.model.dto

import android.content.ContentValues
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.edu.pja.proj1.model.Expanse
import java.util.*

@Entity(tableName = "expanse")
data class ExpanseDto(
    @PrimaryKey(autoGenerate = true)
    var id: Long = -0,
    var place: String = "",
    var category: String = "",
    var date: Date? = null,
    var amount: Double = 0.0,
    var isIncome: Boolean = false,
){
    fun toModel() = Expanse(id, place, category, date, amount, isIncome)

    companion object {
        fun fromContentValues(@Nullable values: ContentValues?): ExpanseDto{
            val expanse = ExpanseDto()
            if (values != null && values.containsKey("id")) {
                expanse.id = values.getAsLong("id");
            }
            if (values != null && values.containsKey("place")) {
                expanse.place = values.getAsString("place");
            }
            if (values != null && values.containsKey("category")) {
                expanse.category = values.getAsString("category");
            }
            if (values != null && values.containsKey("amount")) {
                expanse.amount = values.getAsString("amount").toDouble();
            }
            if (values != null && values.containsKey("isIncome")) {
                expanse.isIncome = values.getAsString("isIncome").toBoolean();
            }
            return expanse;
        }
    }
}