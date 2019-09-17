package es.samiralkalii.myapps.soporteit.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.samiralkalii.myapps.soporteit.ui.startRegistrationActivity


private val TAG= "SplashActivity"
class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SplashViewModel= ViewModelProviders.of(this)[SplashViewModel::class.java]

        viewModel.checkUserLoggedIn()

        viewModel.userLogged.observe(this, Observer {
            if (it) {
                finish()
            } else {
                startRegistrationActivity()
            }
        })
    }
}
