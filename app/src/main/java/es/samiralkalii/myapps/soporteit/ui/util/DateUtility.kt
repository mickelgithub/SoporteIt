package es.samiralkalii.myapps.soporteit.ui.util

import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT= "yyyy-MM-dd"
private const val HOUR_FORMAT= "HH:mm:ss"




fun formatDate(date: Long): String {
    val df= SimpleDateFormat(DATE_FORMAT)
    df.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()))
    return df.format(date)
}

fun formatHour(date: Long): String {
    val df= SimpleDateFormat(HOUR_FORMAT)
    df.setTimeZone(TimeZone.getTimeZone(getCurrentTimeZone()))
    return df.format(date)
}

private fun getCurrentTimeZone(): String {
    val tz = Calendar.getInstance().timeZone
    return tz.id
}
