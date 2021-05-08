package pl.edu.pja.proj1.activities

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {
        fun getLastDayOfMonth(month: Int): Int {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.MONTH, month)
            return calendar.getActualMaximum(Calendar.DATE)
        }

        fun createDateFromString(year: Int, month: Int, day: Int): Date {
            val format = SimpleDateFormat("yyyy-MM-dd")
            var sMonth = (month + 1).toString()
            if (sMonth.length == 1) {
                sMonth = "0$sMonth"
            }
            var sDay = day.toString()
            if (sDay.length == 1) {
                sDay = "0$sDay"
            }
            var sDate = "$year-$sMonth-$sDay"
            val date = format.parse(sDate)
            return date as Date
        }
    }

}