package es.samiralkalii.myapps.soporteit.ui

import android.content.Context
import android.content.Intent
import es.samiralkalii.myapps.soporteit.ui.login.LoginActivity

fun Context.startLoginActivity()= Intent(this, LoginActivity::class.java).also {
    it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(it)
}