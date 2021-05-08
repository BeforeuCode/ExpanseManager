package pl.edu.pja.proj1.activities

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import pl.edu.pja.proj1.App
import pl.edu.pja.proj1.R
import pl.edu.pja.proj1.databinding.ActivityMpGraphBinding
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


class MpGraphActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMpGraphBinding.inflate(layoutInflater) }
    private val db by lazy { (application as App).database }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupSelect();
        setupChart()
    }

    private fun setupChart() {
        val dataPoints = generateDataPoints();
        binding.mpChart.data = getLineData(dataPoints)
        binding.mpChart.invalidate();
        binding.mpChart.legend.isEnabled = false;
        binding.mpChart.axisLeft.valueFormatter = LineChartYAxisValueFormatter()
        binding.mpChart.axisRight.isEnabled = false
        binding.mpChart.xAxis.labelCount = 20
    }

    fun handleMonthChange(id: Long) {
        setupChart()
    }

    private fun setupSelect() {
        val spinner: Spinner = binding.monthsSelect2
        ArrayAdapter.createFromResource(
            this,
            R.array.monthList,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    handleMonthChange(id)
                }

            }
            spinner.adapter = adapter
        }
        val currentMonth: Int = LocalDate.now().monthValue - 1
        spinner.setSelection(currentMonth)
    }


    private fun getLineData(dataPoints: List<DataPoint>): LineData? {
        val dataSets: MutableList<ILineDataSet> = ArrayList()
        var entries = ArrayList<Entry?>()
        var prevValueIsPositive = false
        var prevValueIsNegative = false

        for (i in dataPoints.indices) {
            val y = dataPoints[i].yVal
            //positive y values
            if (y > 0) {
                if (prevValueIsNegative) {
                    //calculate the common mid point between a positive and negative y
                    val midEntry = Entry(dataPoints[i].xVal.toFloat(), 0f)
                    entries.add(midEntry)

                    //draw the current negative dataSet to Red color
                    dataSets.add(getLineDataSet(entries, android.R.color.holo_red_dark, android.R.color.holo_purple))

                    //and initialize a new DataSet starting from the above mid point Entry
                    entries = ArrayList()
                    entries.add(midEntry)
                    prevValueIsNegative = false
                }
                //we are already in a positive dataSet continue adding positive y values
                entries.add(Entry(i.toFloat(), y.toFloat()))
                prevValueIsPositive = true
                //not having any other positive-negative changes so add the remaining positive values in the final dataSet
                if (i == dataPoints.size - 1) {
                    dataSets.add(getLineDataSet(entries, android.R.color.holo_green_light, android.R.color.holo_orange_dark))
                }
            } else if (y < 0) {
                //we are changing to negative values so draw the current positive dataSets
                if (prevValueIsPositive) {
                    //calculate the common mid point between a positive and negative y
                    val midEntry = Entry(dataPoints[i].xVal.toFloat(), 0f)
                    entries.add(midEntry)

                    //draw the current positive dataSet to Green color
                    dataSets.add(getLineDataSet(entries, android.R.color.holo_green_light, android.R.color.holo_orange_dark))

                    //and initialize a new DataSet starting from the above mid point Entry
                    entries = ArrayList()
                    entries.add(midEntry)
                    prevValueIsPositive = false
                }

                //we are already in a negative dataSet continue adding negative y values
                entries.add(Entry(i.toFloat(), y.toFloat()))
                prevValueIsNegative = true
                //not having any other positive-negative changes so add the remaining negative values in the final dataSet
                if (i == dataPoints.size - 1) {
                    dataSets.add(getLineDataSet(entries, android.R.color.holo_red_dark, android.R.color.holo_purple))
                }
            }
        }
        return LineData(dataSets)
    }

    private fun generateDataPoints(): List<DataPoint> {
        val selectedMonth = binding.monthsSelect2.selectedItemPosition
        println(selectedMonth);
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

    private fun getLineDataSet(entries: ArrayList<Entry?>, fillColor: Int, lineColor: Int): LineDataSet {
        val dataSet = LineDataSet(entries, "")
        dataSet.setDrawCircles(false)
        dataSet.valueTextSize = 0f
        dataSet.lineWidth = 3f
        dataSet.mode = LineDataSet.Mode.HORIZONTAL_BEZIER
        dataSet.color = ContextCompat.getColor(this, lineColor)
        dataSet.setDrawFilled(true)
        dataSet.fillColor = ContextCompat.getColor(this, fillColor)
        return dataSet
    }

    class LineChartYAxisValueFormatter : IndexAxisValueFormatter() {

        override fun getFormattedValue(value: Float): String? {
            return "$value PLN"
        }
    }
}