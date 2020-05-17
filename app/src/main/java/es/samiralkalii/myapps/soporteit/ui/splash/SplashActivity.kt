package es.samiralkalii.myapps.soporteit.ui.splash

import androidx.lifecycle.Observer
import es.samiralkalii.myapps.soporteit.ui.BaseActivity
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivity
import es.samiralkalii.myapps.soporteit.ui.logup.LogupActivity
import es.samiralkalii.myapps.soporteit.ui.util.SI
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.hideSystemUI
import es.samiralkalii.myapps.soporteit.ui.util.toast
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class SplashActivity : BaseActivity() {

    private val logger= LoggerFactory.getLogger(SplashActivity::class.java)

    override val viewModel: SplashActivityViewModel by viewModel()

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
                    val user= screenState.renderState.user
                    val confirmed= (user.isBoss && user.bossConfirmation== SI ||
                            !user.isBoss && user.membershipConfirmation== SI)
                    HomeActivity.startActivity(this, user.isEmailVerified,
                        confirmed, intent.extras)
                    finish()
                }
                is SplashState.Relogged -> {
                    logger.debug("relogged in, goto home")
                    val user= screenState.renderState.user
                    val confirmed= (user.isBoss && user.bossConfirmation== SI ||
                            !user.isBoss && user.membershipConfirmation== SI)
                    HomeActivity.startActivity(this, screenState.renderState.user.isEmailVerified,
                        confirmed, intent.extras)
                    finish()
                }
                SplashState.FirstAccess -> {
                    logger.debug("First access, goto signUp")
                    LogupActivity.startLogupActivity(this)
                    finish()
                }
                is SplashState.ShowMessage -> {
                    logger.debug("Error SignIn")
                    toast(resources.getString(screenState.renderState.message))
                    finish()
                }
            }
        }
    }

}