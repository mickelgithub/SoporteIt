package es.samiralkalii.myapps.soporteit.ui.home.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.storage.StorageException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.authlogin.Compare2ImageProfileUseCase
import es.samiralkalii.myapps.usecase.authlogin.UpdateProfileImageUseCase
import es.samiralkalii.myapps.usecase.usermanagment.GetUserUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class ProfileViewModel(private val compare2ImageProfileUseCase: Compare2ImageProfileUseCase,
                       private val updateProfileImageUseCase: UpdateProfileImageUseCase,
                       private val getUserUseCase: GetUserUseCase
): ViewModel() {

    private val logger = LoggerFactory.getLogger(ProfileViewModel::class.java)

    private val _profileImage= MutableLiveData<Uri?>()
    val profileImage: LiveData<Uri?>
                get()= _profileImage

    private val _showSaveMenu= MutableLiveData<Boolean>(false)
    val showSaveMenu: LiveData<Boolean>
        get()= _showSaveMenu

    private val _progressVisible= MutableLiveData<MyDialog.DialogState>()
    val progressVisible: LiveData<MyDialog.DialogState>
        get()= _progressVisible

    private val _profileChangeState= MutableLiveData<Event<ScreenState<ProfileChangeState>>>()
    val profileChangeState: LiveData<Event<ScreenState<ProfileChangeState>>>
        get()= _profileChangeState

    private val _showVerified= MutableLiveData<Boolean>()
    val showVerified: LiveData<Boolean>
    get() = _showVerified

    private val _showNotVerifiedYet= MutableLiveData<Boolean>()
    val showNotVerifiedYet: LiveData<Boolean>
        get() = _showNotVerifiedYet


    private val _user= MutableLiveData<User?>(User.EMPTY)
    val user: LiveData<User?>
        get()= _user

    private var imageChanged: Boolean= false

    fun init() {
        viewModelScope.launch {
            _user.value = async(Dispatchers.IO) {
                getUserUseCase()
            }.await()
            _user.value?.let {
                _profileImage.value= if (!it.profileImage.isNullOrBlank()) Uri.parse(it.profileImage) else null
                _showVerified.value= (it.isBoss && it.bossConfirmation== "Y") ||
                        (!it.isBoss && it.membershipConfirmation== "Y")
                _showNotVerifiedYet.value= (it.isBoss && it.bossConfirmation== "") ||
                        (!it.isBoss && it.membershipConfirmation== "")
            }
        }
    }

    fun updateShowSaveMenu() {
        _showSaveMenu.value= true
    }


    fun updateImageProfile(imgUri: Uri?) {
        _profileImage.value= imgUri
        val localProfileImage= _user.value?.profileImage ?: ""
        if (localProfileImage.isBlank() && imgUri!= null) {
            imageChanged= true
            _showSaveMenu.value= true
        } else if (localProfileImage.isNotBlank() && imgUri== null) {
            imageChanged= true
            _showSaveMenu.value = true
        } else if (localProfileImage.isBlank() && imgUri== null) {
            imageChanged= false
            _showSaveMenu.value= false
        } else {
            viewModelScope.launch() {
                val equals = async(Dispatchers.IO) {
                    compare2ImageProfileUseCase(
                        imgUri.toString(),
                        _user.value?.profileImage ?: ""
                    )
                }.await()
                imageChanged= !equals
                _showSaveMenu.value= !equals
            }
        }
    }

    fun onSaveClick() {
        _progressVisible.value= MyDialog.DialogState.ShowProgressDialog()
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            when (error) {
                is FirebaseNetworkException -> {
                    _profileChangeState.postValue(Event(ScreenState.Render(ProfileChangeState.ShowMessage(R.string.no_internet_connection))))
                }
                is StorageException -> {
                    _profileChangeState.postValue(Event(ScreenState.Render(ProfileChangeState.ShowMessage(R.string.no_internet_connection))))
                }
                else -> {
                    _profileChangeState.postValue(Event(ScreenState.Render(ProfileChangeState.ShowMessage(R.string.not_controled_error))))
                }
            }
        }
        viewModelScope.launch(errorHandler) {
            _user.value= async(Dispatchers.IO) {
                updateProfileImageUseCase(_user.value?.id!!, _user.value?.profileImage!!, _profileImage.value?.toString() ?: "")
                getUserUseCase()
            }.await()
            _profileImage.value= if (!_user.value?.profileImage.isNullOrBlank()) Uri.parse(_user.value?.profileImage) else null
            _showSaveMenu.value= false
            _profileChangeState.value= Event(ScreenState.Render(ProfileChangeState.changeOk))
        }
    }

    fun updateProgressVisible(progressVisible: MyDialog.DialogState) {
        _progressVisible.value= progressVisible
    }


}