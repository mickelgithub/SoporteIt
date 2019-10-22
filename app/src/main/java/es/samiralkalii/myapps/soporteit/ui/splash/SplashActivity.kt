package es.samiralkalii.myapps.soporteit.ui.splash

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.startHomeActivity
import es.samiralkalii.myapps.soporteit.ui.util.startLogupActivity
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModel()

    private val logger= LoggerFactory.getLogger(SplashActivity::class.java)

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

    private fun processState(screenState: ScreenState.Render<SplashState>?) {
        screenState?.let {
            when (screenState.renderState) {
                SplashState.LoggedIn -> {
                    logger.debug("Logged in, goto home")
                    startHomeActivity()
                }
                SplashState.Relogged -> {
                    logger.debug("relogged in, goto home")
                    startHomeActivity()
                }
                SplashState.FirstAccess -> {
                    logger.debug("First access, goto signUp")
                    startLogupActivity()
                }
                is SplashState.ShowMessage -> {
                    logger.debug("Error SignIn")
                    Toast.makeText(this, screenState.renderState.message, Toast.LENGTH_SHORT).show()
                    this.finish()
                }
            }
        }
    }
}
