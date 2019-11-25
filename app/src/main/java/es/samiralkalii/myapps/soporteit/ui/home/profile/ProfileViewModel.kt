package es.samiralkalii.myapps.soporteit.ui.home.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.authlogin.Compare2ImageProfileUseCase
import es.samiralkalii.myapps.usecase.authlogin.SaveProfileImageChangeUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

class ProfileViewModel(private val compare2ImageProfileUseCase: Compare2ImageProfileUseCase,
                       private val saveProfileImageChangeUseCase: SaveProfileImageChangeUseCase
): ViewModel() {

    private val logger = LoggerFactory.getLogger(ProfileViewModel::class.java)

    private val _imageProfile= MutableLiveData<Uri?>()
    val imageProfile: LiveData<Uri?>
        get()= _imageProfile

    private val _showSaveMenu= MutableLiveData<Boolean>(false)
    val showSaveMenu: LiveData<Boolean>
        get()= _showSaveMenu

    private val _progressVisible= MutableLiveData<Boolean>(false)
    val progressVisible: LiveData<Boolean>
        get()= _progressVisible

    private val _profileChangeState= MutableLiveData<Event<ScreenState<ProfileChangeState>>>()
    val profileChangeState: LiveData<Event<ScreenState<ProfileChangeState>>>
        get()= _profileChangeState

    lateinit var user: User

    init {

    }

    fun publishUser(userParam: User) {
        user= userParam
        logger.debug(user.localProfileImage+ "desde profileViewModelllll")
        if (user.localProfileImage.isNotBlank()) {
            _imageProfile.value= Uri.fromFile(File(user.localProfileImage))
        }
    }

    fun updateShowSaveMenu() {
        _showSaveMenu.value= true
    }


    fun updateImageProfile(imgUri: Uri?) {
        _imageProfile.value= imgUri
        if (user.localProfileImage.isBlank() && imgUri!= null) {
            _showSaveMenu.value= true
        } else if (!user.localProfileImage.isBlank() && imgUri== null) {
            _showSaveMenu.value = true
        } else if (user.localProfileImage.isBlank() && imgUri== null) {
            _showSaveMenu.value= false
        } else {
            viewModelScope.launch() {
                val equals = async(Dispatchers.IO) {
                    compare2ImageProfileUseCase(
                        imgUri.toString(),
                        user.localProfileImage
                    )
                }.await()
                _showSaveMenu.value= !equals
            }
        }
    }

    fun onSaveClick() {

        _progressVisible.value= true
        val errorHandler = CoroutineExceptionHandler { _, error ->
            _progressVisible.postValue(false)
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
                saveProfileImageChangeUseCase(user, _imageProfile.value?.toString() ?: "")
            }.await()
            _progressVisible.value = false
            _showSaveMenu.value= false
            _profileChangeState.value= Event(ScreenState.Render(ProfileChangeState.changeOk))
        }


    }


}