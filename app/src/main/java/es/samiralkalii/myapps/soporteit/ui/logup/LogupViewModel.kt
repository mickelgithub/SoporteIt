package es.samiralkalii.myapps.soporteit.ui.logup

import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.AreasDepartments
import es.samiralkalii.myapps.domain.teammanagement.BossCategories
import es.samiralkalii.myapps.domain.teammanagement.Holidays
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.getRandomColor
import es.samiralkalii.myapps.usecase.authlogin.LoginUserCase
import es.samiralkalii.myapps.usecase.authlogin.LogupUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetAreasDepartmentsUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetBossCategoriesUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetHolidayDaysUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class LogupViewModel(private val logupUseCase: LogupUseCase, private val loginUserCase: LoginUserCase,
                     private val getAreasDepartmentsUseCase: GetAreasDepartmentsUseCase,
                     private val getBossCategoriesUseCase: GetBossCategoriesUseCase,
                     private val getHolidayDaysUseCase: GetHolidayDaysUseCase
) : ViewModel() {

    private val logger = LoggerFactory.getLogger(LogupViewModel::class.java)

    val name= MutableLiveData("")
    val email= MutableLiveData("")
    val password= MutableLiveData("")
    val passwordConfirmation= MutableLiveData("")
    val area= MutableLiveData("")
    val department= MutableLiveData("")
    val isBoss= MutableLiveData<Boolean>()
    val bossCategory= MutableLiveData("")
    private val _profileColor= MutableLiveData<Pair<Int, Int>>()
    val profileColor: LiveData<Pair<Int, Int>>
        get() = _profileColor

    private val _departmentsEnabled= MutableLiveData(true)
    val departmentsEnabled: LiveData<Boolean>
        get() = _departmentsEnabled

    private val _areasEnabled= MutableLiveData(true)
    val areasEnabled: LiveData<Boolean>
        get() = _areasEnabled

    private var _dataLoaded= MutableLiveData(false)
    val dataLoaded: LiveData<Boolean>
        get() = _dataLoaded

    val buttonLogupEnabled= getMediatorLiveDataForLogupButtonEnabledState()

    val buttonLoginEnabled= getMediatorLiveDataForLoginButtonEnabledState()

    private lateinit var areasDepartments: AreasDepartments
    private lateinit var bossCategories: BossCategories
    private lateinit var holidays: Holidays

    private val _user= MutableLiveData<User?>()
    val user: LiveData<User?>
        get()= _user

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
            var message= R.string.not_controled_error
            if (error is FirebaseFirestoreException) {
                if (error.code== FirebaseFirestoreException.Code.UNAVAILABLE) {
                    message= R.string.no_internet_connection
                }
            }
            _showLoading.value= false
            _logupState.postValue(Event(ScreenState.Render(LogupState.ShowMessage(message))))

        }
        viewModelScope.launch(errorHandler) {
            val deferedAreaDepartment= async(Dispatchers.IO) {
                getAreasDepartmentsUseCase()
            }
            val deferedBossCategories= async(Dispatchers.IO) {
                getBossCategoriesUseCase()
            }
            val deferedHolidays= async(Dispatchers.IO) {
                getHolidayDaysUseCase()
            }
            areasDepartments= deferedAreaDepartment.await()
            bossCategories= deferedBossCategories.await()
            holidays= deferedHolidays.await()
            _areas.value= areasDepartments.areasDepartments.keys.map { it.name }.toList()
            bossCategories.getBossCategoriesName().let {
               _bossCategories.value= it
               if (!it.isEmpty()) {
                   bossCategory.value= it[0]
               }
           }
            _dataLoaded.value= true
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
        if (email.value!!.isBlank() || !email.value!!.contains("@")) {
            _emailError.value = R.string.email_incorrect_message_error
        } else if (password.value!!.isBlank()) {
            _passwordError.value = R.string.password_incorrect_logup_message_error
        } else {
            _progressVisible.value= MyDialog.DialogState.ShowProgressDialog()
            val errorHandler = CoroutineExceptionHandler { _, error ->
                logger.error(error.toString(), error)
                when (error) {
                    is FirebaseNetworkException -> {
                        _loginState.postValue(Event(ScreenState.Render(LoginState.UpdateMessage(R.string.no_internet_connection))))
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
                        _loginState.postValue(Event(ScreenState.Render(LoginState.UpdateMessage(R.string.firebase_api_no_available))))
                    } else -> {
                    _loginState.postValue(Event(ScreenState.Render(LoginState.UpdateMessage(R.string.no_internet_connection))))
                    }
                }
            }
            viewModelScope.launch(errorHandler) {
                val result= async(Dispatchers.IO) {
                    val resultLoginIn= loginUserCase(email.value!!, password.value!!)
                    resultLoginIn
                }.await()
                when (result) {
                    is LoginUserCase.Result.LoginOk -> {
                        _user.value= result.user
                        _loginState.value = Event(ScreenState.Render(LoginState.LoginOk(result.user)))
                    }
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
        } else if (area.value.isNullOrBlank() && bossCategories.getBossCategory(bossCategory.value!!).level< 3 ) {
            _areaError.value = R.string.area_incorrect
        } else if (department.value.isNullOrBlank() && bossCategories.getBossCategory(bossCategory.value!!).level< 2) {
            _departmentError.value= R.string.department_incorrect
        } else {
            _progressVisible.value= MyDialog.DialogState.ShowProgressDialog()
            val errorHandler = CoroutineExceptionHandler { _, error ->
                logger.error(error.toString(), error)
                when (error) {
                    is FirebaseNetworkException -> {
                        _logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.no_internet_connection))))
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
                        _logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.user_collision))))
                    }
                    is com.google.firebase.FirebaseApiNotAvailableException -> {
                        _logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.firebase_api_no_available))))
                    }
                    else -> {
                        _logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.no_internet_connection))))
                    }
                }
            }

            viewModelScope.launch(errorHandler) {
                var userLocal: User?= null
                val result= async(Dispatchers.IO) {
                    userLocal= createUserForLogup()
                    logupUseCase(userLocal!!)
                }.await()
                when (result) {
                    is LogupUseCase.Result.LoggedUpOk -> {
                        _logupState.value = Event(ScreenState.Render(LogupState.LoggedupOk(result.user)))
                    }
                    is LogupUseCase.Result.LoggedUpAsManagerTeamOk -> {
                        _logupState.value = Event(ScreenState.Render(LogupState.LoggedupAsManagerTeamOk(result.user)))
                    }
                    LogupUseCase.Result.LoggedUpBossDuplicate -> {
                        userLocal?.let {
                            if (it.departmentId.isNotBlank() && it.areaId.isNotBlank()) {
                                _logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.boss_already_exist_department_manager, listOf(it.department, it.area)))))
                            } else if (it.departmentId.isBlank() && it.areaId.isNotBlank()) {
                                _logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.boss_already_exist_area_manager, listOf(it.area)))))
                            } else {
                                _logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.boss_already_exist_director))))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createUserForLogup(): User {
        val profColor= getRandomColor()
        val isEmployeeBoss= isBoss.value ?: false
        val bossCategoryObj= if (isEmployeeBoss) bossCategories.getBossCategory(bossCategory.value!!) else null
        val areaObj= if (area.value!!.isNotBlank()) areasDepartments.getArea(area.value!!) else null
        val departmentObj= if (area.value!!.isNotBlank() && department.value!!.isNotBlank())
            areasDepartments.getDepartment(area.value!!, department.value!!) else null

        val holidaysDay= if (isEmployeeBoss) holidays.holidayDays else User.DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS
        val internalEmployee= if (isEmployeeBoss) true else false

        return User(name= name.value!!, email = email.value!!, password = password.value!!,
            profileImage = _imageProfile.value?.toString() ?: "", area= areaObj?.name ?: "",
            areaId = areaObj?.id ?: "", department = departmentObj?.name ?: "", departmentId = departmentObj?.id ?: "",
            isBoss = isEmployeeBoss, bossCategory = bossCategoryObj?.name ?: "",
            holidayDays = holidaysDay, internalEmployee = internalEmployee,
            bossCategoryId = bossCategoryObj?.id ?: "",
            bossLevel = bossCategoryObj?.level ?: 0,
            profileBackColor = profColor.first ?: -1,
            profileTextColor = profColor.second ?: -1)
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

    fun updateDialogState(dialog: MyDialog.DialogState) {
        _progressVisible.value= dialog
    }

    fun updateProfileColor(color: Pair<Int, Int>) {
        _profileColor.value= color
    }

    companion object {
        val TO_LOG_UP= 0
        val TO_LOG_IN= 1
    }

    private fun getMediatorLiveDataForLoginButtonEnabledState()= MediatorLiveData<Boolean>().apply {
        value= false
        var emailCorrect= false
        var passCorrect= false

        addSource(email, { x -> x?.let {
            emailCorrect= it.isNotBlank() && it.contains("@")
            value= emailCorrect && passCorrect
        }
        })
        addSource(password, { x -> x?.let {
            passCorrect= it.isNotBlank()
            value= emailCorrect && passCorrect
        }
        })
    }

    private fun getMediatorLiveDataForLogupButtonEnabledState()= MediatorLiveData<Boolean>().apply {
        value= false
        var nameCorrect= false
        var emailCorrect= false
        var passCorrect= false
        var passConfirmationCorrect= false
        var areaCorrect= false
        var departCorrect= false
        addSource(name, { x -> x?.let {
            nameCorrect= it.isNotBlank() && it.length>= 4
            value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(email, { x -> x?.let {
            emailCorrect= it.isNotBlank() && it.contains("@")
            value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(password, { x -> x?.let {
            passCorrect= it.isNotBlank()
            value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(passwordConfirmation, { x -> x?.let {
            passConfirmationCorrect= it.isNotBlank()
            value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(area, { x -> x?.let {
            if (it.isNotBlank()) {
                areaCorrect= true
            } else {
                if (dataLoaded.value!!) {
                    if (bossCategories.getBossCategory(bossCategory.value!!).level== 3) {
                        areaCorrect= true
                    } else {
                        areaCorrect= false
                    }
                } else {
                    areaCorrect= false
                }
            }
            value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(bossCategory, { x -> x?.let {
            logger.debug("bossCategory...")
            if (it.isNotBlank()) {
                if (bossCategories.getBossCategory(it).level>=2) {
                    department.value= ""
                    _departmentsEnabled.value= false
                    if (bossCategories.getBossCategory(bossCategory.value!!).level== 3) {
                        area.value= ""
                        _areasEnabled.value= false
                    } else {
                        _areasEnabled.value= true
                    }
                } else {
                    _departmentsEnabled.value= true
                    _areasEnabled.value= true
                }
            }
            val isBossLocal= isBoss?.value!= null && isBoss?.value== true
            if (isBossLocal) {
                if (bossCategories.getBossCategory(it).level>=2) {
                    departCorrect= true
                } else {
                    departCorrect= !department.value.isNullOrBlank()
                }
            } else {
                departCorrect= !department.value.isNullOrBlank()
            }
            value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(isBoss, { x -> x?.let {
            logger.debug("isBoss...")
            if (it) {
                if (bossCategories.getBossCategory(bossCategory.value!!).level>=2) {
                    department.value= ""
                    _departmentsEnabled.value= false
                    if (bossCategories.getBossCategory(bossCategory.value!!).level== 3) {
                        area.value= ""
                        _areasEnabled.value= false
                    } else {
                        _areasEnabled.value= true
                    }
                    departCorrect= true
                } else {
                    _departmentsEnabled.value= true
                    _areasEnabled.value= true
                    departCorrect= !department.value.isNullOrBlank()
                }
            } else {
                _departmentsEnabled.value= true
                _areasEnabled.value= true
                departCorrect= !department.value.isNullOrBlank()
            }
            value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
        addSource(department, { x -> x?.let {
            logger.debug("department...")
            val isBossLocal= isBoss?.value!= null && isBoss?.value== true
            if (isBossLocal) {
                if (bossCategories.getBossCategory(bossCategory.value!!).level>=2) {
                    departCorrect= true
                } else {
                    departCorrect= !it.isNullOrBlank()
                }
            } else {
                departCorrect= !it.isNullOrBlank()
            }
            value= nameCorrect && emailCorrect && passCorrect && passConfirmationCorrect &&
                    areaCorrect && departCorrect
        }
        })
    }

}