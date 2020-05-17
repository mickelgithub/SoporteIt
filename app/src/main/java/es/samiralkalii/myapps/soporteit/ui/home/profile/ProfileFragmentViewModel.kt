package es.samiralkalii.myapps.soporteit.ui.home.profile

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.storage.StorageException
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.BaseFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.SI
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.authlogin.Compare2ImageProfileUseCase
import es.samiralkalii.myapps.usecase.authlogin.UpdateProfileImageUseCase
import es.samiralkalii.myapps.usecase.usermanagment.GetUserUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class ProfileFragmentViewModel(private val compare2ImageProfileUseCase: Compare2ImageProfileUseCase,
                               private val updateProfileImageUseCase: UpdateProfileImageUseCase,
                               private val getUserUseCase: GetUserUseCase
): BaseFragmentViewModel() {

    private val logger = LoggerFactory.getLogger(ProfileFragmentViewModel::class.java)

    override val uiModel= ProfileFragmentUiModel()


    override fun init(bundle: Bundle?) {
        viewModelScope.launch {
            uiModel._user.value = async(Dispatchers.IO) {
                getUserUseCase()
            }.await()
            uiModel._user.value?.let {
                uiModel._profileImage.value= if (!it.profileImage.isNullOrBlank()) Uri.parse(it.profileImage) else null
                uiModel._showVerified.value= (it.isBoss && it.bossConfirmation== SI) ||
                        (!it.isBoss && it.membershipConfirmation== SI)
                uiModel._showNotVerifiedYet.value= (it.isBoss && it.bossConfirmation== "") ||
                        (!it.isBoss && it.membershipConfirmation== "")
            }
        }
    }

    fun updateShowSaveMenu() {
        uiModel._showSaveMenu.value= true
    }


    fun updateImageProfile(imgUri: Uri?) {
        uiModel._profileImage.value= imgUri
        val localProfileImage= uiModel._user.value?.profileImage ?: ""
        if (localProfileImage.isBlank() && imgUri!= null) {
            uiModel.imageChanged= true
            uiModel._showSaveMenu.value= true
        } else if (localProfileImage.isNotBlank() && imgUri== null) {
            uiModel.imageChanged= true
            uiModel._showSaveMenu.value = true
        } else if (localProfileImage.isBlank() && imgUri== null) {
            uiModel.imageChanged= false
            uiModel._showSaveMenu.value= false
        } else {
            viewModelScope.launch() {
                val equals = async(Dispatchers.IO) {
                    compare2ImageProfileUseCase(
                        imgUri.toString(),
                        uiModel._user.value?.profileImage ?: ""
                    )
                }.await()
                uiModel.imageChanged= !equals
                uiModel._showSaveMenu.value= !equals
            }
        }
    }

    fun onSaveClick() {
        uiModel._progressVisible.value= MyDialog.DialogState.ShowProgressDialog()
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            when (error) {
                is FirebaseNetworkException -> {
                    uiModel._profileChangeState.postValue(Event(ScreenState.Render(ProfileChangeState.ShowMessage(R.string.no_internet_connection))))
                }
                is StorageException -> {
                    uiModel._profileChangeState.postValue(Event(ScreenState.Render(ProfileChangeState.ShowMessage(R.string.no_internet_connection))))
                }
                else -> {
                    uiModel._profileChangeState.postValue(Event(ScreenState.Render(ProfileChangeState.ShowMessage(R.string.not_controled_error))))
                }
            }
        }
        viewModelScope.launch(errorHandler) {
            uiModel._user.value= async(Dispatchers.IO) {
                updateProfileImageUseCase(uiModel._user.value?.id!!, uiModel._user.value?.profileImage!!, uiModel._profileImage.value?.toString() ?: "")
                getUserUseCase()
            }.await()
            uiModel._profileImage.value= if (!uiModel._user.value?.profileImage.isNullOrBlank()) Uri.parse(uiModel._user.value?.profileImage) else null
            uiModel._showSaveMenu.value= false
            uiModel._profileChangeState.value= Event(ScreenState.Render(ProfileChangeState.changeOk))
        }
    }

    fun updateProgressVisible(progressVisible: MyDialog.DialogState) {
        uiModel._progressVisible.value= progressVisible
    }

}