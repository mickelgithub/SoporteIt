package es.samiralkalii.myapps.soporteit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val user= User("samir", "samir@gmial.com", "123456789", "pass", "no tengo todavia")
        //Log.d(TAG, user.toString())
    }

    companion object {
        val TAG= "MainActivity"

    }
}
