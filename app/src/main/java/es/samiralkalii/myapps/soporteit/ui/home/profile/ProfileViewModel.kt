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
import es.samiralkalii.myapps.usecase.authlogin.Compare2ImageProfileUseCase
import es.samiralkalii.myapps.usecase.authlogin.SaveProfileChangeUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

class ProfileViewModel(private val compare2ImageProfileUseCase: Compare2ImageProfileUseCase,
                       private val saveProfileChangeUseCase: SaveProfileChangeUseCase
): ViewModel() {

    private val logger = LoggerFactory.getLogger(ProfileViewModel::class.java)

    private val _imageProfile= MutableLiveData<Uri?>()
    val imageProfile: LiveData<Uri?>
        get()= _imageProfile

    private val _showSaveMenu= MutableLiveData<Boolean>(false)
    val showSaveMenu: LiveData<Boolean>
        get()= _showSaveMenu

    private val _progressVisible= MutableLiveData<MyDialog.DialogState>()
    val progressVisible: LiveData<MyDialog.DialogState>
        get()= _progressVisible

    private val _profileChangeState= MutableLiveData<Event<ScreenState<ProfileChangeState>>>()
    val profileChangeState: LiveData<Event<ScreenState<ProfileChangeState>>>
        get()= _profileChangeState

    private val _profileChanged= MutableLiveData<Event<Boolean>>()
    val profileChanged: LiveData<Event<Boolean>>
        get() = _profileChanged

    lateinit var user: User

    private var imageChanged: Boolean= false
    private var currentProfile= ""

    fun publishUser(userParam: User) {
        user= userParam
        logger.debug(user.localProfileImage+ "desde profileViewModelllll")
        if (user.localProfileImage.isNotBlank()) {
            _imageProfile.value= Uri.fromFile(File(user.localProfileImage))
        }
        currentProfile= user.profile
    }

    fun updateShowSaveMenu() {
        _showSaveMenu.value= true
    }


    fun updateImageProfile(imgUri: Uri?) {
        _imageProfile.value= imgUri
        if (user.localProfileImage.isBlank() && imgUri!= null) {
            imageChanged= true
            _showSaveMenu.value= true
        } else if (!user.localProfileImage.isBlank() && imgUri== null) {
            imageChanged= true
            _showSaveMenu.value = true
        } else if (user.localProfileImage.isBlank() && imgUri== null) {
            imageChanged= false
            _showSaveMenu.value= false
        } else {
            viewModelScope.launch() {
                val equals = async(Dispatchers.IO) {
                    compare2ImageProfileUseCase(
                        imgUri.toString(),
                        user.localProfileImage
                    )
                }.await()
                imageChanged= !equals
                _showSaveMenu.value= !equals
            }
        }
    }

    fun onSaveClick(chooseYourProfileResource: String) {

        _progressVisible.value= MyDialog.DialogState.ShowLoading
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            when (error) {
                is FirebaseNetworkException -> {
                    _profileChangeState.postValue(Event(ScreenState.Render(ProfileChangeState.ShowMessage(R.string.no_internet_connection))))
                }
                else -> {
                    _profileChangeState.postValue(Event(ScreenState.Render(ProfileChangeState.ShowMessage(R.string.no_internet_connection))))
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            async(Dispatchers.IO) {
                saveProfileChangeUseCase(user, _imageProfile.value?.toString() ?: "", imageChanged, chooseYourProfileResource)
            }.await()
            _showSaveMenu.value= false
            _profileChangeState.value= Event(ScreenState.Render(ProfileChangeState.changeOk))
            if (currentProfile!= user.profile) {
                _profileChanged.value= Event(true)
            }
            //_progressVisible.value = LoadingDialog.DialogState.ShowSuccess
        }
    }

    fun updateProgressVisible(progressVisible: MyDialog.DialogState) {
        _progressVisible.value= progressVisible
    }
}