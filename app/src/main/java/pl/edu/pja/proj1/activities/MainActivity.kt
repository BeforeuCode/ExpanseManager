package pl.edu.pja.proj1.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.os.HandlerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import pl.edu.pja.proj1.App
import pl.edu.pja.proj1.adapter.expanse.ExpanseAdapter
import pl.edu.pja.proj1.databinding.ActivityMainBinding
import pl.edu.pja.proj1.model.Expanse



private const val REQUEST_ADD_EXPANSE = 10
private const val REQUEST_EDIT_EXPANSE = 20

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val expanseAdapter by lazy { ExpanseAdapter((application as App).database, refreshBalance(), {expanse -> onClick(expanse)  }) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupExpansesList()
        setupAddExpanseButton()
        setupBalance()
    }

    private fun refreshBalance(): () -> Unit = {
        setupBalance()
    }

    private fun onClick(expanse: Expanse) {
        val editExpanseIntent = Intent(this, AddExpanseActivity::class.java)
        editExpanseIntent.putExtra("id", expanse.id)
        startActivityForResult(editExpanseIntent, REQUEST_EDIT_EXPANSE)
    }

    private fun setupBalance() {
        val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
        mainHandler.post{
            binding.currentMonthTotalValue.text = expanseAdapter.getBalance();
        }
    }


    private fun setupExpansesList() {
        binding.expanseList.apply {
            adapter = expanseAdapter
            layoutManager = LinearLayoutManager(context)
            itemAnimator = null
        }
        expanseAdapter.load()
    }

    private fun setupAddExpanseButton() = binding.addButton.setOnClickListener {
        val addExpanseIntent = Intent(this, AddExpanseActivity::class.java)
        startActivityForResult(addExpanseIntent, REQUEST_ADD_EXPANSE)
        setupBalance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ADD_EXPANSE && resultCode == Activity.RESULT_OK) {
            expanseAdapter.load()
            setupBalance()
        } else super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onResume() {
        super.onResume()
        setupExpansesList()
        setupBalance()
    }
}