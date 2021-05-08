package pl.edu.pja.proj1.adapter.expanse

import androidx.recyclerview.widget.DiffUtil
import pl.edu.pja.proj1.model.Expanse

class ExpanseDiff(private val oldExpanses: List<Expanse>, private val newExpanses: List<Expanse>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldExpanses.size

    override fun getNewListSize(): Int = newExpanses.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldExpanses[oldItemPosition].id == newExpanses[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldExpanses[oldItemPosition].place == newExpanses[newItemPosition].place &&
                oldExpanses[oldItemPosition].category == newExpanses[newItemPosition].category &&
                oldExpanses[oldItemPosition].date == newExpanses[newItemPosition].date &&
                oldExpanses[oldItemPosition].amount == newExpanses[newItemPosition].amount &&
                oldExpanses[oldItemPosition].isIncome == newExpanses[newItemPosition].isIncome
}