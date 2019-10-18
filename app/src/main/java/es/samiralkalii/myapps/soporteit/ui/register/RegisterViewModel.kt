package es.samiralkalii.myapps.soporteit.ui.register

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.authlogin.RegisterUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory


class RegisterViewModel(val registerUseCase: RegisterUseCase) : ViewModel() {

    private val logger = LoggerFactory.getLogger(RegisterViewModel::class.java!!)

    private val _registerState= MutableLiveData<ScreenState<RegisterState>>()
    val registerState
        get()= _registerState

    private val _progressbarVisible= MutableLiveData<Boolean>()
    val progressVisible
        get()= _progressbarVisible

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

    private val _nameVisible= MutableLiveData<Boolean>(true)
    val nameVisible
        get()= _nameVisible

    private fun clearErrors() {
        _nameError.value= null
        _emailError.value= null
        _passwordError.value= null
    }

    fun registerUser() {

        clearErrors()

        if (user.name.isBlank() || user.name.length<= 4) {
            _nameError.value= R.string.name_incorrect_message_error
        } else {
            _progressbarVisible.value= true
            val errorHandler = CoroutineExceptionHandler { _, error ->
                _progressbarVisible.postValue(false)
                logger.error(error.toString(), error)
                when (error) {
                    is FirebaseNetworkException -> {
                        _registerState.postValue(ScreenState.Render(RegisterState.ShowMessage(R.string.no_internet_connection)))
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        if (error.toString().contains("email address is badly formatted")) {
                            //_registerState.postValue(ScreenState.Render(RegisterState.ShowMessage("Email no válido al tener formato incorrecto")))
                            _emailError.postValue(R.string.email_incorrect_message_error)
                        } else if (error.toString().contains("The given password is invalid")) {
                            //_registerState.postValue(ScreenState.Render(RegisterState.ShowMessage("La contraseña debe ser de al menos 6 posiciones")))
                            _passwordError.postValue(R.string.password_incorrect_message_error)
                        }
                    }
                    is FirebaseAuthUserCollisionException -> {
                        _registerState.postValue(ScreenState.Render(RegisterState.ShowMessage(R.string.user_collision)))
                    }
                    else -> {
                        _registerState.postValue(ScreenState.Render(RegisterState.ShowMessage(R.string.no_internet_connection)))
                    }
                }
            }

            viewModelScope.launch(errorHandler) {
                val result= async(Dispatchers.IO) {
                    registerUseCase.registerUser(user, imageProfile.value?.toString() ?: "")
                }.await()
                when (result) {
                    is RegisterUseCase.Result.RegisteredOk -> {
                        _progressbarVisible.value = false
                        _registerState.value = ScreenState.Render(RegisterState.RegisteredOk)
                    }
                }
            }
        }
    }

    fun onRegisterClick()= registerUser()

    fun updateImageProfile(imgUri: Uri?) {
        _imageProfile.value= imgUri
    }

    fun onAlreadyLoggedUpClick() {
        logger.debug("already logged up")
        _nameVisible.value= false
    }

}