package es.samiralkalii.myapps.soporteit.ui.home.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.usecase.authlogin.Compare2ImageProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File

class ProfileViewModel(private val compare2ImageProfileUseCase: Compare2ImageProfileUseCase): ViewModel() {

    private val logger = LoggerFactory.getLogger(ProfileViewModel::class.java)

    private val _imageProfile= MutableLiveData<Uri?>()
    val imageProfile: LiveData<Uri?>
        get()= _imageProfile

    private val _showSaveMenu= MutableLiveData<Boolean>(false)

    val showSaveMenu: LiveData<Boolean>
        get()= _showSaveMenu

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
                    compare2ImageProfileUseCase.compare2Images(
                        imgUri.toString(),
                        user.localProfileImage
                    )
                }.await()
                if (!equals) {
                    _showSaveMenu.value = true
                }
            }
        }
    }






}