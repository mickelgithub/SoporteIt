package es.samiralkalii.myapps.soporteit.ui.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivity
import es.samiralkalii.myapps.soporteit.ui.logup.LogupActivity
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity


fun Context.toast(msg: String, duration: Int= Toast.LENGTH_SHORT)=
    Toast.makeText(this, msg, duration).show()

/*fun Context.startLogupActivity()= Intent(this, LogupActivity::class.java).also {
    //it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
    it.flags= Intent.FLAG_ACTIVITY_NO_ANIMATION
    startActivity(it)
}*/

/*fun Context.startHomeActivity(bundle: Bundle, goto: Int= -1)= Intent(this, HomeActivity::class.java).also {
    val args= bundle
    if (goto> 0) {
        args.putInt(SplashActivity.GOTO_KEY, goto)
    }
    it.putExtras(args)
    it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(it)
}*/

fun Context.convertPixelsToDp(px: Float): Float {
    return px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.convertDpToPixels(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
}

fun AppCompatActivity.hideSystemUI() {
    val uiOptions: Int = window.getDecorView().getSystemUiVisibility()
    var newUiOptions = uiOptions

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

fun AppCompatActivity.requestStoragePermissions() {
    if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
        Toast.makeText(this, getString(es.samiralkalii.myapps.soporteit.R.string.read_permission_indication), Toast.LENGTH_LONG).show();
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    } else {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }
}

fun AppCompatActivity.checkStoragePermission(): Boolean {
    val result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
    return result == PackageManager.PERMISSION_GRANTED
}

