package pl.edu.pja.proj1.activities


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import pl.edu.pja.proj1.App
import pl.edu.pja.proj1.databinding.ActivityAddExpanseBinding
import pl.edu.pja.proj1.model.dto.ExpanseDto
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.absoluteValue


class AddExpanseActivity : AppCompatActivity() {
    private val binding by lazy { ActivityAddExpanseBinding.inflate(layoutInflater) }
    private val database by lazy { (application as App).database }
    private var isEditing: Boolean = false;
    private var expanseId: Long = 0;
    private var expanse: ExpanseDto? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.extras != null) {
            expanseId = intent.extras!!.get("id") as Long
            isEditing = true;
            binding.addExpanseButton.text = "Save"
            binding.shareButton.visibility = View.VISIBLE
            getExpanseDetails(expanseId)
        }
        setContentView(binding.root)
        setupSaveButton()
        setupDatePicker()
        if(isEditing) {
            setupShareButton()
        }

    }

    private fun setupShareButton() {
        val format = SimpleDateFormat("dd.MM.yyyy")
        val dateString = format.format(expanse!!.date);
        val expanseType = if (expanse!!.isIncome) {
            "earned "
        } else {
            "spent "
        }
        val message: String =
            "I`ve " + expanseType + expanse!!.amount.absoluteValue.toString() + "PLN" + " in " + expanse!!.place + " on " + dateString + " Category: " + expanse!!.category

        binding.shareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND

                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    private fun getExpanseDetails(id: Long) {
        thread {
            val mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
            expanse = database.expanses.getById(id)
            mainHandler.post {
                binding.placeInput.setText(expanse!!.place)
                binding.categoryInput.setText(expanse!!.category)
                val format = SimpleDateFormat("dd.MM.yyyy")
                val dateString = format.format(expanse!!.date);
                binding.dateInput.setText(dateString)
                binding.amountInput.setText(expanse!!.amount.absoluteValue.toString())
                binding.isIncomeSwitch.isChecked = expanse!!.isIncome
            }
        }
    }

    private fun setupDatePicker() {
        binding.dateInput.transformIntoDatePicker(this, "dd.MM.yyyy", Date())
    }

    private fun setupSaveButton() = binding.addExpanseButton.setOnClickListener {
        val place = binding.placeInput.text.toString();
        val category = binding.categoryInput.text.toString();
        val format = SimpleDateFormat("dd.MM.yyyy")
        val date = format.parse(binding.dateInput.text.toString());
        val isIncome = binding.isIncomeSwitch.isChecked
        val sign = if (isIncome) {
            ""
        } else {
            "-"
        }

        val amount = (sign + binding.amountInput.text.toString()).toDouble()

        val expanseDto = ExpanseDto(
            id = expanseId,
            place = place,
            category = category,
            date = date,
            amount = amount,
            isIncome = isIncome
        )
        if (isEditing) {
            thread {
                database.expanses.editExpanse(expanseDto)
                setResult(Activity.RESULT_OK)
                finish()
            }

        } else {
            thread {
                database.expanses.addExpanse(expanseDto)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }


    }

    private fun EditText.transformIntoDatePicker(
        context: Context,
        format: String,
        maxDate: Date? = null
    ) {
        isFocusableInTouchMode = false
        isClickable = true
        isFocusable = false

        val myCalendar = Calendar.getInstance()
        val datePickerOnDataSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                myCalendar.set(Calendar.YEAR, year)
                myCalendar.set(Calendar.MONTH, monthOfYear)
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val sdf = SimpleDateFormat(format, Locale.UK)
                setText(sdf.format(myCalendar.time))
            }

        setOnClickListener {
            DatePickerDialog(
                context, datePickerOnDataSetListener, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)
            ).run {
                maxDate?.time?.also { datePicker.maxDate = it }
                show()
            }
        }
    }

}