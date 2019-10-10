package es.samiralkalii.myapps.soporteit.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.startHomeActivity
import es.samiralkalii.myapps.soporteit.ui.util.startRegistrationActivity


private val TAG= "SplashActivity"
class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: SplashViewModel= ViewModelProviders.of(this)[SplashViewModel::class.java]

        viewModel.checkUserLoggedIn()

        viewModel.splashState.observe(this, Observer {
            if (it is ScreenState.Render) {
                processState(it)
            }
        })
    }

    fun processState(screenState: ScreenState.Render<SplashState>?) {
        screenState?.let {
            when (screenState.renderState) {
                SplashState.LoggedIn, SplashState.GotoHome -> startHomeActivity()
                SplashState.GotoRegister -> startRegistrationActivity()
                is SplashState.ShowMessage -> Unit
            }
        }

    }
}
