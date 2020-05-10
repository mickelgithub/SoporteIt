package es.samiralkalii.myapps.soporteit.ui.home.home

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.BaseFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.BaseViewModel
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.teammanagement.ConfirmDenyMemberUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetGroupsUseCase
import es.samiralkalii.myapps.usecase.usermanagment.GetUserUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class HomeFragmentViewModel(private val getGroupsUseCase: GetGroupsUseCase,
                            private val getUserUseCase: GetUserUseCase,
                            private val confirmDenyMemberUseCase: ConfirmDenyMemberUseCase
): BaseFragmentViewModel() {

    private val logger = LoggerFactory.getLogger(HomeFragmentViewModel::class.java)

    private lateinit var user: User

    override val uiModel= HomeFragmentViewModelUiModel()

    override fun init() {
        initData()
    }

    fun updateDialogState(dialog: MyDialog.DialogState) {
        uiModel._progressVisible.postValue(dialog)
    }

    fun updateItems(items: List<MemberUserViewModelTemplate>) {
        uiModel._items.value= items.toMutableList()
    }


    fun initData(refresh: Boolean= false) {
        if (!refresh) {
            uiModel._items.value= mutableListOf(MemberUserViewModelTemplate.MemberUserViewModelLoading)
        }
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            var message= R.string.not_controled_error
            if (error is FirebaseFirestoreException) {
                if (error.code.ordinal== FirebaseFirestoreException.Code.UNAVAILABLE.ordinal) {
                    message= R.string.no_internet_connection
                }
            }
            uiModel._getGroupsActionState.postValue(Event(ScreenState.Render(HomeFragmentStates.GetGroupsState.ShowMessage(message))))
            if (refresh) {
                uiModel._refreshingState.postValue(Event(true))
            }
        }
        viewModelScope.launch(errorHandler) {

            val groupList= async(Dispatchers.IO) {
                user= getUserUseCase()
                getGroupsUseCase(user)
            }.await()
            var result= mutableListOf<MemberUserViewModelTemplate>(MemberUserViewModelTemplate.MemberUserViewModelEmpty)
            if (!groupList.isEmpty) {
                result= groupList.groups.map {
                    val items= mutableListOf<MemberUserViewModelTemplate>()
                    items.add(MemberUserViewModelTemplate.GroupMemberUserViewModel(it.name))
                    items.addAll(it.members.map { userItem -> MemberUserViewModelTemplate.MemberUserViewModel(userItem, this@HomeFragmentViewModel) })
                    items
                }.flatMap({it}).toMutableList()
                if (groupList.groups.size== 1 && groupList.groups[0].members.isEmpty()) {
                    result.add(MemberUserViewModelTemplate.MemberUserViewModelEmpty)
                }
            }
            uiModel._getGroupsActionState.value= Event(ScreenState.Render(HomeFragmentStates.GetGroupsState.GetGroupsStateOk(result)))
            if (refresh) {
                uiModel._refreshingState.value= Event(true)
            }
        }
    }

    suspend fun confirmDenyMember(user: String, isConfirmed: Boolean)= confirmDenyMemberUseCase(user, isConfirmed)

    fun updateItem(position: Int, memberUserViewModel: MemberUserViewModelTemplate) {
        uiModel._items.value?.let {
            it[position]= memberUserViewModel
        }
    }

    fun removeItem(position: Int) {
        uiModel._items.value?.removeAt(position)
    }



}