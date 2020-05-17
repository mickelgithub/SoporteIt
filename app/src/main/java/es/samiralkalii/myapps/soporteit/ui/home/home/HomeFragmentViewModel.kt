package es.samiralkalii.myapps.soporteit.ui.home.home

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.BaseFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivityViewModel.Companion.CONFIRMED_BUNDLE_KEY
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivityViewModel.Companion.IS_EMAIL_VALIDATED_BUNDLE_KEY
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.teammanagement.ConfirmDenyMemberUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetGroupsUseCase
import es.samiralkalii.myapps.usecase.usermanagment.GetUserUseCase
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

class HomeFragmentViewModel(private val getGroupsUseCase: GetGroupsUseCase,
                            private val getUserUseCase: GetUserUseCase,
                            private val confirmDenyMemberUseCase: ConfirmDenyMemberUseCase
): BaseFragmentViewModel() {

    private val logger = LoggerFactory.getLogger(HomeFragmentViewModel::class.java)

    override val uiModel= HomeFragmentViewModelUiModel()

    override fun init(bundle: Bundle?) {
        bundle?.let {
            val isEmailValidated= it.getBoolean(IS_EMAIL_VALIDATED_BUNDLE_KEY, false)
            val isConfirmed= it.getBoolean(CONFIRMED_BUNDLE_KEY, false)
            if (isEmailValidated && isConfirmed) {
                initData()
            }
        }
    }

    fun updateDialogState(dialog: MyDialog.DialogState) {
        uiModel._progressVisible.postValue(dialog)
    }

    fun updateItems(items: List<MemberUserViewModelTemplate>) {
        uiModel._items.value= items.toMutableList()
    }

    fun initData(refresh: Boolean= false) {
        logger.debug("estamos haciendo el initData...<>><<<<<<<<<<<<<<<<<<<<<<<<")
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

            val groupList= withContext(Dispatchers.IO) {
                val user= getUserUseCase()
                uiModel._user.postValue(user)
                getGroupsUseCase(user)
            }
            var result= mutableListOf<MemberUserViewModelTemplate>(MemberUserViewModelTemplate.MemberUserViewModelEmpty)
            if (!groupList.isEmpty) {
                result= groupList.groups.map {
                    val items= mutableListOf<MemberUserViewModelTemplate>()
                    items.add(MemberUserViewModelTemplate.GroupMemberUserViewModel(it.name))
                    items.addAll(it.members.map { userItem -> MemberUserViewModelTemplate.MemberUserViewModel(userItem) })
                    items
                }.flatMap{it}.toMutableList()
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

    //suspend fun confirmDenyMember(user: String, isConfirmed: Boolean)= confirmDenyMemberUseCase(user, isConfirmed)

    fun updateItem(position: Int, memberUserViewModel: MemberUserViewModelTemplate) {
        uiModel._items.value?.let {
            it[position]= memberUserViewModel
        }
    }

    fun removeItem(position: Int) {
        uiModel._items.value?.removeAt(position)
    }

}