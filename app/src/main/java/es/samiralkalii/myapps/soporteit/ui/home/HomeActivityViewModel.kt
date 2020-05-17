package es.samiralkalii.myapps.soporteit.ui.home

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.BaseViewModel
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.SI
import es.samiralkalii.myapps.usecase.usermanagment.GetUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory


class HomeActivityViewModel(private val getUserUseCase: GetUserUseCase) : BaseViewModel() {

    private val logger = LoggerFactory.getLogger(HomeActivityViewModel::class.java)

    companion object {
        const val NAVIGATE_TO_BUNDLE_KEY= "NAV_TO"
        const val IS_EMAIL_VALIDATED_BUNDLE_KEY= "isEmailValidated"
        const val CONFIRMED_BUNDLE_KEY= "confirmed"
    }

    override val uiModel= HomeActivityUiModel()

    override fun init(data: Bundle?) {
        data?.let {
            val navTo= it.getInt(NAVIGATE_TO_BUNDLE_KEY, R.id.homeFragment)
            val isEmailValidated= it.getBoolean(IS_EMAIL_VALIDATED_BUNDLE_KEY, false)
            val isConfirmed= it.getBoolean(CONFIRMED_BUNDLE_KEY, false)
            return init(navTo, isEmailValidated, isConfirmed)
        }
        throw IllegalStateException(
            "Los campos $NAVIGATE_TO_BUNDLE_KEY y $IS_EMAIL_VALIDATED_BUNDLE_KEY son obligatorios")
    }

    private fun init(navTo: Int, isEmailValidated: Boolean, isConfirmed: Boolean) {
        logger.debug("init.........................")
        uiModel._emailValidated.value= isEmailValidated
        uiModel._confirmed.value= isConfirmed
        if (isEmailValidated) {
            when {
                //navTo nos viene desde SplashActivity cuando se entra
                //a la aplicacion desde una notificacion, en caso contrario
                //no viene y su valor por defecto tiene que ser HOME
                navTo!=  R.id.homeFragment -> {
                    uiModel._navTo.value = Event(navTo)
                }
                else -> {
                    viewModelScope.launch {
                        val user = withContext(Dispatchers.IO) {
                            //obtenermos el usuario de sharedPreferences (local)
                            getUserUseCase()
                        }
                        if ((!user.isBoss && user.membershipConfirmation== SI) || (user.isBoss && user.bossConfirmation== SI)) {
                            uiModel._navTo.value = Event(R.id.homeFragment)
                        } else {
                            uiModel._navTo.value = Event(R.id.profileFragmentTopLevel)
                            disableBottomNav()
                        }
                    }
                }
            }
        }
    }

    fun updateUser(user: User, reply: Reply) {
        //user.teamInvitationState= reply
        if (reply== Reply.KO) {
            /*user.teamInvitationState= Reply.NONE
            user.isBoss= ""
            user.team= ""
            user.teamId= ""*/
        }
    }

    private fun disableBottomNav() {
        uiModel._bottomNavEnabled.value= false
    }
}