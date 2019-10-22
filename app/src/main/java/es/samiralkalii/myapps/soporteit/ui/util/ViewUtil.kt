package es.samiralkalii.myapps.soporteit.ui.util

import android.content.Context
import android.content.Intent
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivity
import es.samiralkalii.myapps.soporteit.ui.logup.LogupActivity

fun Context.startLogupActivity()= Intent(this, LogupActivity::class.java).also {
    it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(it)
}

fun Context.startHomeActivity()= Intent(this, HomeActivity::class.java).also {
    it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(it)
}

