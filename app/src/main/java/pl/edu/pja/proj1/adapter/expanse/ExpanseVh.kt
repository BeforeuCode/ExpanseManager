package pl.edu.pja.proj1.adapter.expanse

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import pl.edu.pja.proj1.databinding.ItemExpanseBinding
import pl.edu.pja.proj1.model.Expanse
import java.text.SimpleDateFormat

class ExpanseVh(private  val binding: ItemExpanseBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(expanse: Expanse, onClickCallback: (expanse: Expanse) -> Unit) = with(binding) {
        placeValue.text = expanse.place
        categoryValue.text = expanse.category
        val dateFormat = SimpleDateFormat("dd.MM.yyyy");
        dateValue.text = dateFormat.format(expanse.date)
        amountValue.text = expanse.amount.toString() + " PLN"
        binding.card.setOnClickListener {
            onClickCallback(expanse)
        }
        if(expanse.isIncome) {
            binding.card.setCardBackgroundColor(Color.parseColor("#c5e1a5"))
        } else {
            binding.card.setCardBackgroundColor(Color.parseColor("#ef9a9a"))
        }

    }
}