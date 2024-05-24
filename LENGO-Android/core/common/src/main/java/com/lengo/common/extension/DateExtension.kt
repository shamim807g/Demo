package com.lengo.common.extension

import logcat.LogPriority
import logcat.logcat
import java.text.SimpleDateFormat
import java.util.*


fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

fun getCurrentDateTimeString(): String {
    return Calendar.getInstance().time.toString("yyyy-MM-dd'T'HH:mm:ss")
}


fun getCurrentHour(): Int {
    val cal = Calendar.getInstance()
    return cal.get(Calendar.HOUR_OF_DAY)
}

fun getCurrentDateTimeStringTwoHourBack(): String {
    val cal  = Calendar.getInstance()
    cal.add(Calendar.HOUR, -2)
    return cal.time.toString("yyyy-MM-dd'T'HH:mm:ss")
}

fun getCurrentDate(): String {
    return Calendar.getInstance().time.toString("yyyy-MM-dd")
}


fun formattedMinChartDate(startTime: String): String {
    var simpleDateFormat2 =  SimpleDateFormat("yyyy-MM-dd");
    val parsedDate = simpleDateFormat2.parse(startTime);
    simpleDateFormat2 = SimpleDateFormat("dd/MM")
    return simpleDateFormat2.format(parsedDate)
}

/**
 * Pattern: dd.MM.
 */
fun Date.formatToDayAndMonth(): String{
    val sdf= SimpleDateFormat("dd.MM.", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: HH:mm.
 */
fun Date.formatToHourAndMinute(): String{
    val sdf= SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(this)
}


/**
 * Pattern: yyyy-MM-dd HH:mm:ss
 */
fun Date.formatToServerDateTimeDefaults(): String{
    val sdf= SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

fun Date.formatToTruncatedDateTime(): String{
    val sdf= SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: yyyy-MM-dd
 */
fun Date.formatToServerDateDefaults(): String{
    val sdf= SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: HH:mm:ss
 */
fun Date.formatToServerTimeDefaults(): String{
    val sdf= SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: dd/MM/yyyy HH:mm:ss
 */
fun Date.formatToViewDateTimeDefaults(): String{
    val sdf= SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: dd/MM/yyyy
 */
fun Date.formatToViewDateDefaults(): String{
    val sdf= SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Pattern: HH:mm:ss
 */
fun Date.formatToViewTimeDefaults(): String{
    val sdf= SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return sdf.format(this)
}

/**
 * Add field date to current date
 */
fun Date.add(field: Int, amount: Int): Date {
    Calendar.getInstance().apply {
        time = this@add
        add(field, amount)
        return time
    }
}

fun getDayOfWeek(days: Int,locale: String): String {
    return getCalculatedDate("EE",days,locale)
}

fun getDate(days: Int, locale: String): String {
    return getCalculatedDate("yyyy-MM-dd",days,locale)
}

fun getCalculatedDate(format: String,days: Int, locale: String): String {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, days)
    val sdf = SimpleDateFormat(format, Locale(locale))
    return sdf.format(Date(cal.timeInMillis))
}

fun Date.addYears(years: Int): Date{
    return add(Calendar.YEAR, years)
}
fun Date.addMonths(months: Int): Date {
    return add(Calendar.MONTH, months)
}
fun Date.addDays(days: Int): Date{
    return add(Calendar.DAY_OF_MONTH, days)
}
fun Date.addHours(hours: Int): Date{
    return add(Calendar.HOUR_OF_DAY, hours)
}
fun Date.addMinutes(minutes: Int): Date{
    return add(Calendar.MINUTE, minutes)
}
fun Date.addSeconds(seconds: Int): Date{
    return add(Calendar.SECOND, seconds)
}