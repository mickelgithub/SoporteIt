package es.samiralkalii.myapps.soporteit.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.startHomeActivity
import es.samiralkalii.myapps.soporteit.ui.util.startLogupActivity
import es.samiralkalii.myapps.soporteit.ui.util.toBundle
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModel()

    private val logger= LoggerFactory.getLogger(SplashActivity::class.java)

    private var gotoExtra: Int= -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gotoExtra= intent.getIntExtra(SplashActivity.GOTO_KEY, -1)

        viewModel.checkUserAuth()

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
                    startHomeActivity(screenState.renderState.user.toBundle(), gotoExtra)
                }
                is SplashState.Relogged -> {
                    logger.debug("relogged in, goto home")
                    startHomeActivity(screenState.renderState.user.toBundle(), gotoExtra)
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

    companion object {

        const val GOTO_PROFILE= 1
        const val GOTO_KEY= "GOTO"


        fun getIntentToProfileScreen(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra(GOTO_KEY, GOTO_PROFILE)
            return intent
        }

        enum class GOTO {
            PROFILE, PROFILE_PROFILE_NEEDED, HOME, TEAM_MANAGEMENT
        }
    }
}
