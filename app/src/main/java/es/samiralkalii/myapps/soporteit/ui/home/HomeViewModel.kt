package es.samiralkalii.myapps.soporteit.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.usecase.authlogin.GetUserUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class HomeViewModel(val getUserUseCase: GetUserUseCase) : ViewModel() {

    lateinit var user: User

    private val _emailValidated= MutableLiveData<Boolean>()
    val emailValidated: LiveData<Boolean>
        get() = _emailValidated

    init {
        viewModelScope.launch {
            user = async(Dispatchers.IO) {
                getUserUseCase.getUser()
            }.await()
            _emailValidated.value = user.emailVerified
        }
    }

    fun publishUser(userParam: User) {
        user= userParam
        _emailValidated.value= user.emailVerified
    }















}