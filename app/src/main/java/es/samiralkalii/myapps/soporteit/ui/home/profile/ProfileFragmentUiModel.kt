package es.samiralkalii.myapps.soporteit.ui.home.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.UiModel
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState

class ProfileFragmentUiModel: UiModel {

    val _profileImage= MutableLiveData<Uri?>()
    val profileImage: LiveData<Uri?>
        get()= _profileImage

    val _showSaveMenu= MutableLiveData(false)
    val showSaveMenu: LiveData<Boolean>
        get()= _showSaveMenu

    val _progressVisible= MutableLiveData<MyDialog.DialogState>()
    val progressVisible: LiveData<MyDialog.DialogState>
        get()= _progressVisible

    val _profileChangeState= MutableLiveData<Event<ScreenState<ProfileChangeState>>>()
    val profileChangeState: LiveData<Event<ScreenState<ProfileChangeState>>>
        get()= _profileChangeState

    val _showVerified= MutableLiveData<Boolean>()
    val showVerified: LiveData<Boolean>
        get() = _showVerified

    val _showNotVerifiedYet= MutableLiveData<Boolean>()
    val showNotVerifiedYet: LiveData<Boolean>
        get() = _showNotVerifiedYet

    val _user= MutableLiveData(User.EMPTY)
    val user: LiveData<User?>
        get()= _user

    val _isHost= MutableLiveData(true)
    val isHost: LiveData<Boolean>
        get()= _isHost

    var imageChanged: Boolean= false
}