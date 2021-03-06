package es.samiralkalii.myapps.soporteit.ui.logup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.common.AreasDepartments
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.authlogin.LoginUserCase
import es.samiralkalii.myapps.usecase.authlogin.LogupUseCase
import es.samiralkalii.myapps.usecase.common.GetAreasDepartmentsUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File


private const val CHOOSE_PROFILE= "Elige tu perfil"

class LogupViewModel(private val logupUseCase: LogupUseCase, private val loginUserCase: LoginUserCase, private val getAreasDepartmentsUseCase: GetAreasDepartmentsUseCase) : ViewModel() {

    private val logger = LoggerFactory.getLogger(LogupViewModel::class.java)

    var name= ""
    var email= ""
    var password= ""
    var passwordConfirmation= ""
    var localProfileImage= ""

    private lateinit var areasDepartments: AreasDepartments

    private lateinit var user: User

    private val _logupState= MutableLiveData<Event<ScreenState<LogupState>>>()
    val logupState: LiveData<Event<ScreenState<LogupState>>>
        get()= _logupState

    private val _loginState= MutableLiveData<Event<ScreenState<LoginState>>>()
    val loginState: LiveData<Event<ScreenState<LoginState>>>
        get()= _loginState

    private val _progressVisible= MutableLiveData<MyDialog.DialogState>()
    val progressVisible: LiveData<MyDialog.DialogState>
        get()= _progressVisible

    private val _imageProfile= MutableLiveData<Uri?>()
    val imageProfile: LiveData<Uri?>
        get()= _imageProfile

    private val _nameError= MutableLiveData<Int?>()
    val nameError: LiveData<Int?>
        get()= _nameError

    private val _emailError= MutableLiveData<Int?>()
    val emailError: LiveData<Int?>
        get()= _emailError

    private val _passwordError= MutableLiveData<Int?>()
    val passwordError: LiveData<Int?>
        get()= _passwordError

    private val _confirmationPasswordError= MutableLiveData<Int?>()
    val confirmationPasswordError: LiveData<Int?>
        get()= _confirmationPasswordError

    private val _spinnerState= MutableLiveData<Int>(0)
    val spinnerState: LiveData<Int>
        get()= _spinnerState

    private val _loginOrLogUp= MutableLiveData<Int>(0)
    val loginOrLogUp: LiveData<Int>
        get()= _loginOrLogUp

    private val _showLoading= MutableLiveData<Boolean>(true)
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private val _departments= MutableLiveData<List<String>>()
    val deparments: LiveData<List<String>>
    get() = _departments

    private val _areas= MutableLiveData<List<String>>()
    val areas: LiveData<List<String>>
        get() = _areas

    val area= MutableLiveData<String>()
    val department= MutableLiveData<String>()

