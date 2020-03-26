package es.samiralkalii.myapps.soporteit.ui.util

import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT= "yyyy-MM-dd"
private const val HOUR_FORMAT= "HH:mm:ss"




fun formatDate(date: Long): String {
    val calendar= Calendar.getInstance()
    calendar.timeInMillis= date
    val df= SimpleDateFormat(DATE_FORMAT)
    return df.format(calendar.time)
}

fun formatHour(date: Long): String {
    val calendar= Calendar.getInstance()
    calendar.timeInMillis= date
    val df= SimpleDateFormat(HOUR_FORMAT)
    return df.format(calendar.time)
}
