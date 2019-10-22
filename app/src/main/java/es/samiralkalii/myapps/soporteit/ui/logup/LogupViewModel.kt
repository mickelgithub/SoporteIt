package es.samiralkalii.myapps.soporteit.ui.logup

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.authlogin.LoginUserCase
import es.samiralkalii.myapps.usecase.authlogin.LogupUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

class LogupViewModel(val logupUseCase: LogupUseCase, val loginUserCase: LoginUserCase) : ViewModel() {

    private val logger = LoggerFactory.getLogger(LogupViewModel::class.java)

    private val _registerState= MutableLiveData<ScreenState<LogupState>>()
    val registerState
        get()= _registerState

    private val _loginState= MutableLiveData<ScreenState<LoginState>>()
    val loginState
        get()= _loginState

    private val _progressVisible= MutableLiveData<Boolean>(false)
    val progressVisible
        get()= _progressVisible

    val user= User()

    private val _imageProfile= MutableLiveData<Uri?>()
    val imageProfile
        get()= _imageProfile

    private val _nameError= MutableLiveData<Int?>()
    val nameError
        get()= _nameError

    private val _emailError= MutableLiveData<Int?>()
    val emailError
        get()= _emailError

    private val _passwordError= MutableLiveData<Int?>()
    val passwordError
        get()= _passwordError

    private val _loginOrLogUp= MutableLiveData<Int>(0)
    val loginOrLogUp
        get()= _loginOrLogUp

    private fun clearErrorsLogUp() {
        _nameError.value= null
        _emailError.value= null
        _passwordError.value= null
    }

    private fun clearErrorsLogin() {
        _emailError.value= null
        _passwordError.value= null
    }

    private fun loginUser() {

        clearErrorsLogin()

        _progressVisible.value= true
        val errorHandler = CoroutineExceptionHandler { _, error ->
            _progressVisible.postValue(false)
            logger.error(error.toString(), error)
            when (error) {
                is FirebaseNetworkException -> {
                    _loginState.postValue(ScreenState.Render(LoginState.ShowMessage(R.string.no_internet_connection)))
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    if (error.toString().contains("email address is badly formatted")) {
                        _emailError.postValue(R.string.email_incorrect_message_error)
                    } else if (error.toString().contains("The password is invalid")) {
                        _passwordError.postValue(R.string.password_incorrect_login_message_error)
                    }
                }
                is FirebaseAuthInvalidUserException -> {
                    if (error.toString().contains("There is no user record corresponding to this identifier")) {
                        _emailError.postValue(R.string.user_not_exist_message_error)
                    }
                }
                else -> {
                    _loginState.postValue(ScreenState.Render(LoginState.ShowMessage(R.string.no_internet_connection)))
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            val result= async(Dispatchers.IO) {
                 val result= loginUserCase.loginUser(user)
                if (!user.localProfileImage.isBlank()) {
                    _imageProfile.postValue(Uri.fromFile(File(user.localProfileImage)))
                }
                result
            }.await()
            when (result) {
                is LoginUserCase.Result.LoginOk -> {
                    _progressVisible.value = false
                    _loginState.value = ScreenState.Render(LoginState.LoginOk)
                }
            }
        }
    }

    fun registerUser() {

        clearErrorsLogUp()

        if (user.name.isBlank() || user.name.length< 4) {
            _nameError.value = R.string.name_incorrect_message_error
        } else if (user.email.isBlank()) {
            _emailError.value = R.string.email_incorrect_message_error
        } else if (user.password.isBlank()) {
            _passwordError.value= R.string.password_incorrect_logup_message_error
        } else {
            _progressVisible.value= true
            val errorHandler = CoroutineExceptionHandler { _, error ->
                _progressVisible.postValue(false)
                logger.error(error.toString(), error)
                when (error) {
                    is FirebaseNetworkException -> {
                        _registerState.postValue(ScreenState.Render(LogupState.ShowMessage(R.string.no_internet_connection)))
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        if (error.toString().contains("email address is badly formatted")) {
                            //_registerState.postValue(ScreenState.Render(LogupState.ShowMessage("Email no válido al tener formato incorrecto")))
                            _emailError.postValue(R.string.email_incorrect_message_error)
                        } else if (error.toString().contains("The given password is invalid")) {
                            //_registerState.postValue(ScreenState.Render(LogupState.ShowMessage("La contraseña debe ser de al menos 6 posiciones")))
                            _passwordError.postValue(R.string.password_incorrect_logup_message_error)
                        }
                    }
                    is FirebaseAuthUserCollisionException -> {
                        _registerState.postValue(ScreenState.Render(LogupState.ShowMessage(R.string.user_collision)))
                    }
                    else -> {
                        _registerState.postValue(ScreenState.Render(LogupState.ShowMessage(R.string.no_internet_connection)))
                    }
                }
            }

            viewModelScope.launch(errorHandler) {
                val result= async(Dispatchers.IO) {
                    logupUseCase.logupUser(user, imageProfile.value?.toString() ?: "")
                }.await()
                when (result) {
                    is LogupUseCase.Result.RegisteredOk -> {
                        _progressVisible.value = false
                        _registerState.value = ScreenState.Render(LogupState.RegisteredOk)
                    }
                }
            }
        }
    }

    fun onRegisterClick()= registerUser()

    fun onLogInClick()= loginUser()

    fun updateImageProfile(imgUri: Uri?) {
        _imageProfile.value= imgUri
    }

    fun onAlreadyLoggedUpClick() {
        _loginOrLogUp.value= TO_LOG_IN
        updateImageProfile(null)
        clearErrorsLogUp()
    }

    fun noAcountClick() {
        _loginOrLogUp.value= TO_LOG_UP
        clearErrorsLogin()
    }

    companion object {
        val TO_LOG_UP= 0
        val TO_LOG_IN= 1
    }

}