package es.samiralkalii.myapps.soporteit.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import es.samiralkalii.myapps.data.authlogin.AuthLoginRepository
import es.samiralkalii.myapps.data.authlogin.AuthLoginService
import es.samiralkalii.myapps.soporteit.framework.FirebaseAuthLoginService
import es.samiralkalii.myapps.usecase.authlogin.AuthUseCase
import es.samiralkalii.myapps.usecase.authlogin.LoginUseCase

class SplashViewModel() : ViewModel() {

    private val _userLogged= MutableLiveData<Boolean>()
    val userLogged: LiveData<Boolean>
        get() = _userLogged

    private val _userLoginSuccess= MutableLiveData<Boolean>()
    val userLoginSuccess
        get() = _userLoginSuccess

    val authLoginService: AuthLoginService = FirebaseAuthLoginService(FirebaseAuth.getInstance(), _userLoginSuccess, _userLogged)

    private val authLoginRepository= AuthLoginRepository(authLoginService)

    private val authUseCase= AuthUseCase(authLoginRepository)
    private val loginUseCase= LoginUseCase(authLoginRepository)

    fun checkUserLogged()= authUseCase.checkUserLoggedIn()

    fun signInUser(name: String, pass: String)= loginUseCase.signInUser(name, pass)

}