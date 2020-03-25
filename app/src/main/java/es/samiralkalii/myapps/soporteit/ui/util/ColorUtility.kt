package es.samiralkalii.myapps.soporteit.ui.util

import android.graphics.Color
import java.util.*


fun getRandomColor(): Pair<Int, Int> {

    val RGB = 0xff + 1
    val colors = IntArray(2)
    val a = 256
    val r1 = Math.floor(Math.random() * RGB).toInt()
    val r2 = Math.floor(Math.random() * RGB).toInt()
    val r3 = Math.floor(Math.random() * RGB).toInt()
    val c1 = Color.rgb(r1, r2, r3)
    var c2: Int?= null
    if (r1 + r2 + r3 > 450) {
        c2 = Color.parseColor("#222222")
    } else {
        c2 = Color.parseColor("#ffffff")
    }
    return (c1 to c2)

}