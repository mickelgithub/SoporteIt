package es.samiralkalii.myapps.soporteit.ui.home.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Group
import es.samiralkalii.myapps.soporteit.ui.UiModel
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
import es.samiralkalii.myapps.soporteit.ui.util.Constants.Companion.GROUP_ALL
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState

class HomeFragmentViewModelUiModel: UiModel {

    val _progressVisible= MutableLiveData<MyDialog.DialogState>()
    val progressVisible: LiveData<MyDialog.DialogState>
        get()= _progressVisible

    val _items= MutableLiveData<MutableList<MemberUserViewModelTemplate>?>()
    val items: LiveData<MutableList<MemberUserViewModelTemplate>?>
        get() = _items

    val _getGroupsActionState= MutableLiveData<Event<ScreenState<HomeFragmentStates.GetGroupsState>>>()
    val getGroupsActionState: LiveData<Event<ScreenState<HomeFragmentStates.GetGroupsState>>>
        get() = _getGroupsActionState

    val _refreshingState= MutableLiveData<Event<Boolean>?>()
    val refreshingState: LiveData<Event<Boolean>?>
        get() = _refreshingState

    val _searchViewVisibility= MutableLiveData(false)
    val searchViewVisibility: LiveData<Boolean>
        get() = _searchViewVisibility

    var _user= MutableLiveData<User?>()
    val user: LiveData<User?>
        get() = _user

    val _updateGroup= MutableLiveData<Event<Group>?>()
    val updateGroup: LiveData<Event<Group>?>
        get() = _updateGroup

    val _selectedGroup= MutableLiveData(GROUP_ALL)
    val selectedGroup: LiveData<String>
        get() = _selectedGroup

    val _updateExpandableGroup= MutableLiveData<MemberUserViewModelTemplate.GroupMemberUserViewModel?>()
    val updateExpandableGroup: LiveData<MemberUserViewModelTemplate.GroupMemberUserViewModel?>
        get() = _updateExpandableGroup

    //the first boolean atribute indicate whether after deleting the users, there is no more users
    //en this case, the adapter haz to delete each item and that's it
    val _deletedUsers= MutableLiveData<Event<Triple<Boolean, List<String>?, List<String>?>>?>()
    val deletedUsers: LiveData<Event<Triple<Boolean, List<String>?, List<String>?>>?>
        get() = _deletedUsers

}