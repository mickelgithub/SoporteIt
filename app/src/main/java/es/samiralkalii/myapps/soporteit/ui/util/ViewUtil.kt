package es.samiralkalii.myapps.soporteit.ui.util

import android.content.Context
import android.content.Intent
import es.samiralkalii.myapps.soporteit.ui.register.RegisterActivity

fun Context.startRegistrationActivity()= Intent(this, RegisterActivity::class.java).also {
    it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(it)
}

