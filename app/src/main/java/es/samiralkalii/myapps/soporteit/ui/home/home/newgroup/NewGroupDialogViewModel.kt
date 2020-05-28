package es.samiralkalii.myapps.soporteit.ui.home.home.newgroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.teammanagement.Group
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_MEMBERS
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_NAME
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.util.KEY_AREA
import es.samiralkalii.myapps.soporteit.ui.util.KEY_AREA_ID
import es.samiralkalii.myapps.soporteit.ui.util.KEY_DEPARTMENT_ID
import es.samiralkalii.myapps.usecase.teammanagement.GetDeparmentUsersUseCase
import es.samiralkalii.myapps.usecase.teammanagement.NewGroupUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import kotlin.math.sign

class NewGroupDialogViewModel(
    private val getDeparmentUsersUseCase: GetDeparmentUsersUseCase, private val newGroupUseCase: NewGroupUseCase
): ViewModel(), NewGroupDialogViewModelInteractor {

    private val logger = LoggerFactory.getLogger(NewGroupDialogViewModel::class.java)

    lateinit var uiModel: NewGroupDialogViewModelUiModel

    private lateinit var boss: String
    private lateinit var area: String
    private lateinit var department: String
    private lateinit var groups: List<String>

    fun publishInitInfo(boss: String, area: String, department: String, groups: List<String>) {
        this.boss= boss
        this.area= area
        this.department= department
        this.groups= groups
        uiModel= NewGroupDialogViewModelUiModel(groups)
        //we load users
        init()
    }

    private fun init() {
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            var message= R.string.not_controled_error
            if (error is FirebaseFirestoreException) {
                if (error.code.ordinal== FirebaseFirestoreException.Code.UNAVAILABLE.ordinal) {
                    message= R.string.no_internet_connection
                }
            }
            uiModel._error.postValue(message)
            uiModel._dialogCancelable.postValue(true)
            uiModel._dataLoaded.postValue(false)
        }
        uiModel._dialogCancelable.value= false
        uiModel._itemsLiveData.value= mutableListOf(MemberUserNewGroupTemplate.MemberUserNewGroupViewModelLoading)
        uiModel._dataLoaded.value= false
        viewModelScope.launch(errorHandler) {
            uiModel.items= async(Dispatchers.IO) {
                getDeparmentUsersUseCase(area, department).filter { it.id!= boss }
            }.await()
            uiModel._itemsLiveData.value= uiModel.items.map {
                MemberUserNewGroupTemplate.MemberUserNewGroupViewModel(
                    it
                )
            }.toMutableList()
            uiModel._itemsLeft.value= uiModel.items.size
            uiModel._dataLoaded.value= true
            uiModel._dialogCancelable.value= true
        }
    }
    
    fun deleteItemAt(position: Int) {
        uiModel._itemsLiveData.value!!.removeAt(position)
        uiModel._itemsLeft.value= uiModel._itemsLiveData.value!!.size
    }

    override fun createGroup() {

        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            var message= R.string.not_controled_error
            if (error is FirebaseFirestoreException) {
                if (error.code.ordinal== FirebaseFirestoreException.Code.UNAVAILABLE.ordinal) {
                    message= R.string.no_internet_connection
                }
            }
            uiModel._error.postValue(message)
            uiModel._dialogCancelable.postValue(true)
        }
        uiModel._buttonCreateGroupEnabled.value= false
        uiModel._buttonLoadEnabled.value= false
        uiModel._dialogCancelable.value= false
        val users= uiModel._itemsLiveData.value!!
        uiModel._itemsLiveData.value= MutableList(users.size) {MemberUserNewGroupTemplate.MemberUserNewGroupViewModelLoading}
        viewModelScope.launch(errorHandler) {
            async(Dispatchers.IO) {
                newGroupUseCase(mutableMapOf(KEY_GROUP_NAME to uiModel.groupName.value!!,
                    KEY_AREA_ID to area, KEY_DEPARTMENT_ID to department, KEY_GROUP_MEMBERS to users.map { (it as MemberUserNewGroupTemplate.MemberUserNewGroupViewModel).user.id }))
            }.await()
            uiModel._itemsLiveData.value= MutableList(users.size) {MemberUserNewGroupTemplate.MemberUserNewGroupViewModelSuccess}
            uiModel._dismissDialog.value= true
        }
    }

    override fun refreshUsers() {
        uiModel._itemsLiveData.value= uiModel.items.map {
            MemberUserNewGroupTemplate.MemberUserNewGroupViewModel(
                it
            )
        }.toMutableList()
        uiModel._itemsLeft.value= uiModel.items.size
    }

}

interface NewGroupDialogViewModelInteractor {
    fun createGroup()
    fun refreshUsers()
}