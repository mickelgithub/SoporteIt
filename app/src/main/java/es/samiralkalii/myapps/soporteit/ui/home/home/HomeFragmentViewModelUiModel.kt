package es.samiralkalii.myapps.soporteit.ui.home.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.UiModel
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
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

    val _searchViewVisibility= MutableLiveData<Boolean>(false)
    val searchViewVisibility: LiveData<Boolean>
        get() = _searchViewVisibility

    var _user= MutableLiveData<User?>()
    val user: LiveData<User?>
        get() = _user

}