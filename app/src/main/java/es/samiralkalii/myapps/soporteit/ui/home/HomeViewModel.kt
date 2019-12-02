package es.samiralkalii.myapps.soporteit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity
import es.samiralkalii.myapps.soporteit.ui.util.Event
import org.slf4j.LoggerFactory


class HomeViewModel() : ViewModel() {

    private val logger = LoggerFactory.getLogger(HomeViewModel::class.java)


    lateinit var user: User

    private val _emailValidated= MutableLiveData<Boolean>()
    val emailValidated: LiveData<Boolean>
        get() = _emailValidated


    private val _goto= MutableLiveData<Event<SplashActivity.Companion.GOTO>>()
    val goto: LiveData<Event<SplashActivity.Companion.GOTO>>
    get() = _goto

    private var gotoExtra: Int= -1

    init {
        logger.debug("se ha creado el HomeViewModel")
    }

    fun publishUserAndGoto(userParam: User, gotoParam: Int) {
        gotoExtra= gotoParam
        user= userParam
        _emailValidated.value= user.emailVerified
        if (gotoExtra== SplashActivity.GOTO_PROFILE && user.bossVerification== "N") {
            _goto.value= Event(SplashActivity.Companion.GOTO.PROFILE_PROFILE_NEEDED)
        } else if (gotoExtra== SplashActivity.GOTO_PROFILE) {
            _goto.value= Event(SplashActivity.Companion.GOTO.PROFILE)
        } else {
            _goto.value= Event(SplashActivity.Companion.GOTO.HOME)
        }
    }

    fun updateProfileImage(userParam: User) {
        user.localProfileImage= userParam.localProfileImage
        user.remoteProfileImage= userParam.remoteProfileImage
    }

    fun updateProfile(userParam: User) {
        user.profile= userParam.profile
    }

    fun updateGoto(gotoParam: SplashActivity.Companion.GOTO) {
        _goto.value= Event(gotoParam)
    }



















}