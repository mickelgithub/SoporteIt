package es.samiralkalii.myapps.soporteit.ui.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivity
import es.samiralkalii.myapps.soporteit.ui.logup.LogupActivity
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity


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
