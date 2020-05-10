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
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException


class HomeViewModel(private val getUserUseCase: GetUserUseCase) : BaseViewModel() {

    private val logger = LoggerFactory.getLogger(HomeViewModel::class.java)

    companion object {
        const val NAVIGATE_TO_BUNDLE_KEY= "NAV_TO"
        const val IS_EMAIL_VALIDATED_BUNDLE_KEY= "isEmailValidated"
    }

    override val uiModel= HomeViewModelUiModel()

    override fun init(data: Bundle?) {
        data?.let {
            val navTo= it.getInt(NAVIGATE_TO_BUNDLE_KEY, R.id.homeFragment)
            val isEmailValidated= it.getBoolean(IS_EMAIL_VALIDATED_BUNDLE_KEY, false)
            return init(navTo, isEmailValidated)
        }
        throw IllegalStateException(
            "Los campos ${NAVIGATE_TO_BUNDLE_KEY} y ${IS_EMAIL_VALIDATED_BUNDLE_KEY} son obligatorios")
    }

    private fun init(navTo: Int, isEmailValidated: Boolean) {
        logger.debug("init.........................")
        uiModel._emailValidated.value= isEmailValidated
        if (isEmailValidated) {
            when {
                //goto nos viene desde SplashActivity cuando se entra
                //a la aplicacion desde una notificacion, en caso contrario
                //no viene y su valor por defecto tiene que ser HOME
                navTo in setOf(R.id.profileFragment, R.id.notificationsFragment,
                    R.id.schedulersFragment, R.id.holidaysFragment, R.id.absencesFragment) -> {
                    uiModel._navTo.value = Event(navTo)
                }
                else -> {
                    viewModelScope.launch {
                        val user = async(Dispatchers.IO) {
                            //obtenermos el usuario de sharedPreferences (local)
                            getUserUseCase()
                        }.await()
                        if ((!user.isBoss && user.membershipConfirmation== SI) || (user.isBoss && user.bossConfirmation== SI)) {
                            uiModel._navTo.value = Event(R.id.homeFragment)
                        } else {
                            uiModel._navTo.value = Event(R.id.profileFragment)
                            disableBottomNav()
                        }
                    }
                }
            }
        }

        /*if (gotoExtra== SplashActivity.GOTO_PROFILE && user.bossVerification== "N") {

        } else if (gotoExtra== SplashActivity.GOTO_PROFILE) {
            _goto.value = Event(SplashActivity.Companion.GOTO.PROFILE)
        } else if (user.bossVerification== "P") {
            _goto.value = Event(SplashActivity.Companion.GOTO.PROFILE)
        } else if (user.isBoss() && !user.teamCreated){
            //si es responsable de equipo y no tiene todavia creado ningun equipo, le dirigimos
            //a la pantalla de gestion de equipo
            _goto.value= Event(SplashActivity.Companion.GOTO.TEAM_MANAGEMENT)
        } else {
            _goto.value= Event(SplashActivity.Companion.GOTO.HOME)
        }*/
    }

    fun updateProfileImage(userParam: User) {
        //user.profileImage= userParam.profileImage
        //user.remoteProfileImage= userParam.remoteProfileImage
    }

    fun updateTeamCreated(user: User) {
        //this.user.team= user.team
        //this.user.teamId= user.teamId
    }

    fun updateProfile(userParam: User) {
        //user.profile= userParam.profile
    }

    fun updateBossVerification(bossVerification: String) {
        //user.bossVerified= bossVerification
    }

    /*fun updateGoto(gotoParam: GOTO) {
        uiModel._goto.value= Event(gotoParam)
    }*/

    fun updateUser(user: User, reply: Reply) {
        //user.teamInvitationState= reply
        if (reply== Reply.KO) {
            /*user.teamInvitationState= Reply.NONE
            user.isBoss= ""
            user.team= ""
            user.teamId= ""*/
        }
    }

    fun disableBottomNav() {
        uiModel._bottomNavEnabled.value= false
    }



}