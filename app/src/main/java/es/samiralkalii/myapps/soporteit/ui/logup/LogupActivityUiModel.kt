package es.samiralkalii.myapps.soporteit.ui.logup

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.AreasDepartments
import es.samiralkalii.myapps.domain.teammanagement.BossCategories
import es.samiralkalii.myapps.domain.teammanagement.Holidays
import es.samiralkalii.myapps.soporteit.ui.UiModel
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState

class LogupActivityUiModel: UiModel {

    val name= MutableLiveData("")
    val email= MutableLiveData("")
    val password= MutableLiveData("")
    val passwordConfirmation= MutableLiveData("")
    val area= MutableLiveData("")
    val department= MutableLiveData("")
    val isBoss= MutableLiveData<Boolean>()
    val bossCategory= MutableLiveData("")

    var _dataLoaded= MutableLiveData(false)
    val dataLoaded: LiveData<Boolean>
        get() = _dataLoaded

    val _imageProfile= MutableLiveData<Uri?>()
    val imageProfile: LiveData<Uri?>
        get()= _imageProfile

    val _nameError= MutableLiveData<Int?>()
    val nameError: LiveData<Int?>
        get()= _nameError

    val _emailError= MutableLiveData<Int?>()
    val emailError: LiveData<Int?>
        get()= _emailError

    val _passwordError= MutableLiveData<Int?>()
    val passwordError: LiveData<Int?>
        get()= _passwordError

    val _confirmationPasswordError= MutableLiveData<Int?>()
    val confirmationPasswordError: LiveData<Int?>
        get()= _confirmationPasswordError

    val _showLoading= MutableLiveData<Boolean>(true)
    val showLoading: LiveData<Boolean>
        get() = _showLoading

    val _areaError= MutableLiveData<Int?>()
    val areaError: LiveData<Int?>
        get() = _areaError

    val _areasEnabled= MutableLiveData(true)
    val areasEnabled: LiveData<Boolean>
        get() = _areasEnabled

    val _areas= MutableLiveData<List<String>>()
    val areas: LiveData<List<String>>
        get() = _areas

    val _departmentsEnabled= MutableLiveData(true)
    val departmentsEnabled: LiveData<Boolean>
        get() = _departmentsEnabled

    val _departmentError= MutableLiveData<Int?>()
    val departmentError: LiveData<Int?>
        get() = _departmentError

    val _departments= MutableLiveData<List<String>>()
    val departments: LiveData<List<String>>
        get() = _departments

    val _bossCategories= MutableLiveData<List<String>>()
    val bossCategoriesObservable: LiveData<List<String>>
        get() = _bossCategories

    lateinit var areasDepartments: AreasDepartments
    lateinit var bossCategories: BossCategories

    val buttonLogupEnabled= getMediatorLiveDataForLogupButtonEnabledState()

    val buttonLoginEnabled= getMediatorLiveDataForLoginButtonEnabledState()

    val _profileColor= MutableLiveData<Pair<Int, Int>>()
    val profileColor: LiveData<Pair<Int, Int>>
        get() = _profileColor

    lateinit var holidays: Holidays

    val _user= MutableLiveData<User?>()
    val user: LiveData<User?>
        get()= _user

    val _logupState= MutableLiveData<Event<ScreenState<LogupState>>>()
    val logupState: LiveData<Event<ScreenState<LogupState>>>
        get()= _logupState

    val _loginState= MutableLiveData<Event<ScreenState<LoginState>>>()
    val loginState: LiveData<Event<ScreenState<LoginState>>>
        get()= _loginState

    val _progressVisible= MutableLiveData<MyDialog.DialogState>()
    val progressVisible: LiveData<MyDialog.DialogState>
        get()= _progressVisible

    val _loginOrLogUp= MutableLiveData(LogupActivityViewModel.ScreenLogup.LOGUP)
    val loginOrLogUp: LiveData<LogupActivityViewModel.ScreenLogup>
        get()= _loginOrLogUp

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
            val isBossLocal= isBoss.value!= null && isBoss.value== true
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