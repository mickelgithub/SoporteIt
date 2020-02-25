package es.samiralkalii.myapps.soporteit.ui.splash

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.RemoteInput
import androidx.lifecycle.Observer
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.soporteit.framework.notification.KEY_TEXT_REPLY
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.startHomeActivity
import es.samiralkalii.myapps.soporteit.ui.util.startLogupActivity
import es.samiralkalii.myapps.soporteit.ui.util.toBundle
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModel()

    private val logger= LoggerFactory.getLogger(SplashActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                    startHomeActivityyy(screenState.renderState.user)
                }
                is SplashState.Relogged -> {
                    logger.debug("relogged in, goto home")
                    startHomeActivityyy(screenState.renderState.user)
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

    private fun startHomeActivityyy(user: User) {
        val gotoExtra= intent.getIntExtra(SplashActivity.GOTO_KEY, -1)
        val replyTeamInvitacion= intent.getStringExtra(SplashActivity.REPLY_TEAM_INVITATION_KEY) ?: ""
        val notifId= intent.getStringExtra(SplashActivity.NOTIF_ID_KEY) ?: ""
        val replyTeamInvitacionText= RemoteInput.getResultsFromIntent(intent)?.getCharSequence(KEY_TEXT_REPLY) ?: ""
        user.teamInvitationState= replyTeamInvitacion
        if (replyTeamInvitacion.isNotBlank()) {
            viewModel.publishUser(user)
            viewModel.handleTeamInvitacion(user, (if (replyTeamInvitacion== REPLY_TEAM_INVITATION_OK) Reply.OK else Reply.KO), replyTeamInvitacionText.toString(), notifId)
        }
        startHomeActivity(user.toBundle(), gotoExtra)
    }

    companion object {

        const val GOTO_PROFILE= 1
        const val GOTO_NOTIFICATIONS= 2
        const val GOTO_HOME= 3
        const val GOTO_KEY= "GOTO"
        const val REPLY_TEAM_INVITATION_KEY= "accept_team_invitation"
        const val REPLY_TEAM_INVITATION_OK= "S"
        const val REPLY_TEAM_INVITATION_KO= "N"
        const val NOTIF_ID_KEY= "notifi_id"

        private val logger= LoggerFactory.getLogger(SplashActivity::class.java)


        fun getIntentToProfileScreen(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra(GOTO_KEY, GOTO_PROFILE)
            return intent
        }

        fun getIntentToNotificationsScreen(context: Context): Intent {
            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra(GOTO_KEY, GOTO_NOTIFICATIONS)
            return intent
        }

        fun getIntentToHomeScreen(context: Context, replyTeamInvitacion: String= "", notificationId: String): Intent {
            logger.debug("*******El valor de replyTeamInvitation es ${replyTeamInvitacion} y el GOTO es ${GOTO_HOME}")
            val intent = Intent(context, SplashActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }.putExtra(GOTO_KEY, GOTO_HOME).putExtra(REPLY_TEAM_INVITATION_KEY, replyTeamInvitacion).putExtra(NOTIF_ID_KEY, notificationId)
            return intent
        }

        enum class GOTO {
            PROFILE, PROFILE_PROFILE_NEEDED, HOME, NOTIFICATIONS
        }
    }
}
