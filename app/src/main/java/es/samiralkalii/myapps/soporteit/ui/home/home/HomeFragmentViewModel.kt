package es.samiralkalii.myapps.soporteit.ui.home.home

import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Group
import es.samiralkalii.myapps.domain.teammanagement.GroupList
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.BaseFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivityViewModel.Companion.CONFIRMED_BUNDLE_KEY
import es.samiralkalii.myapps.soporteit.ui.home.HomeActivityViewModel.Companion.IS_EMAIL_VALIDATED_BUNDLE_KEY
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.teammanagement.DeleteGroupUseCase
import es.samiralkalii.myapps.usecase.teammanagement.DeleteUsersUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetGroupsUseCase
import es.samiralkalii.myapps.usecase.usermanagment.GetUserUseCase
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory

class HomeFragmentViewModel(private val getGroupsUseCase: GetGroupsUseCase,
                            private val getUserUseCase: GetUserUseCase,
                            private val deleteGroupUseCase: DeleteGroupUseCase,
                            private val deleteUsersUseCase: DeleteUsersUseCase
): BaseFragmentViewModel() {

    private val logger = LoggerFactory.getLogger(HomeFragmentViewModel::class.java)

    override val uiModel= HomeFragmentViewModelUiModel()

    lateinit var myGroups: GroupList
    lateinit var myGroupsUiModel: List<MemberUserViewModelTemplate.GroupMemberUserViewModel>

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

    private fun mapModelGroupToUiModelGroup(user: User, expandedGroup: String)=
        if (myGroups.isEmpty || (myGroups.groups.size== 1 && myGroups.groups[0].members.isEmpty())) {
            listOf()
        } else {
            myGroups.groups.map {
                val subItems = it.members.map { userItem ->
                    MemberUserViewModelTemplate.MemberUserViewModel(
                        userItem,
                        uiModel.user.value!!,
                        it
                    )
                }
                val groupItem = MemberUserViewModelTemplate.GroupMemberUserViewModel(
                    user,
                    it,
                    subItems,
                    this@HomeFragmentViewModel
                ).apply {
                    setExpanded(it.name.equals(expandedGroup, true))
                }
                groupItem
            }
        }

    private fun getMyGroupListItemsToShow(groups: List<MemberUserViewModelTemplate.GroupMemberUserViewModel>)=
        if (groups.isEmpty()) {
            listOf(MemberUserViewModelTemplate.MemberUserViewModelEmpty)
        } else {
            groups.map {
                val groupResult= mutableListOf<MemberUserViewModelTemplate>(it)
                if (it.isExpanded) {
                    groupResult.addAll(it.subItems)
                }
                groupResult.toList()
            }.flatten()
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
            var user= uiModel._user.value
            myGroups= withContext(Dispatchers.IO) {
                if (user== null) {
                    user= getUserUseCase()
                }
                getGroupsUseCase(user!!)
            }
            uiModel._user.value= user
            myGroupsUiModel= mapModelGroupToUiModelGroup(uiModel.user.value!!, uiModel._selectedGroup.value!!)
            uiModel._getGroupsActionState.value= Event(ScreenState.Render(HomeFragmentStates.GetGroupsState.GetGroupsStateOk(getMyGroupListItemsToShow(myGroupsUiModel))))
            if (refresh) {
                uiModel._refreshingState.value= Event(true)
            }
            uiModel._searchViewVisibility.value= myGroups.groups.size>= 2
        }
    }

    //suspend fun confirmDenyMember(user: String, isConfirmed: Boolean)= confirmDenyMemberUseCase(user, isConfirmed)

    private fun filterResult(data: List<MemberUserViewModelTemplate>): MutableList<MemberUserViewModelTemplate>  {
        val result= mutableListOf<MemberUserViewModelTemplate>()
        val selectedGroup= data.find { it is MemberUserViewModelTemplate.GroupMemberUserViewModel && it.isExpanded} as MemberUserViewModelTemplate.GroupMemberUserViewModel?
        if (selectedGroup!= null) {
            result.addAll(data.filter { it is MemberUserViewModelTemplate.GroupMemberUserViewModel || it in selectedGroup!!.subItems})
        } else {
            result.addAll(data)
        }
        return result
    }

    fun updateItem(position: Int, memberUserViewModel: MemberUserViewModelTemplate) {
        uiModel._items.value?.let {
            it[position]= memberUserViewModel
        }
    }

    fun removeItem(position: Int) {
        uiModel._items.value?.removeAt(position)
    }

    fun filterGroups(groupName: String) {
        var filteredGroups= myGroups.groups
        var result= mutableListOf<MemberUserViewModelTemplate>(MemberUserViewModelTemplate.MemberUserViewModelEmpty)
        if (groupName.length> 0) {
            filteredGroups= myGroups.groups.filter { it.name.startsWith(groupName, ignoreCase = true)}.toMutableList()
        }
        if (filteredGroups.isNotEmpty()) {
            result= filterResult(filteredGroups.map {
                val items= mutableListOf<MemberUserViewModelTemplate>()
                val subItems= it.members.map { userItem -> MemberUserViewModelTemplate.MemberUserViewModel(userItem, uiModel.user.value!!, it) }
                val groupItem= MemberUserViewModelTemplate.GroupMemberUserViewModel(uiModel._user.value!!, it, subItems, this).apply {
                    //setExpanded(it.name.equals(uiModel.selectedGroup.value, true))
                }
                items.add(groupItem)
                items.addAll(subItems)
                items
            }.flatMap{it}.toMutableList())
            if (filteredGroups.size== 1 && filteredGroups[0].members.isEmpty()) {
                result.add(MemberUserViewModelTemplate.MemberUserViewModelEmpty)
            }
        }
        uiModel._getGroupsActionState.value= Event(ScreenState.Render(HomeFragmentStates.GetGroupsState.GetGroupsStateOk(result)))
    }

    fun onDeleteGroup(group: String) {
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            var message= R.string.not_controled_error
            if (error is FirebaseFirestoreException) {
                if (error.code.ordinal== FirebaseFirestoreException.Code.UNAVAILABLE.ordinal) {
                    message= R.string.no_internet_connection
                }
            }
        }
        viewModelScope.launch(errorHandler) {
            async(Dispatchers.IO) {
                deleteGroupUseCase(group)
            }.await()
            initData()
        }
    }

    fun updateGroup(group: Group) {
        uiModel._updateGroup.value= Event(group)
    }

    fun deleteUsers(users: List<String>) {
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            var message= R.string.not_controled_error
            if (error is FirebaseFirestoreException) {
                if (error.code.ordinal== FirebaseFirestoreException.Code.UNAVAILABLE.ordinal) {
                    message= R.string.no_internet_connection
                }
            }
        }
        viewModelScope.launch(errorHandler) {
            async(Dispatchers.IO) {
                deleteUsersUseCase(users)
            }.await()

            updateStateAfterUsersDeletion(users)

        }
    }

    private fun updateStateAfterUsersDeletion(users: List<String>) {

        //Todos contains all members
        val deletedGroups= mutableListOf<String>()
        val newMembers= myGroups.groups[0].members.filter { it.id !in users }
        if (newMembers.isEmpty()) {
            myGroups= GroupList(mutableListOf())
            myGroupsUiModel= listOf()
            uiModel._deletedUsers.value= Event(Triple(true, null, null))
        } else {
            val newGroups= myGroups.groups.map {
                val newItems= it.members.filter { user ->  user.id !in users }
                if (newItems.isEmpty()) {
                    deletedGroups.add(it.name)
                }
                it.copy(members = newItems)
            }.filter { it.members.isNotEmpty() }
            myGroups= GroupList(newGroups.toMutableList())
            myGroupsUiModel= myGroupsUiModel.map {
                val subItems= it.subItems.filter { subitem ->
                    subitem.user.id !in users
                }
                it.subItems= subItems
                it
                }.filter { it.subItems.isNotEmpty() }
            uiModel._deletedUsers.value= Event(Triple(false, users, deletedGroups))
        }
    }

        /*myGroups.groups.forEach {
            val members= it.members.filter { user -> user.id !in users }
            if (members.size> 0) {
                val newGroup= it.copy(members = members)
                groups.add(newGroup)
            }
        }
        if (groups.isNotEmpty()) {
            result= filterResult(groups.map {
                val items= mutableListOf<MemberUserViewModelTemplate>()
                val subItems= it.members.map { userItem -> MemberUserViewModelTemplate.MemberUserViewModel(userItem, uiModel.user.value!!, it) }
                val groupItem= MemberUserViewModelTemplate.GroupMemberUserViewModel(uiModel._user.value!!, it, subItems,this@HomeFragmentViewModel).apply {
                    //setExpanded(it.name.equals(uiModel.selectedGroup.value, true))
                }
                items.add(groupItem)
                items.addAll(subItems)
                items
            }.flatMap{it}.toMutableList())
        } else {
            result= mutableListOf(MemberUserViewModelTemplate.MemberUserViewModelEmpty)
        }
        uiModel._getGroupsActionState.value= Event(ScreenState.Render(HomeFragmentStates.GetGroupsState.GetGroupsStateOk(result)))
        myGroups= GroupList(groups)

    }*/

    fun onExpandClick(itemGroup: MemberUserViewModelTemplate.GroupMemberUserViewModel) {

        if (!itemGroup.group.name.equals(uiModel._selectedGroup.value, true)) {
            uiModel._updateExpandableGroup.value= itemGroup
            uiModel._selectedGroup.value= itemGroup.group.name
        }
    }

    override fun onCleared() {
        super.onCleared()
        logger.debug("${this.javaClass.canonicalName} onCleared*******")
    }
}