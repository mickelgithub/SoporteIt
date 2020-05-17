package es.samiralkalii.myapps.soporteit.ui.logup

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.BaseViewModel
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.getRandomColor
import es.samiralkalii.myapps.usecase.authlogin.LoginUserCase
import es.samiralkalii.myapps.usecase.authlogin.LogupUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetAreasDepartmentsUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetBossCategoriesUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetHolidayDaysUseCase
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

class LogupActivityViewModel(private val logupUseCase: LogupUseCase, private val loginUserCase: LoginUserCase,
                             private val getAreasDepartmentsUseCase: GetAreasDepartmentsUseCase,
                             private val getBossCategoriesUseCase: GetBossCategoriesUseCase,
                             private val getHolidayDaysUseCase: GetHolidayDaysUseCase
) : BaseViewModel(), LogupViewModelInteractor {

    private val logger = LoggerFactory.getLogger(LogupActivityViewModel::class.java)

    override val uiModel= LogupActivityUiModel()

    override fun init(data: Bundle?) {
        loadData()
    }

    private fun loadData() {
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            var message= R.string.not_controled_error
            if (error is FirebaseFirestoreException) {
                if (error.code.ordinal== FirebaseFirestoreException.Code.UNAVAILABLE.ordinal) {
                    message= R.string.no_internet_connection
                }
            }
            uiModel._showLoading.postValue(false)
            uiModel._logupState.postValue(Event(ScreenState.Render(LogupState.ShowMessage(message))))
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
            uiModel.areasDepartments= deferedAreaDepartment.await()
            uiModel.bossCategories= deferedBossCategories.await()
            uiModel.holidays= deferedHolidays.await()
            uiModel._areas.value= uiModel.areasDepartments.areasDepartments.keys.map { it.name }.toList()
            uiModel.bossCategories.getBossCategoriesName().let {
                uiModel._bossCategories.value= it
                if (it.isNotEmpty()) {
                    uiModel.bossCategory.value= it[0]
                }
            }
            uiModel._dataLoaded.value= true
            uiModel._showLoading.value= false
        }
    }

    private fun clearErrorsLogUp() {
        uiModel._nameError.value= null
        uiModel._emailError.value= null
        uiModel._passwordError.value= null
        uiModel._confirmationPasswordError.value= null
        uiModel._areaError.value= null
        uiModel._departmentError.value= null
    }

    private fun clearErrorsLogin() {
        uiModel._emailError.value= null
        uiModel._passwordError.value= null
    }

    private fun loginUser() {
        clearErrorsLogin()
        if (uiModel.email.value!!.isBlank() || !uiModel.email.value!!.contains("@")) {
            uiModel._emailError.value = R.string.email_incorrect_message_error
        } else if (uiModel.password.value!!.isBlank()) {
            uiModel._passwordError.value = R.string.password_incorrect_logup_message_error
        } else {
            uiModel._progressVisible.value= MyDialog.DialogState.ShowProgressDialog()
            val errorHandler = CoroutineExceptionHandler { _, error ->
                logger.error(error.toString(), error)
                when (error) {
                    is FirebaseNetworkException -> {
                        uiModel._loginState.postValue(Event(ScreenState.Render(LoginState.UpdateMessage(R.string.no_internet_connection))))
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        if (error.toString().contains("email address is badly formatted")) {
                            uiModel._emailError.postValue(R.string.email_incorrect_message_error)
                        } else if (error.toString().contains("The password is invalid")) {
                            uiModel._passwordError.postValue(R.string.password_incorrect_login_message_error)
                        }
                        uiModel._progressVisible.postValue(MyDialog.DialogState.HideDialog(0L))
                    }
                    is FirebaseAuthInvalidUserException -> {
                        if (error.toString().contains("There is no user record corresponding to this identifier")) {
                            uiModel._emailError.postValue(R.string.user_not_exist_message_error)
                        }
                        uiModel._progressVisible.postValue(MyDialog.DialogState.HideDialog(0L))
                    }
                    is com.google.firebase.FirebaseApiNotAvailableException -> {
                        uiModel._loginState.postValue(Event(ScreenState.Render(LoginState.UpdateMessage(R.string.firebase_api_no_available))))
                    } else -> {
                    uiModel._loginState.postValue(Event(ScreenState.Render(LoginState.UpdateMessage(R.string.no_internet_connection))))
                    }
                }
            }
            viewModelScope.launch(errorHandler) {
                val result= withContext(Dispatchers.IO) {
                    val resultLoginIn= loginUserCase(uiModel.email.value!!, uiModel.password.value!!)
                    resultLoginIn
                }
                when (result) {
                    is LoginUserCase.Result.LoginOk -> {
                        uiModel._user.value= result.user
                        uiModel._loginState.value = Event(ScreenState.Render(LoginState.LoginOk(result.user)))
                    }
                }
            }
        }
    }

    private fun logupUser() {
        clearErrorsLogUp()
        if (uiModel.name.value!!.isBlank() || uiModel.name.value!!.length< 4) {
            uiModel._nameError.value = R.string.name_incorrect_message_error
        } else if (uiModel.email.value!!.isBlank() || !uiModel.email.value!!.contains("@")) {
            uiModel._emailError.value = R.string.email_incorrect_message_error
        } else if (uiModel.password.value!!.isBlank()) {
            uiModel._passwordError.value = R.string.password_incorrect_logup_message_error
        } else if (uiModel.passwordConfirmation.value!!.isBlank()) {
            uiModel._confirmationPasswordError.value = R.string.password_incorrect_logup_message_error
        } else if (uiModel.passwordConfirmation.value!!.isNotBlank() && uiModel.password.value!!.isNotBlank() && uiModel.password.value!= uiModel.passwordConfirmation.value) {
            uiModel._confirmationPasswordError.value = R.string.passwords_not_equals_logup_message_error
        } else if (uiModel.area.value.isNullOrBlank() && uiModel.bossCategories.getBossCategory(uiModel.bossCategory.value!!).level< 3 ) {
            uiModel._areaError.value = R.string.area_incorrect
        } else if (uiModel.department.value.isNullOrBlank() && uiModel.bossCategories.getBossCategory(uiModel.bossCategory.value!!).level< 2) {
            uiModel._departmentError.value= R.string.department_incorrect
        } else {
            uiModel._progressVisible.value= MyDialog.DialogState.ShowProgressDialog()
            val errorHandler = CoroutineExceptionHandler { _, error ->
                logger.error(error.toString(), error)
                when (error) {
                    is FirebaseNetworkException -> {
                        uiModel._logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.no_internet_connection))))
                    }
                    is FirebaseAuthInvalidCredentialsException -> {
                        if (error.toString().contains("email address is badly formatted")) {
                            uiModel._emailError.postValue(R.string.email_incorrect_message_error)
                            uiModel._progressVisible.postValue(MyDialog.DialogState.HideDialog(0L))
                        } else if (error.toString().contains("The given password is invalid")) {
                            uiModel._passwordError.postValue(R.string.password_incorrect_logup_message_error)
                            uiModel._progressVisible.postValue(MyDialog.DialogState.HideDialog(0L))
                        }
                    }
                    is FirebaseAuthUserCollisionException -> {
                        uiModel._logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.user_collision))))
                    }
                    is com.google.firebase.FirebaseApiNotAvailableException -> {
                        uiModel._logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.firebase_api_no_available))))
                    }
                    else -> {
                        uiModel._logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.no_internet_connection))))
                    }
                }
            }

            viewModelScope.launch(errorHandler) {
                var userLocal: User?= null
                val result= withContext(Dispatchers.IO) {
                    userLocal= createUserForLogup()
                    logupUseCase(userLocal!!)
                }
                when (result) {
                    is LogupUseCase.Result.LoggedUpOk -> {
                        uiModel._logupState.value = Event(ScreenState.Render(LogupState.LoggedupOk(result.user)))
                    }
                    is LogupUseCase.Result.LoggedUpAsManagerTeamOk -> {
                        uiModel._logupState.value = Event(ScreenState.Render(LogupState.LoggedupAsManagerTeamOk(result.user)))
                    }
                    LogupUseCase.Result.LoggedUpBossDuplicate -> {
                        userLocal?.let {
                            if (it.departmentId.isNotBlank() && it.areaId.isNotBlank()) {
                                uiModel._logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.boss_already_exist_department_manager, listOf(it.department, it.area)))))
                            } else if (it.departmentId.isBlank() && it.areaId.isNotBlank()) {
                                uiModel._logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.boss_already_exist_area_manager, listOf(it.area)))))
                            } else {
                                uiModel._logupState.postValue(Event(ScreenState.Render(LogupState.UpdateMessage(R.string.boss_already_exist_director))))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createUserForLogup(): User {
        val profColor= getRandomColor()
        val isEmployeeBoss= uiModel.isBoss.value ?: false
        val bossCategoryObj= if (isEmployeeBoss) uiModel.bossCategories.getBossCategory(uiModel.bossCategory.value!!) else null
        val areaObj= if (uiModel.area.value!!.isNotBlank()) uiModel.areasDepartments.getArea(uiModel.area.value!!) else null
        val departmentObj= if (uiModel.area.value!!.isNotBlank() && uiModel.department.value!!.isNotBlank())
            uiModel.areasDepartments.getDepartment(uiModel.area.value!!, uiModel.department.value!!) else null

        val holidaysDay= if (isEmployeeBoss) uiModel.holidays.holidayDays else User.DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS

        return User(name= uiModel.name.value!!, email = uiModel.email.value!!, password = uiModel.password.value!!,
            profileImage = uiModel._imageProfile.value?.toString() ?: "", area= areaObj?.name ?: "",
            areaId = areaObj?.id ?: "", department = departmentObj?.name ?: "", departmentId = departmentObj?.id ?: "",
            isBoss = isEmployeeBoss, bossCategory = bossCategoryObj?.name ?: "",
            holidayDays = holidaysDay, internalEmployee = isEmployeeBoss,
            bossCategoryId = bossCategoryObj?.id ?: "",
            bossLevel = bossCategoryObj?.level ?: 0,
            profileBackColor = profColor.first,
            profileTextColor = profColor.second)
    }

    override fun onLogupClick()= logupUser()

    override fun onLogInClick()= loginUser()

    fun updateImageProfile(imgUri: Uri?) {
        uiModel._imageProfile.value= imgUri
    }

    override fun onAlreadyLoggedUpClick() {
        uiModel._loginOrLogUp.value= ScreenLogup.LOGIN
        updateImageProfile(null)
        clearErrorsLogUp()
    }

    override fun noAcountClick() {
        uiModel._loginOrLogUp.value= ScreenLogup.LOGUP
        clearErrorsLogin()
    }

    fun updateDepartmentsOfArea(area: String?) {
        if (!area.isNullOrBlank()) {
            val departments= uiModel.areasDepartments.getDepartmentsName(area)
            if (departments!= uiModel._departments.value) {
                uiModel._departments.value= departments
                uiModel.department.value= ""
            }
        }
    }

    fun updateDialogState(dialog: MyDialog.DialogState) {
        uiModel._progressVisible.value= dialog
    }

    fun updateProfileColor(color: Pair<Int, Int>) {
        uiModel._profileColor.value= color
    }

    enum class ScreenLogup {
        LOGUP, LOGIN
    }

}

interface LogupViewModelInteractor {

    fun onLogupClick()
    fun onAlreadyLoggedUpClick()
    fun noAcountClick()
    fun onLogInClick()

}

