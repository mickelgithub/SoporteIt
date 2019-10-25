package es.samiralkalii.myapps.soporteit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.samiralkalii.myapps.domain.User


class HomeViewModel() : ViewModel() {

    lateinit var user: User

    private val _emailValidated= MutableLiveData<Boolean>()
    val emailValidated: LiveData<Boolean>
        get() = _emailValidated

    fun publishUser(userParam: User) {
        user= userParam
        _emailValidated.value= user.emailVerified
    }















}