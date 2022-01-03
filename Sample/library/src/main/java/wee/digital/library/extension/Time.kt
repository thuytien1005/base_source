package wee.digital.library.extension

import java.lang.reflect.InvocationTargetException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

val SECOND: Long get() = 1000

val MIN: Long get() = 60 * SECOND

val HOUR: Long get() = 60 * MIN

val DAY: Long get() = 24 * HOUR

val MONTH: Long get() = 31 * DAY

val YEAR: Long get() = 365 * DAY

val calendar: Calendar get() = Calendar.getInstance(TimeZone.getDefault())

val nowInMillis: Long get() = calendar.timeInMillis

val nowInSecond: Long get() = nowInMillis / SECOND

val defaultTimeFormat by lazy { SimpleDateFormat("dd/MM/yyyy-HH:mm:ss") }

val defaultDateFormat by lazy { SimpleDateFormat("dd/MM/yyyy") }

val simpleDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd") }

val responseDateFormat by lazy { SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'") }


fun nowText(strFormat: String): String {
    return nowInMillis.timeFormat(strFormat)
}

fun nowText(format: SimpleDateFormat): String {
    return nowInMillis.timeFormat(format)
}

val nowText: String get() = nowText(defaultTimeFormat)

val todayText: String get() = nowText(defaultDateFormat)

fun addDays(days: Int?): Date {
    val cal = calendar
    cal.add(Calendar.DATE, days ?: 0)
    return cal.time
}

fun addYears(years: Int): Date {
    val cal = calendar
    cal.add(Calendar.YEAR, years)
    return cal.time
}

fun Date.addDays(days: Int): Date {
    val cal = calendar.also { it.time = this }
    cal.add(Calendar.DATE, days)
    return cal.time
}

fun Date.addYears(years: Int): Date {
    val cal = calendar.also { it.time = this }
    cal.add(Calendar.YEAR, years)
    return cal.time
}

// if give up time in second convert to time in millis
fun Long.correctTime(): Long {
    return if (this < 1000000000000L) this * 1000 else this
}

fun Long.timeFormat(format: SimpleDateFormat): String {
    return try {
        format.format(Date(this.correctTime()))
    } catch (e: ParseException) {
        ""
    } catch (e: InvocationTargetException) {
        ""
    }
}

fun Long.timeFormat(strFormat: String): String {
    return timeFormat(SimpleDateFormat(strFormat))
}

fun Long.secondsFormat(): String {
    return "%s:%02d".format(this / 60, this % 60)
}

/**
 * [String] time convert
 */
fun String?.calculatorAge(format: SimpleDateFormat): Int {
    val inputDate = calendar
    try {
        inputDate.time = format.parse(this)
    } catch (e: ParseException) {
        print(e.message)
    }
    var age = calendar.year - inputDate.year
    when {
        calendar.month - inputDate.month == 0 -> {
            if (calendar.day - inputDate.day < 0) {
                age -= 1
            }
        }
        calendar.month - inputDate.month < 0 -> age -= 1
    }
    return age
}

fun String?.toDate(format: SimpleDateFormat): Date {
    this ?: return Date()
    return try {
        format.parse(this)
    } catch (e: Exception) {
        return Date()
    }
}

fun String?.toDateOrNull(format: SimpleDateFormat): Date? {
    this ?: return null
    return try {
        format.parse(this)
    } catch (e: Exception) {
        return null
    }
}

fun Date?.toString(format: SimpleDateFormat): String? {
    this ?: return null
    return try {
        format.format(this)
    } catch (e: Exception) {
        return null
    }
}

fun Date?.toString(strFormat: String): String? {
    return toString(SimpleDateFormat(strFormat))
}

fun Date.inTimes(minDate: Date?, maxDate: Date?): Boolean {
    minDate ?: return false
    maxDate ?: return false
    return this.after(minDate.addDays(-1)) && this.before(maxDate.addDays(1))
}

val Long.secondText: String
    get() {
        val seconds = this / 1000
        return "%02d:%02d".format(seconds / 60, seconds % 60)
    }

val Long.toSecond: Long
    get() {
        return this / 1000
    }

/**
 * [Calendar] time convert
 */
val Calendar.year: Int get() = this.get(Calendar.YEAR)

val Calendar.month: Int get() = this.get(Calendar.MONTH) + 1

val Calendar.day: Int get() = this.get(Calendar.DAY_OF_MONTH)

val Calendar.maxDayOfMonth: Int get() = this.getActualMaximum(Calendar.DAY_OF_MONTH)

val Calendar.dayOfYear: Int get() = this.getActualMaximum(Calendar.DAY_OF_YEAR)



