package es.samiralkalii.myapps.soporteit.ui.splash

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.startHomeActivity
import es.samiralkalii.myapps.soporteit.ui.util.startRegistrationActivity
import org.koin.android.viewmodel.ext.android.viewModel

private val TAG= "SplashActivity"

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.splashState.observe(this, Observer {
            if (it is ScreenState.Render) {
                processState(it)
            }
        })
    }

    override fun onResume() {
        super.onResume()

        viewModel.checkUserAuth()
    }

    fun processState(screenState: ScreenState.Render<SplashState>?) {
        screenState?.let {
            when (screenState.renderState) {
                SplashState.LoggedIn -> {
                    Log.d(TAG, "Logged in, goto home")
                    startHomeActivity()
                }
                SplashState.Relogged -> {
                    Log.d(TAG, "relogged in, goto home")
                    startHomeActivity()
                }
                SplashState.FirstAccess -> {
                    Log.d(TAG, "First access, goto signIn")
                    startRegistrationActivity()
                }
                is SplashState.ShowMessage -> {
                    Log.d(TAG, "Error SignIn")
                    Toast.makeText(this, screenState.renderState.message, Toast.LENGTH_SHORT).show()
                    this.finish()
                }
            }
        }
    }
}
