package pl.edu.pja.proj1.adapter.expanse

import android.app.AlertDialog
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.HandlerCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import pl.edu.pja.proj1.database.AppDatabase
import pl.edu.pja.proj1.databinding.ItemExpanseBinding

import pl.edu.pja.proj1.model.Expanse
import kotlin.concurrent.thread

class ExpanseAdapter(private val database: AppDatabase, private val refreshBalance:() -> Unit, private val onClickCallback:(expanse: Expanse) -> Unit) : RecyclerView.Adapter<ExpanseVh>() {

    private val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    var expanses = emptyList<Expanse>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpanseVh {
        val binding = ItemExpanseBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ExpanseVh(binding)
    }

    override fun onBindViewHolder(holder: ExpanseVh, position: Int) {
        holder.bind(expanses[position], onClickCallback)


        holder.itemView.setOnLongClickListener { it ->

            val builder: AlertDialog.Builder = AlertDialog.Builder(it.context)

            builder.setMessage("Are you sure you want to remove that entry?")
            builder.setTitle("Remove")
            builder.setCancelable(true)

            builder.setNegativeButton(
                "Cancel"
            ) { dialog, _ ->
                dialog.cancel()
            }

            builder.setPositiveButton(
                "Confirm"
            ) { _, _ ->
                thread {
                    database.expanses.deleteById(expanses[position].id)
                    val newExpanses = database.expanses.getAll().map { it.toModel() }
                    reloadView(newExpanses)
                    refreshBalance()
                }
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()

            return@setOnLongClickListener true
        }
    }

    override fun getItemCount(): Int = expanses.size

    fun getBalance(): String {
        var balance = ""
        val t = Thread { balance = database.expanses.getBalance().toString() + " PLN" }
        t.start()
        t.join()
        return balance
    }

    fun load() = thread {
        val newExpanses = database.expanses.getAll().map { it.toModel() }
        reloadView(newExpanses)
    }

    private fun reloadView(newExpanses: List<Expanse>) {
        val diff = ExpanseDiff(expanses, newExpanses)
        val result = DiffUtil.calculateDiff(diff)
        expanses = newExpanses
        mainHandler.post {
            result.dispatchUpdatesTo(this)
        }
    }




}