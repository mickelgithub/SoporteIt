package es.samiralkalii.myapps.soporteit.ui.splash

import android.content.Context
import android.content.Intent
import androidx.lifecycle.Observer
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.BaseActivity
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivity
import es.samiralkalii.myapps.soporteit.ui.util.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class SplashActivity : BaseActivity() {

    private val logger= LoggerFactory.getLogger(SplashActivity::class.java)

    private val viewModel: SplashViewModel by viewModel()

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

    override fun initLoading() {
        viewModel.checkUserAuth()
    }

    private fun processState(screenState: ScreenState.Render<SplashState>?) {
        screenState?.let {
            when (screenState.renderState) {
                is SplashState.LoggedIn -> {
                    logger.debug("Logged in, goto home")
                    startHomeActivityyy(screenState.renderState.user)
                }
                is SplashState.Relogged -> {
                    logger.debug("relogged in, goto home")
                    startHomeActivityyy(screenState.renderState.user)
                }
                SplashState.FirstAccess -> {
                    logger.debug("First access, goto signUp")
                    startLogupActivity()
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

    private fun startHomeActivityyy(user: User) {
        val gotoExtra= intent.getIntExtra(GOTO_KEY, -1)
        HomeActivity.startActivity(user.isEmailVerified, gotoExtra, this)
    }

    companion object {

        const val GOTO_PROFILE= 1
        const val GOTO_NOTIFICATIONS= 2
        const val GOTO_HOME= 3
        const val GOTO_KEY= "GOTO"

        private val logger= LoggerFactory.getLogger(SplashActivity::class.java)


        fun getIntentToProfileScreen(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra(GOTO_KEY, GOTO_PROFILE)
            return intent
        }

        fun getIntentForHome(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java).putExtra(GOTO_KEY, GOTO_NOTIFICATIONS)
            return intent
        }

        fun getIntentToNotificationsScreen(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra(GOTO_KEY, GOTO_NOTIFICATIONS)
            return intent
        }

        enum class GOTO {
            PROFILE, HOME, NOTIFICATIONS
        }
    }
}
