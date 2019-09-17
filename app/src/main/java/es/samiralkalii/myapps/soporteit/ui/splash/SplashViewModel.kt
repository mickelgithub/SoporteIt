package es.samiralkalii.myapps.soporteit.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import es.samiralkalii.myapps.data.authlogin.AuthRepository
import es.samiralkalii.myapps.data.authlogin.IAuthService
import es.samiralkalii.myapps.soporteit.framework.FirebaseAuthService
import es.samiralkalii.myapps.usecase.authlogin.AuthUseCase

class SplashViewModel() : ViewModel() {


    private val _userLogged= MutableLiveData<Boolean>()
    val userLogged: LiveData<Boolean>
        get() = _userLogged

    val authService: IAuthService = FirebaseAuthService(FirebaseAuth.getInstance())

    private val authRepository= AuthRepository(authService)

    private val authUseCase= AuthUseCase(authRepository)

    fun checkUserLoggedIn() {
        _userLogged.value= authUseCase.checkUserLoggedIn()
    }

}