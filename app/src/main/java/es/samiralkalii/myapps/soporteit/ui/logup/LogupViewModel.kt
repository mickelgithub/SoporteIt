package es.samiralkalii.myapps.soporteit.ui.logup

import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.common.AreasDepartments
import es.samiralkalii.myapps.domain.common.BossCategories
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.getRandomColor
import es.samiralkalii.myapps.usecase.authlogin.LoginUserCase
import es.samiralkalii.myapps.usecase.authlogin.LogupUseCase
import es.samiralkalii.myapps.usecase.common.GetAreasDepartmentsUseCase
import es.samiralkalii.myapps.usecase.common.GetBossCategoriesUseCase
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import java.io.File


private const val CHOOSE_PROFILE= "Elige tu perfil"

class LogupViewModel(private val logupUseCase: LogupUseCase, private val loginUserCase: LoginUserCase, private val getAreasDepartmentsUseCase: GetAreasDepartmentsUseCase,
private val getBossCategoriesUseCase: GetBossCategoriesUseCase) : ViewModel() {

    private val logger = LoggerFactory.getLogger(LogupViewModel::class.java)

    val name= MutableLiveData<String>("")
    val email= MutableLiveData<String>("")
    val password= MutableLiveData<String>("")
    val passwordConfirmation= MutableLiveData<String>("")
    val area= MutableLiveData<String>("")
    val department= MutableLiveData<String>("")
    val isBoss= MutableLiveData<Boolean>()
    val bossCategory= MutableLiveData<String>("")
    val _profileColor= MutableLiveData<Pair<Int, Int>>()
    val profileColor: LiveData<Pair<Int, Int>>
        get() = _profileColor
    val buttonLogupEnabled= MediatorLiveData<Boolean>().apply {
        value= false
        var nameCorrect= false
        var emailCorrect= false
        var passCorrect= false
        var passConfirmationCorrect= false
        var areaCorrect= false
        var departCorrect= false
        addSource(name, { x -> x?.let {
            nameCorrect= it.isNotBlank() && it.length>= 4
            this.value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
            }
        })
        addSource(email, { x -> x?.let {
            emailCorrect= it.isNotBlank() && it.contains("@")
            this.value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(password, { x -> x?.let {
            passCorrect= it.isNotBlank()
            this.value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(passwordConfirmation, { x -> x?.let {
            passConfirmationCorrect= it.isNotBlank()
            this.value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(area, { x -> x?.let {
            areaCorrect= it.isNotBlank()
            this.value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(department, { x -> x?.let {
            departCorrect= it.isNotBlank()
            this.value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
    }

    private lateinit var areasDepartments: AreasDepartments
    private lateinit var bossCategories: BossCategories

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

    private val _areaError= MutableLiveData<Int?>()
    val areaError: LiveData<Int?>
        get() = _areaError

    private val _departmentError= MutableLiveData<Int?>()
    val departmentError: LiveData<Int?>
        get() = _departmentError

    private val _loginOrLogUp= MutableLiveData<Int>(0)
    val loginOrLogUp: LiveData<Int>
        get()= _loginOrLogUp

    private val _showLoading= MutableLiveData<Boolean>(true)
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private val _departments= MutableLiveData<List<String>>()
    val departments: LiveData<List<String>>
    get() = _departments

    private val _areas= MutableLiveData<List<String>>()
    val areas: LiveData<List<String>>
        get() = _areas

    private val _bossCategories= MutableLiveData<List<String>>()
    val bossCategoriesObservable: LiveData<List<String>>
        get() = _bossCategories



    init {

        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            var message= -1
            when (error) {
                is FirebaseFirestoreException -> {
                    if (error.code== FirebaseFirestoreException.Code.UNAVAILABLE) {
                        message= R.string.no_internet_connection
                    } else {
                        message = R.string.not_controled_error
                    }
                }
                else -> {
                    message= R.string.not_controled_error
                }
            }
            _showLoading.value= false
            _progressVisible.value= MyDialog.DialogState.ShowMessageDialog(message)
        }
        viewModelScope.launch(errorHandler) {
            val deferedAreaDepartment= async(Dispatchers.IO) {
                getAreasDepartmentsUseCase()
            }
            val deferedBossCategories= async(Dispatchers.IO) {
                getBossCategoriesUseCase()
            }
            areasDepartments= deferedAreaDepartment.await()
            bossCategories= deferedBossCategories.await()
            _areas.value= areasDepartments.areasDepartments.keys.map { it.name }.toList()
           bossCategories.getBossCategoriesName().let {
               _bossCategories.value= it
               if (!it.isEmpty()) {
                   bossCategory.value= it[0]
               }
           }
            _showLoading.value= false
        }
    }

    private fun clearErrorsLogUp() {
        _nameError.value= null
        _emailError.value= null
        _passwordError.value= null
        _confirmationPasswordError.value= null
        _areaError.value= null
        _departmentError.value= null
    }

    private fun clearErrorsLogin() {
        _emailError.value= null
        _passwordError.value= null
    }

    private fun loginUser() {

        clearErrorsLogin()

        _progressVisible.value= MyDialog.DialogState.ShowProgressDialog()
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
                    _progressVisible.postValue(MyDialog.DialogState.HideDialog(0L))
                }
                is FirebaseAuthInvalidUserException -> {
                    if (error.toString().contains("There is no user record corresponding to this identifier")) {
                        _emailError.postValue(R.string.user_not_exist_message_error)
                    }
                    _progressVisible.postValue(MyDialog.DialogState.HideDialog(0L))
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
                if (!user.profileImage.isBlank()) {
                    _imageProfile.postValue(Uri.fromFile(File(user.profileImage)))
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

    fun logupUser() {
        clearErrorsLogUp()
        if (name.value!!.isBlank() || name.value!!.length< 4) {
            _nameError.value = R.string.name_incorrect_message_error
        } else if (email.value!!.isBlank() || !email.value!!.contains("@")) {
            _emailError.value = R.string.email_incorrect_message_error
        } else if (password.value!!.isBlank()) {
            _passwordError.value = R.string.password_incorrect_logup_message_error
        } else if (passwordConfirmation.value!!.isBlank()) {
            _confirmationPasswordError.value = R.string.password_incorrect_logup_message_error
        } else if (passwordConfirmation.value!!.isNotBlank() && password.value!!.isNotBlank() && password.value!= passwordConfirmation.value) {
            _confirmationPasswordError.value = R.string.passwords_not_equals_logup_message_error
        } else if (area.value.isNullOrBlank()) {
            _areaError.value = R.string.area_incorrect
        } else if (department.value.isNullOrBlank()) {
            _departmentError.value= R.string.department_incorrect
        } else {
            _profileColor.value= getRandomColor()
            _progressVisible.value= MyDialog.DialogState.ShowProgressDialog()
            val errorHandler = CoroutineExceptionHandler { _, error ->
                logger.error(error.toString(), error)
                when (error) {
                    is FirebaseNetworkException -> {
                        _logupState.postValue(Event(ScreenState.Render(LogupState.ShowMessage(R.string.no_internet_connection))))
                        _progressVisible.postValue(MyDialog.DialogState.UpdateMessage(R.string.no_internet_connection))
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        if (error.toString().contains("email address is badly formatted")) {
                            _emailError.postValue(R.string.email_incorrect_message_error)
                            _progressVisible.postValue(MyDialog.DialogState.HideDialog(0L))
                        } else if (error.toString().contains("The given password is invalid")) {
                            _passwordError.postValue(R.string.password_incorrect_logup_message_error)
                            _progressVisible.postValue(MyDialog.DialogState.HideDialog(0L))
                        }
                    }
                    is FirebaseAuthUserCollisionException -> {
                        _logupState.postValue(Event(ScreenState.Render(LogupState.ShowMessage(R.string.user_collision))))
                        _progressVisible.postValue(MyDialog.DialogState.HideDialog(0L))
                    }
                    is com.google.firebase.FirebaseApiNotAvailableException -> {
                        _loginState.postValue(Event(ScreenState.Render(LoginState.ShowMessage(R.string.firebase_api_no_available))))
                    }
                    else -> {
                        _logupState.postValue(Event(ScreenState.Render(LogupState.ShowMessage(R.string.no_internet_connection))))
                        _progressVisible.postValue(MyDialog.DialogState.UpdateMessage(R.string.no_internet_connection))
                    }
                }
            }

            viewModelScope.launch(errorHandler) {
                val result= async(Dispatchers.IO) {

                    delay(2000)

                    logupUseCase(user)

                }.await()
                _progressVisible.value = MyDialog.DialogState.UpdateSuccess()
                when (result) {
                    /*is LogupUseCase.Result.LoggedUpOk -> {
                        _logupState.value = Event(ScreenState.Render(LogupState.LoggedupOk(result.user)))
                    }
                    is LogupUseCase.Result.LoggedUpAsManagerTeamOk -> {
                        _logupState.value = Event(ScreenState.Render(LogupState.LoggedupAsManagerTeamOk(result.user)))
                    }*/
                }
            }
        }
    }

    fun createUser(): User {
        val isEmployeeBoss= isBoss.value ?: false
        val bossCategoryObj= if (isEmployeeBoss) bossCategories.getBossCategory(bossCategory.value!!) else null
        val areaObj= areasDepartments.getArea(area.value!!)
        val departmentObj= areasDepartments.getDepartment(area.value!!, department.value!!)
        return User(name= name.value!!, email = email.value!!, password = password.value!!,
            profileImage = _imageProfile.value?.toString() ?: "", area= areaObj.name,
            areaId = areaObj.id, department = departmentObj.name, departmentId = departmentObj.id,
            isBoss = isEmployeeBoss, bossCategory = bossCategoryObj?.name ?: "",
            bossCategoryId = bossCategoryObj?.id ?: "",
            bossLevel = bossCategoryObj?.level ?: 0)
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
        if (!area.isNullOrBlank()) {
            val departments= areasDepartments.getDepartmentsName(area!!)
            if (departments!= _departments.value) {
                _departments.value= departments
                department.value= ""
            }
        }
    }

    companion object {
        val TO_LOG_UP= 0
        val TO_LOG_IN= 1
    }

}