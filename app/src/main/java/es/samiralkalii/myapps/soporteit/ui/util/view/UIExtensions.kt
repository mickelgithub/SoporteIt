package es.samiralkalii.myapps.soporteit.ui.util.view

import android.content.Context
import android.widget.Toast

val PICK_IMAGE= 1
val PERMISSION_REQUEST_CODE= 2
val IMAGE_MIMETYPE= "image/*"

fun Context.toast(msg: String, duration: Int= Toast.LENGTH_SHORT)=
    Toast.makeText(this, msg, duration).show()