package com.word.search.puzzle.play.constants

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

class Hive {

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        @SuppressLint("SimpleDateFormat") val mdformat = SimpleDateFormat("dd MMM, yyyy")
        return mdformat.format(calendar.time)
    }

    fun getCurrentMonth(): Pair<String, String>? {
        val calendar = Calendar.getInstance()
        @SuppressLint("SimpleDateFormat") val displayFormat = SimpleDateFormat("MMM, yyyy")
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("MM/yyyy")
        return Pair<String, String>(displayFormat.format(calendar.time), "${dateFormat.format(calendar.time)}")
    }

    fun getPreviousMonth(currentMonth: String): Pair<String, String>? {
        val calendar = Calendar.getInstance()
        @SuppressLint("SimpleDateFormat") val displayFormat = SimpleDateFormat("MMM, yyyy")
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("MM/yyyy")
        val date: Date = displayFormat.parse(currentMonth)!!
        calendar.time = date
        calendar.add(Calendar.MONTH, -1)
        return Pair<String, String>(displayFormat.format(calendar.time), "${dateFormat.format(calendar.time)}")
    }

    fun getNextMonth(currentMonth: String): Pair<String, String>? {
        val calendar = Calendar.getInstance()
        @SuppressLint("SimpleDateFormat") val displayFormat = SimpleDateFormat("MMM, yyyy")
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("MM/yyyy")
        val date: Date = displayFormat.parse(currentMonth)!!
        calendar.time = date
        calendar.add(Calendar.MONTH, 1)
        return Pair<String, String>(displayFormat.format(calendar.time), "${dateFormat.format(calendar.time)}")
    }

    fun getCurrentYear(): String {
        val calendar = Calendar.getInstance()
        @SuppressLint("SimpleDateFormat") val displayFormat = SimpleDateFormat("yyyy")
        return displayFormat.format(calendar.time)
    }

    fun getPreviousYear(currentMonth: String): String {
        val calendar = Calendar.getInstance()
        @SuppressLint("SimpleDateFormat") val displayFormat = SimpleDateFormat("yyyy")
        val date: Date = displayFormat.parse(currentMonth)!!
        calendar.time = date
        calendar.add(Calendar.YEAR, -1)
        return displayFormat.format(calendar.time)
    }

    fun getNextYear(currentMonth: String): String {
        val calendar = Calendar.getInstance()
        @SuppressLint("SimpleDateFormat") val displayFormat = SimpleDateFormat("yyyy")
        val date: Date = displayFormat.parse(currentMonth)!!
        calendar.time = date
        calendar.add(Calendar.YEAR, 1)
        return displayFormat.format(calendar.time)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateFromString(stringDate: String): Date {
        val finalFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date: Date? = null
        try {
            date = finalFormat.parse(stringDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date!!
    }

    fun getStringFromDate(date: Date): String {
        @SuppressLint("SimpleDateFormat") val finalFormat = SimpleDateFormat("dd/MM/yyyy")
        return finalFormat.format(date)
    }

    fun getCurrentDateTime(): String {
        val calendar = Calendar.getInstance()
        @SuppressLint("SimpleDateFormat") val mdformat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return mdformat.format(calendar.time)
    }

    fun getTimestamp(): String {
        val calendar = Calendar.getInstance()
        @SuppressLint("SimpleDateFormat") val mdformat = SimpleDateFormat("yyyyMMdd_HHmmss")
        return mdformat.format(calendar.time)
    }

    fun formatCurrency(amount: Float): String {
        val df = DecimalFormat("#,###,###,###,###.00")
        df.minimumFractionDigits = 2
        return df.format(amount)
    }

    @SuppressLint("SimpleDateFormat")
    fun formatDateHeader(date: String): String {
        val inFormat = SimpleDateFormat("dd/MM/yyyy")
        val input: Date = inFormat.parse(date)
        val weekDayFormat = SimpleDateFormat("EEEE").format(input)
        val dayFormat = SimpleDateFormat("dd").format(input)
        val monthFormat = SimpleDateFormat("MM").format(input)
        val yearFormat = SimpleDateFormat("yyyy").format(input)

        return "$weekDayFormat#$dayFormat#$monthFormat#$yearFormat"
    }

    fun loadWeeks() {
        val cal = Calendar.getInstance()
        for (i in 0..11) {
            cal[Calendar.YEAR] = 2020
            cal[Calendar.DAY_OF_MONTH] = 1
            cal[Calendar.MONTH] = i
            val maxWeekNumber = cal.getActualMaximum(Calendar.WEEK_OF_MONTH)
            println("loadWeeks For Month :: $i Num Week :: $maxWeekNumber")
        }
    }

    fun getWeekRange(year: Int, week_no: Int): Pair<String, String> {
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_WEEK] = Calendar.SUNDAY
        cal[Calendar.YEAR] = year
        cal[Calendar.WEEK_OF_YEAR] = week_no
        val sunday = cal.time
        cal.add(Calendar.DATE, 6)
        val saturday = cal.time
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return Pair<String, String>(sdf.format(sunday), sdf.format(saturday))
    }

    fun getWeeksOfMonth(month: Int, year: Int): List<WeekRangeEntity> {
        val cal = Calendar.getInstance()
        cal[Calendar.YEAR] = year
        cal[Calendar.MONTH] = month - 1
        cal[Calendar.DAY_OF_MONTH] = 1
        val monthDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val positionOfWeekOfYear: ArrayList<String> = ArrayList()
        val weekRange = mutableListOf<WeekRangeEntity>()
        for (i in 0 until monthDays) {
            cal.set(year, month - 1, i)
            val weekOfYear: Int = cal.get(Calendar.WEEK_OF_YEAR)

            if (!positionOfWeekOfYear.contains("$weekOfYear")) {
                positionOfWeekOfYear.add("$weekOfYear")
                val range = getWeekRange(year, cal[Calendar.WEEK_OF_YEAR])
                weekRange.add(
                    WeekRangeEntity(
                    weekPosition = weekOfYear.toString(),
                    startDate = range.first,
                    endDate = range.second
                )
                )
            }
            cal.add(Calendar.DATE, 1)
        }
        return weekRange
    }

    fun getRegexBack(html: String, regex: String): String {

        val patt = Pattern.compile(regex)
        val match = patt.matcher(html)

        while (match.find()) {

            return match.group(1)
        }
        return ""
    }
}

data class WeekRangeEntity(
    var weekPosition: String,
    var startDate: String,
    var endDate: String
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}

fun getFileSize(size: Long): String {
    if (size <= 0)
        return "0"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()

    return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
}
