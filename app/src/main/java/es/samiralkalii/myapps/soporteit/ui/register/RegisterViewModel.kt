package es.samiralkalii.myapps.soporteit.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import es.samiralkalii.myapps.data.authlogin.ILoginService
import es.samiralkalii.myapps.data.authlogin.LoginRepository
import es.samiralkalii.myapps.soporteit.framework.FirebaseLoginService

class RegisterViewModel:  ViewModel(), ILoginService {

    private val _loginSuccessfull= MutableLiveData<Boolean>()
    val loginSuccessFull
        get() = _loginSuccessfull

    val loginService= FirebaseLoginService(FirebaseAuth.getInstance(), _loginSuccessfull)

    val loginRepository= LoginRepository(loginService)


    override fun signInUser(mail: String, pass: String) {

    }
}