package es.samiralkalii.myapps.soporteit.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import es.samiralkalii.myapps.data.authlogin.AuthRepository
import es.samiralkalii.myapps.data.authlogin.IAuthService
import es.samiralkalii.myapps.soporteit.framework.FirebaseAuthService
import es.samiralkalii.myapps.usecase.authlogin.AuthUseCase

class SplashViewModel() : ViewModel(), IAuthService {


    private val _userLogged= MutableLiveData<Boolean>()
    val userLogged: LiveData<Boolean>
        get() = _userLogged

    val authLoginService: IAuthService = FirebaseAuthService(FirebaseAuth.getInstance(), _userLogged)

    private val authRepository= AuthRepository(authLoginService)

    private val authUseCase= AuthUseCase(authRepository)

    //fun checkUserLogged()= authUseCase.checkUserLoggedIn()

    override fun checkUserLoggedIn() {
        authUseCase.checkUserLoggedIn()
    }

    /*override fun signInUser(mail: String, pass: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }*/

}