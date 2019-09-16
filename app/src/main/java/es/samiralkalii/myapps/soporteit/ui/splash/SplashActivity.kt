package es.samiralkalii.myapps.soporteit.ui.splash

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.samiralkalii.myapps.soporteit.ui.startLoginActivity


private val TAG= "SplashActivity"
class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_splash)

        val viewModel: SplashViewModel= ViewModelProviders.of(this)[SplashViewModel::class.java]

        viewModel.checkUserLoggedIn()

        viewModel.userLogged.observe(this, Observer {
            if (it) {
                Log.d(TAG, "SIIIIIII LOGADO")
                finish()
            } else {
                Log.d(TAG, "NOOOOOO LOGADO")
                //startActivity(Intent(this, LoginActivity::class.java))
                startLoginActivity()
            }
        })
    }
}
