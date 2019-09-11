package es.samiralkalii.myapps.soporteit.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import es.samiralkalii.myapps.data.splash.AuthService
import es.samiralkalii.myapps.data.splash.SplashRepository
import es.samiralkalii.myapps.soporteit.framework.FirebaseAuthService

class SplashViewModel() : ViewModel() {

    private val _userLogged= MutableLiveData<Boolean>()
    val userLogged: LiveData<Boolean>
        get() = _userLogged

    private val _userLoginSuccess= MutableLiveData<Boolean>()
    val userLoginSuccess
        get() = _userLoginSuccess

    val authService: AuthService= FirebaseAuthService(FirebaseAuth.getInstance(), _userLoginSuccess, _userLogged)

    private val splashRepository= SplashRepository(authService)

    fun checkUserLogged()= splashRepository.checkUserLoggedIn()

    fun signInUser(name: String, pass: String)= splashRepository.signInUser(name, pass)

}