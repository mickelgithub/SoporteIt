package es.samiralkalii.myapps.soporteit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.util.Event


class HomeViewModel() : ViewModel() {

    lateinit var user: User

    private val _emailValidated= MutableLiveData<Boolean>()
    val emailValidated: LiveData<Boolean>
        get() = _emailValidated


    private val _gotoProfile= MutableLiveData<Event<Boolean>>()
    val gotoProfile: LiveData<Event<Boolean>>
    get() = _gotoProfile

    fun publishUser(userParam: User) {
        user= userParam
        _emailValidated.value= user.emailVerified
        if (user.bossVerification== "N") {
            _gotoProfile.value= Event(true)
        }
    }

    fun updateProfileImage(userParam: User) {
        user.localProfileImage= userParam.localProfileImage
        user.remoteProfileImage= userParam.remoteProfileImage
    }

    fun updateProfile(userParam: User) {
        user.profile= userParam.profile
    }



















}