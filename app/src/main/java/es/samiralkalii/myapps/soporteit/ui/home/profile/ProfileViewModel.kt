package es.samiralkalii.myapps.soporteit.ui.home.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.getFirstName
import es.samiralkalii.myapps.usecase.authlogin.Compare2ImageProfileUseCase
import es.samiralkalii.myapps.usecase.authlogin.SaveProfileChangeUseCase
import es.samiralkalii.myapps.usecase.usermanagment.GetUserUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

class ProfileViewModel(private val compare2ImageProfileUseCase: Compare2ImageProfileUseCase,
                       private val saveProfileChangeUseCase: SaveProfileChangeUseCase,
                       private val getUserUseCase: GetUserUseCase
): ViewModel() {

    private val logger = LoggerFactory.getLogger(ProfileViewModel::class.java)

    private val _imageProfile= MutableLiveData<Uri?>()
    val imageProfile: LiveData<Uri?>
                get()= _imageProfile

    private val _bgColorProfile= MutableLiveData<Int?>()
    val bgColorProfile: LiveData<Int?>
        get()= _bgColorProfile

    private val _txtColorProfile= MutableLiveData<Int?>()
    val txtColorProfile: LiveData<Int?>
        get()= _txtColorProfile

    private val _nameProfile= MutableLiveData<String?>()
    val nameProfile: LiveData<String?>
        get()= _nameProfile

    private val _showSaveMenu= MutableLiveData<Boolean>(false)
    val showSaveMenu: LiveData<Boolean>
        get()= _showSaveMenu

    private val _progressVisible= MutableLiveData<MyDialog.DialogState>()
    val progressVisible: LiveData<MyDialog.DialogState>
        get()= _progressVisible

    private val _profileChangeState= MutableLiveData<Event<ScreenState<ProfileChangeState>>>()
    val profileChangeState: LiveData<Event<ScreenState<ProfileChangeState>>>
        get()= _profileChangeState


    private val _user= MutableLiveData<User?>(User.EMPTY)
    val user: LiveData<User?>
        get()= _user

    private var imageChanged: Boolean= false
    private var currentProfile= ""

    fun init() {
        viewModelScope.launch {
            _user.value = async(Dispatchers.IO) {
                getUserUseCase()
            }.await()
            val localProfileImage= _user.value?.profileImage ?: ""
            if (localProfileImage.isNotBlank()) {
                _imageProfile.value= Uri.fromFile(File(localProfileImage))
            } else {
                _bgColorProfile.value= user.value?.profileBackColor
                _txtColorProfile.value= user.value?.profileTextColor
                _nameProfile.value= getFirstName(_user.value?.name)
            }
            currentProfile= _user.value?.profile ?: ""
        }
    }

    fun updateShowSaveMenu() {
        _showSaveMenu.value= true
    }


    fun updateImageProfile(imgUri: Uri?) {
        _imageProfile.value= imgUri
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

    fun onSaveClick(chooseYourProfileResource: String) {

        _progressVisible.value= MyDialog.DialogState.ShowProgressDialog()
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            when (error) {
                is FirebaseNetworkException -> {
                    _profileChangeState.postValue(Event(ScreenState.Render(ProfileChangeState.ShowMessage(R.string.no_internet_connection))))
                }
                else -> {
                    _profileChangeState.postValue(Event(ScreenState.Render(ProfileChangeState.ShowMessage(R.string.not_controled_error))))
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            async(Dispatchers.IO) {
                saveProfileChangeUseCase(_user.value!!, _imageProfile.value?.toString() ?: "", imageChanged, chooseYourProfileResource)
            }.await()
            _showSaveMenu.value= false
            _profileChangeState.value= Event(ScreenState.Render(ProfileChangeState.changeOk))
            /*if (currentProfile!= user.profile) {
                _profileChanged.value= Event(true)
            }*/
            //_progressVisible.value = LoadingDialog.DialogState.ShowSuccess
        }
    }

    fun updateProgressVisible(progressVisible: MyDialog.DialogState) {
        _progressVisible.value= progressVisible
    }
}