    init {
        logger.debug("Cargando las areas...")
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            var message= -1
            when (error) {
                is FirebaseFirestoreException -> {
                    if (error.code== FirebaseFirestoreException.Code.UNAVAILABLE) {
                        message= R.string.no_internet_connection
                    }
                }
                else -> {
                    message= R.string.not_controled_error
                }
            }
            _showLoading.value= false
            _progressVisible.value= MyDialog.DialogState.ShowMessage(message)
        }
        viewModelScope.launch(errorHandler) {
            areasDepartments= async(Dispatchers.IO) {
                getAreasDepartmentsUseCase()
            }.await()
            _showLoading.value= false
            _areas.value= areasDepartments.areasDepartments.keys.toList()
        }
    }

    private fun clearErrorsLogUp() {
        _nameError.value= null
        _emailError.value= null
        _passwordError.value= null
        _spinnerState.value= 0
    }

    fun indicateSpinnerState(state: Int) {
        _spinnerState.value= state
    }

    private fun clearErrorsLogin() {
        _emailError.value= null
        _passwordError.value= null
    }

    private fun loginUser() {

        clearErrorsLogin()

        _progressVisible.value= MyDialog.DialogState.ShowLoading
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            when (error) {
                is FirebaseNetworkException -> {
                    _loginState.postValue(Event(ScreenState.Render(LoginState.ShowMessage(R.string.no_internet_connection))))
                }
                is FirebaseAuthInvalidCredentialsException -> {
                    if (error.toString().contains("email address is badly formatted")) {
                        _emailError.postValue(R.string.email_incorrect_message_error)
                    } else if (error.toString().contains("The password is invalid")) {
                        _passwordError.postValue(R.string.password_incorrect_login_message_error)
                    }
                    _progressVisible.postValue(MyDialog.DialogState.ShowMessage(R.string.nothing))
                }
                is FirebaseAuthInvalidUserException -> {
                    if (error.toString().contains("There is no user record corresponding to this identifier")) {
                        _emailError.postValue(R.string.user_not_exist_message_error)
                    }
                    _progressVisible.postValue(MyDialog.DialogState.ShowMessage(R.string.nothing))
                }
                is com.google.firebase.FirebaseApiNotAvailableException -> {
                    _loginState.postValue(Event(ScreenState.Render(LoginState.ShowMessage(R.string.firebase_api_no_available))))
                } else -> {
                    _loginState.postValue(Event(ScreenState.Render(LoginState.ShowMessage(R.string.no_internet_connection))))
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            val result= async(Dispatchers.IO) {
                val resultLoginIn= loginUserCase(user)
                if (!user.localProfileImage.isBlank()) {
                    _imageProfile.postValue(Uri.fromFile(File(user.localProfileImage)))
                }
                resultLoginIn
            }.await()
            when (result) {
                is LoginUserCase.Result.LoginOk -> {
                    _loginState.value = Event(ScreenState.Render(LoginState.LoginOk(result.user)))
                }
            }
        }
    }

    fun updateProgressVisible(progressVisible: MyDialog.DialogState) {
        _progressVisible.value= progressVisible
    }

    fun logupUser() {

        clearErrorsLogUp()

        if (name.isBlank() || name.length< 4) {
            _nameError.value = R.string.name_incorrect_message_error
        } else if (email.isBlank() || !email.contains("@")) {
            _emailError.value = R.string.email_incorrect_message_error
        } else if (password.isBlank()) {
            _passwordError.value = R.string.password_incorrect_logup_message_error
        } else if (passwordConfirmation.isBlank()) {
            _confirmationPasswordError.value = R.string.password_incorrect_logup_message_error
        } else if (passwordConfirmation.isNotBlank() && password.isNotBlank() && password!= passwordConfirmation) {
            _confirmationPasswordError.value = R.string.passwords_not_equals_logup_message_error
        } else if (user.profile.isBlank() || user.profile== CHOOSE_PROFILE) {
            _spinnerState.value= 1
        } else {
            _progressVisible.value= MyDialog.DialogState.ShowLoading
            val errorHandler = CoroutineExceptionHandler { _, error ->
                logger.error(error.toString(), error)
                when (error) {
                    is FirebaseNetworkException -> {
                        _logupState.postValue(Event(ScreenState.Render(LogupState.ShowMessage(R.string.no_internet_connection))))
                        _progressVisible.postValue(MyDialog.DialogState.ShowMessage(R.string.no_internet_connection))
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        if (error.toString().contains("email address is badly formatted")) {
                            _emailError.postValue(R.string.email_incorrect_message_error)
                            _progressVisible.postValue(MyDialog.DialogState.ShowMessage(R.string.nothing))
                        } else if (error.toString().contains("The given password is invalid")) {
                            _passwordError.postValue(R.string.password_incorrect_logup_message_error)
                            _progressVisible.postValue(MyDialog.DialogState.ShowMessage(R.string.nothing))
                        }
                    }
                    is FirebaseAuthUserCollisionException -> {
                        _logupState.postValue(Event(ScreenState.Render(LogupState.ShowMessage(R.string.user_collision))))
                        _progressVisible.postValue(MyDialog.DialogState.ShowMessage(R.string.nothing))
                    }
                    is com.google.firebase.FirebaseApiNotAvailableException -> {
                        _loginState.postValue(Event(ScreenState.Render(LoginState.ShowMessage(R.string.firebase_api_no_available))))
                    }
                    else -> {
                        _logupState.postValue(Event(ScreenState.Render(LogupState.ShowMessage(R.string.no_internet_connection))))
                        _progressVisible.postValue(MyDialog.DialogState.ShowMessage(R.string.no_internet_connection))
                    }
                }
            }

            viewModelScope.launch(errorHandler) {
                val result= async(Dispatchers.IO) {
                    logupUseCase(user, imageProfile.value?.toString() ?: "")
                }.await()
                _progressVisible.value = MyDialog.DialogState.ShowSuccess
                when (result) {
                    is LogupUseCase.Result.LoggedUpOk -> {
                        _logupState.value = Event(ScreenState.Render(LogupState.LoggedupOk(result.user)))
                    }
                    is LogupUseCase.Result.LoggedUpAsManagerTeamOk -> {
                        _logupState.value = Event(ScreenState.Render(LogupState.LoggedupAsManagerTeamOk(result.user)))
                    }
                }
            }
        }
    }

    fun onLogupClick()= logupUser()

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

    fun updateDepartmentsOfArea(area: String?) {

        if (areasDepartments.getDepartments(area!!)!= _departments.value) {
            _departments.value= areasDepartments.getDepartments(area!!)
            department.value= ""
        }
    }

    companion object {
        val TO_LOG_UP= 0
        val TO_LOG_IN= 1
    }

}