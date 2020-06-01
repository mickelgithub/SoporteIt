package es.samiralkalii.myapps.soporteit.ui.home.home.newgroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Group
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_MEMBERS
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_NAME
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.util.KEY_AREA
import es.samiralkalii.myapps.soporteit.ui.util.KEY_AREA_ID
import es.samiralkalii.myapps.soporteit.ui.util.KEY_DEPARTMENT_ID
import es.samiralkalii.myapps.soporteit.ui.util.OPERATION_UPDATE
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
    private lateinit var operation: String
    private lateinit var groupUsers: List<String>
    private lateinit var groupName: String

    fun publishInitInfo(boss: String, area: String,
                        department: String,
                        groups: List<String>,
                        operation: String,
                        groupUsers: List<String>,
                        groupName: String) {
        this.boss= boss
        this.area= area
        this.department= department
        this.groups= groups
        uiModel= NewGroupDialogViewModelUiModel(groups)
        this.operation= operation
        this.groupUsers= groupUsers
        this.groupName= groupName
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
            if (operation== OPERATION_UPDATE) {
                uiModel.items= uiModel.items.sortedWith(Comparator<User> { p0: User, p1: User ->
                    if (p0.id in groupUsers && p1.id !in groupUsers) {
                        -1
                    } else if (p0.id !in groupUsers && p1.id in groupUsers) {
                        1
                    } else 0
                })
            }
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
        uiModel._disable_recyclerview_swipping.value= true
        viewModelScope.launch(errorHandler) {
            async(Dispatchers.IO) {
                val users= listOf<String>(boss, *uiModel._itemsLiveData.value!!.map { (it as MemberUserNewGroupTemplate.MemberUserNewGroupViewModel).user.id }.toTypedArray())
                newGroupUseCase(mutableMapOf(KEY_GROUP_NAME to uiModel.groupName.value!!,
                    KEY_AREA_ID to area, KEY_DEPARTMENT_ID to department, KEY_GROUP_MEMBERS to users))
            }.await()
            uiModel._sucessVisible.value= true
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