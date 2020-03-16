package es.samiralkalii.myapps.soporteit.ui.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivity
import es.samiralkalii.myapps.soporteit.ui.logup.LogupActivity
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity


fun Context.toast(msg: String, duration: Int= Toast.LENGTH_SHORT)=
    Toast.makeText(this, msg, duration).show()

fun Context.startLogupActivity()= Intent(this, LogupActivity::class.java).also {
    it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
    startActivity(it)
}

fun Context.startHomeActivity(bundle: Bundle, goto: Int= -1)= Intent(this, HomeActivity::class.java).also {
    val superBundle= bundle
    if (goto> 0) {
        superBundle.putInt(SplashActivity.GOTO_KEY, goto)
    }
    it.putExtras(superBundle)
    it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(it)
}

fun Context.convertPixelsToDp(px: Float): Float {
    return px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.convertDpToPixels(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
}

fun AppCompatActivity.hideSystemUI() {
    val uiOptions: Int = window.getDecorView().getSystemUiVisibility()
    var newUiOptions = uiOptions
    val isImmersiveModeEnabled =
        uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions

    if (Build.VERSION.SDK_INT >= 14) {
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }
    if (Build.VERSION.SDK_INT >= 16) {
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
    }
    if (Build.VERSION.SDK_INT >= 18) {
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
    window.getDecorView().setSystemUiVisibility(newUiOptions)

}