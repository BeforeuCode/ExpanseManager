package pl.edu.pja.proj1.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Spinner
import pl.edu.pja.proj1.App
import pl.edu.pja.proj1.R
import pl.edu.pja.proj1.databinding.ActivityChartBinding
import java.time.LocalDate
import java.util.*

class ChartActivity : AppCompatActivity() {

    private val binding by lazy { ActivityChartBinding.inflate(layoutInflater) }
    private val db by lazy { (application as App).database }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupSelect()
    }

    fun handleMonthChange(id: Long) {
        println(id)
        val selectedMonth = binding.monthsSelect.selectedItemPosition
        val daysInMonth: Int = DateUtils.getLastDayOfMonth(selectedMonth + 1)
        binding.graphView.setData(generateDataPoints(), daysInMonth)
    }

    private fun setupSelect() {
        val spinner: Spinner = binding.monthsSelect
        ArrayAdapter.createFromResource(
            this,
            R.array.monthList,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.onItemSelectedListener = object : OnItemSelectedListener{
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    handleMonthChange(id)
                }

            }
            spinner.adapter = adapter
        }
        val currentMonth: Int = LocalDate.now().monthValue - 1
        spinner.setSelection(currentMonth)
    }


    private fun generateDataPoints(): List<DataPoint> {
        val selectedMonth = binding.monthsSelect.selectedItemPosition

        val amounts = getAmounts(selectedMonth)
        val dates = getDates(selectedMonth)
        val daysInMonth: Int = DateUtils.getLastDayOfMonth(selectedMonth + 1)
        var i = 0
        var sum = 0.0
        var summary: MutableList<Double> = (0 until daysInMonth).map {0.0}.toMutableList()

        for(i in dates.indices){
            val a = dates[i].toString().substring(8, 10).toInt()
            summary[a] += amounts[i]
        }

        var dataPointsList = mutableListOf<DataPoint>()
        for(i in 0 until summary.size){
            sum += summary[i]
            dataPointsList.add(DataPoint(i, sum.toInt()))
        }
        return dataPointsList
    }



    private fun getDates(monthInt: Int): List<Date> {
        val startDate: Date = DateUtils.createDateFromString(2021, monthInt, 1)
        val lastDay: Int = DateUtils.getLastDayOfMonth(monthInt)
        val endDate: Date = DateUtils.createDateFromString(2021, monthInt, lastDay)
        var dateList: List<Date> = emptyList()
        val t = Thread {
            dateList = db.expanses.getEventDatesFromDateBetween(startDate, endDate)
        }
        t.start()
        t.join()
        return dateList
    }

    private fun getAmounts(monthInt: Int): List<Double> {
        val startDate: Date = DateUtils.createDateFromString(2021, monthInt, 1)
        val lastDay: Int = DateUtils.getLastDayOfMonth(monthInt)
        val endDate: Date = DateUtils.createDateFromString(2021, monthInt, lastDay)
        var amountList: List<Double> = emptyList()
        val t = Thread {
            amountList = db.expanses.getEventAmountFromDateBetween(startDate, endDate)
        }
        t.start()
        t.join()
        return amountList
    }
}