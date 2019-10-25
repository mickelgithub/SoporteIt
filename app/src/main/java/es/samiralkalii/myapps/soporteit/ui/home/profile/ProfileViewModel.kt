package es.samiralkalii.myapps.soporteit.ui.home.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.samiralkalii.myapps.domain.User
import org.slf4j.LoggerFactory
import java.io.File

class ProfileViewModel: ViewModel() {

    private val logger = LoggerFactory.getLogger(ProfileViewModel::class.java)

    private val _imageProfile= MutableLiveData<Uri?>()
    val imageProfile: LiveData<Uri?>
        get()= _imageProfile

    lateinit var user: User

    init {

    }

    fun publishUser(userParam: User) {
        user= userParam
        logger.debug(user.localProfileImage+ "desde profileViewModelllll")
            val fileImage= File(user.localProfileImage)
            if (fileImage.exists()) {
                logger.debug("SIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII")
            }
        _imageProfile.value= Uri.fromFile(fileImage)
    }


}