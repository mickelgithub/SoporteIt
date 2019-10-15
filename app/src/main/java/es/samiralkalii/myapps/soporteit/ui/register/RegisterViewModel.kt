package es.samiralkalii.myapps.soporteit.ui.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.authlogin.RegisterUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


private val TAG= "RegisterViewModel"

class RegisterViewModel(val registerUseCase: RegisterUseCase) : ViewModel() {

    private val _registerState= MutableLiveData<ScreenState<RegisterState>>()
    val registerState
        get() = _registerState

    private val _progressbarVisible= MutableLiveData<Boolean>()
    val progressVisible
        get() = _progressbarVisible

    val user= User()

    private val _imageProfile= MutableLiveData<String?>()
    val imageProfile
        get() = _imageProfile


    /*private val userAccessFramework: IUserAccess = UserAccess(FirebaseAuth.getInstance())
    private val userDatabaseFramework: IUserDatabase = UserDatabase(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    private val sharedPreferencesFramework: IPreferences = MySharedPreferences(context)

    private val userAccessRepository= UserAccessRepository(userAccessFramework)
    private val userDatabaseRepository= UserDatabaseRepository(userDatabaseFramework)
    private val preferenceRepository= PreferenceRepository(sharedPreferencesFramework)

    private val registerUseCase= RegisterUseCase(userAccessRepository, userDatabaseRepository, preferenceRepository) */

    fun registerUser() {
        _progressbarVisible.value= true
        val errorHandler = CoroutineExceptionHandler { _, error ->
            _progressbarVisible.postValue(false)
            Log.e(TAG, error.toString()+ "....................")
            when (error) {
                is FirebaseNetworkException -> {
                    _registerState.postValue(ScreenState.Render(RegisterState.ShowMessage("Error de conexion, comprueba el acceso a Internet")))
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    if (error.toString().contains("email address is badly formatted")) {
                        _registerState.postValue(ScreenState.Render(RegisterState.ShowMessage("Email no válido al tener formato incorrecto")))
                    } else if (error.toString().contains("The given password is invalid")) {
                        _registerState.postValue(ScreenState.Render(RegisterState.ShowMessage("La contraseña debe ser de al menos 6 posiciones")))
                    }
                }
                is FirebaseAuthUserCollisionException -> {
                    _registerState.postValue(ScreenState.Render(RegisterState.ShowMessage("Ya existe ese usuario!!!")))
                }
                else -> {
                    _registerState.postValue(ScreenState.Render(RegisterState.ShowMessage("Error no controlado!!!")))
                }
            }
        }
        viewModelScope.launch(errorHandler) {
            val result= async(Dispatchers.IO) {
                registerUseCase.registerUser(user)
            }.await()
            when (result) {
                is RegisterUseCase.Result.RegisteredOk -> {
                    _progressbarVisible.value = false
                    _registerState.value = ScreenState.Render(RegisterState.RegisteredOk)
                }
            }
        }
    }

    fun onRegisterClick()= registerUser()

    fun saveProfileImage(file: String?) {



    }

    fun updateImageProfile(imgFilePath: String?) {
        _imageProfile.value= imgFilePath
    }

}