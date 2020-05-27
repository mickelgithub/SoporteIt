package es.samiralkalii.myapps.soporteit.ui.home.home.newgroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User

class NewGroupDialogViewModelUiModel() {

    lateinit var items: List<User>
    val groupName= MutableLiveData("")

    val _itemsLiveData= MutableLiveData<List<MemberUserNewGroupTemplate>>()
    val itemsLiveData: LiveData<List<MemberUserNewGroupTemplate>>
        get() = _itemsLiveData

    val _dismissDialog= MutableLiveData(false)
    val dismissDialog: LiveData<Boolean>
        get() = _dismissDialog

    val _dialogCancelable= MutableLiveData(true)
    val dialogCancelable: LiveData<Boolean>
        get() = _dialogCancelable

    val _error= MutableLiveData<Int>()
    val error: LiveData<Int>
        get() = _error

    var _dataLoaded= MutableLiveData(false)
    val dataLoaded: LiveData<Boolean>
        get() = _dataLoaded

    val _groupNameError= MutableLiveData<Int?>()
    val groupNameError: LiveData<Int?>
        get()= _groupNameError

    val _buttonCreateGroupEnabled= getMediatorLiveDataForCreateGrooupButtonEnabledState()
    val buttonCreateGroupEnabled: LiveData<Boolean>
        get() = _buttonCreateGroupEnabled

    /*val _buttonCreateGroupVisible= MutableLiveData(false)
    val buttonCreateGroupVisible: LiveData<Boolean>
        get() = _buttonCreateGroupVisible*/

    private fun getMediatorLiveDataForCreateGrooupButtonEnabledState()= MediatorLiveData<Boolean>().apply {
        value= false
        var groupNameCorrect= false
        var selectedMembersCorrect= false

        addSource(groupName, { x -> x?.let {
            groupNameCorrect= it.isNotBlank() && it.length>= 2
            value= groupNameCorrect && selectedMembersCorrect
        }
        })
        addSource(_itemsLiveData, { x -> x?.let {
            selectedMembersCorrect= it.isNotEmpty() && it.fold(true) {
                    condition, element -> condition && element is MemberUserNewGroupTemplate.MemberUserNewGroupViewModel
            }
            value= groupNameCorrect && selectedMembersCorrect
        }
        })
    }
}