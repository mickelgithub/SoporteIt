package es.samiralkalii.myapps.soporteit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.usecase.usermanagment.GetUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory


class HomeViewModel(private val getUserUseCase: GetUserUseCase) : ViewModel() {

    private val logger = LoggerFactory.getLogger(HomeViewModel::class.java)

    private val _emailValidated= MutableLiveData<Boolean>()
    val emailValidated: LiveData<Boolean>
        get() = _emailValidated

    private val _goto= MutableLiveData<Event<SplashActivity.Companion.GOTO>>()
    val goto: LiveData<Event<SplashActivity.Companion.GOTO>>
    get() = _goto

    private var gotoExtra: Int= -1

    private lateinit var user: User

    fun init(gotoParam: Int, isEmailVerified: Boolean) {
        logger.debug("init.........................")
        gotoExtra= gotoParam
        _emailValidated.value= isEmailVerified
        if (isEmailVerified) {
            when {
                //gotoExtra nos viene desde SplashActivity cuando se realiza alguna
                //accion sobre las notificaciones, por ejemplo pulsar en la notificacion,
                //sino, tendria el valor por defecto -1
                gotoExtra == SplashActivity.GOTO_PROFILE -> {
                    _goto.value = Event(SplashActivity.Companion.GOTO.PROFILE)
                }
                gotoExtra == SplashActivity.GOTO_NOTIFICATIONS -> {
                    _goto.value = Event(SplashActivity.Companion.GOTO.NOTIFICATIONS)
                }
                else -> {
                    viewModelScope.launch {
                        user = async(Dispatchers.IO) {
                            getUserUseCase()
                        }.await()
                        if ((!user.isBoss && user.membershipConfirmation== "Y") || (user.isBoss && user.bossConfirmation== "Y")) {
                            _goto.value = Event(SplashActivity.Companion.GOTO.HOME)
                        } else {
                            _goto.value = Event(SplashActivity.Companion.GOTO.PROFILE)
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

    fun updateGoto(gotoParam: SplashActivity.Companion.GOTO) {
        _goto.value= Event(gotoParam)
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

}