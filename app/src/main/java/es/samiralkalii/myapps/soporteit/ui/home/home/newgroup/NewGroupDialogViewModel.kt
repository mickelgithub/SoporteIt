package es.samiralkalii.myapps.soporteit.ui.home.home.newgroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.usecase.teammanagement.GetDeparmentUsersUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class NewGroupDialogViewModel(
    private val getDeparmentUsersUseCase: GetDeparmentUsersUseCase
): ViewModel(), NewGroupDialogViewModelInteractor {

    private val logger = LoggerFactory.getLogger(NewGroupDialogViewModel::class.java)

    lateinit var uiModel: NewGroupDialogViewModelUiModel

    private lateinit var boss: String
    private lateinit var area: String
    private lateinit var department: String
    private lateinit var groups: List<String>

    fun publishUser(boss: String, area: String, department: String, groups: List<String>) {
        this.boss= boss
        this.area= area
        this.department= department
        this.groups= groups
        uiModel= NewGroupDialogViewModelUiModel()
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
        uiModel._itemsLiveData.value= listOf(MemberUserNewGroupTemplate.MemberUserNewGroupViewModelLoading)
        uiModel._dataLoaded.value= false
        viewModelScope.launch(errorHandler) {
            uiModel.items= async(Dispatchers.IO) {
                getDeparmentUsersUseCase(area, department).filter { it.id!= boss }
            }.await()
            uiModel._itemsLiveData.value= uiModel.items.map {
                MemberUserNewGroupTemplate.MemberUserNewGroupViewModel(
                    it
                )
            }
            uiModel._dataLoaded.value= true
            uiModel._dialogCancelable.value= true
        }
    }

    override fun createGroup() {
        if (groups.contains(uiModel.groupName.value!!.toUpperCase())) {
            uiModel._groupNameError.value= R.string.group_already_exist
        }
    }

    override fun onCleared() {
        super.onCleared()
        logger.debug("NewGroupDialogViewModel cleared------")
    }
}

interface NewGroupDialogViewModelInteractor {
    fun createGroup()
}