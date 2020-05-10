package es.samiralkalii.myapps.soporteit.ui.splash

import androidx.lifecycle.Observer
import es.samiralkalii.myapps.soporteit.ui.BaseActivity
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivity
import es.samiralkalii.myapps.soporteit.ui.logup.LogupActivity
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.hideSystemUI
import es.samiralkalii.myapps.soporteit.ui.util.toast
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class SplashActivity : BaseActivity() {

    private val logger= LoggerFactory.getLogger(SplashActivity::class.java)

    override val viewModel: SplashViewModel by viewModel()

    override fun initUI() {
        hideSystemUI()
    }

    override fun initStateObservation() {
        viewModel.splashState.observe(this, Observer {
            if (it is ScreenState.Render) {
                processState(it)
            }
        })
    }

    private fun processState(screenState: ScreenState.Render<SplashState>?) {
        screenState?.let {
            when (screenState.renderState) {
                is SplashState.LoggedIn -> {
                    logger.debug("Logged in, goto home")
                    HomeActivity.startActivity(this, screenState.renderState.user.isEmailVerified, intent.extras)
                }
                is SplashState.Relogged -> {
                    logger.debug("relogged in, goto home")
                    HomeActivity.startActivity(this, screenState.renderState.user.isEmailVerified, intent.extras)
                }
                SplashState.FirstAccess -> {
                    logger.debug("First access, goto signUp")
                    LogupActivity.startLogupActivity(this)
                    this.finish()
                }
                is SplashState.ShowMessage -> {
                    logger.debug("Error SignIn")
                    toast(resources.getString(screenState.renderState.message))
                    this.finish()
                }
            }
        }
    }

}