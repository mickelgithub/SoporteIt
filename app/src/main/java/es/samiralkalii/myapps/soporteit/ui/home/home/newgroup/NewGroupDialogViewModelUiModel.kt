package es.samiralkalii.myapps.soporteit.ui.home.home.newgroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import kotlinx.android.synthetic.main.new_group_member_user_item.view.*

class NewGroupDialogViewModelUiModel(val groups: List<String>) {

    lateinit var items: List<User>
    val groupName= MutableLiveData("")

    val _itemsLeft= MutableLiveData<Int>(-1)

    val _itemsLiveData= MutableLiveData<MutableList<MemberUserNewGroupTemplate>>()
    val itemsLiveData: LiveData<MutableList<MemberUserNewGroupTemplate>>
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

    val _groupNameError= getMediatorLiveDataForGroupNameError()
    val groupNameError: LiveData<Int>
        get()= _groupNameError

    val _buttonCreateGroupEnabled= getMediatorLiveDataForCreateGrooupButtonEnabledState()
    val buttonCreateGroupEnabled: LiveData<Boolean>
        get() = _buttonCreateGroupEnabled

    val _sucessVisible= MutableLiveData(false)
    val sucessVisible: LiveData<Boolean>
        get() = _sucessVisible

    val _buttonLoadEnabled= getMediatorLiveDataForLoadButtonEnabledState()
    val buttonLoadEnabled: LiveData<Boolean>
        get() = _buttonLoadEnabled

    val _disable_recyclerview_swipping= MutableLiveData(false)
    val disable_recyclerview_swipping: LiveData<Boolean>
        get() = _disable_recyclerview_swipping

    private fun getMediatorLiveDataForCreateGrooupButtonEnabledState()= MediatorLiveData<Boolean>().apply {
        value= false
        var groupNameCorrect= false
        var selectedMembersCorrect= false
        var numberItemsCorrect= false

        addSource(groupName, { x -> x?.let {
            groupNameCorrect= it.isNotBlank() && it.length>= 2 && !groups.contains(it.toUpperCase())
            value= groupNameCorrect && selectedMembersCorrect && numberItemsCorrect
        }
        })
        addSource(_itemsLiveData, { x -> x?.let {
            selectedMembersCorrect= it.isNotEmpty() && it.fold(true) {
                    condition, element -> condition && element is MemberUserNewGroupTemplate.MemberUserNewGroupViewModel
            }
            value= groupNameCorrect && selectedMembersCorrect && numberItemsCorrect
        }
        })
        addSource(_itemsLeft, { x -> x?.let {
            numberItemsCorrect= it> 0
            }
            value= groupNameCorrect && selectedMembersCorrect && numberItemsCorrect
        })
    }

    private fun getMediatorLiveDataForLoadButtonEnabledState()= MediatorLiveData<Boolean>().apply {
        value= false
        var loadCorrect= false
        var dataCorrect= false

        addSource(_dataLoaded, { x -> x?.let {
            loadCorrect= it
            value= loadCorrect && dataCorrect
        }
        })
        addSource(_itemsLeft, { x -> x?.let {
            dataCorrect= _itemsLeft.value!!>= 0 && _itemsLeft.value!!< items.size
            value= loadCorrect && dataCorrect
        }
        })
    }

    private fun getMediatorLiveDataForGroupNameError()= MediatorLiveData<Int>().apply {
        value= R.string.nothing
        addSource(groupName, { x -> x?.let {
            value= if (!groups.contains(it.toUpperCase())) R.string.nothing else R.string.group_already_exist
        }
        })
    }
}