package es.samiralkalii.myapps.soporteit.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    abstract fun initUI()
    abstract fun initStateObservation()
    open fun initLoading() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initUI()
        initLoading()
        initStateObservation()
    }

}