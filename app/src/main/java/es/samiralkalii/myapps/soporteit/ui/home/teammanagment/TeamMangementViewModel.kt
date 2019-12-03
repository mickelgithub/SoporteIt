package es.samiralkalii.myapps.soporteit.ui.home.teammanagment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.samiralkalii.myapps.domain.User
import org.slf4j.LoggerFactory

class TeamMangementViewModel: ViewModel() {

    private val logger = LoggerFactory.getLogger(TeamMangementViewModel::class.java)

    lateinit var user: User

    private val _progressVisible= MutableLiveData<Boolean>(false)
    val progressVisible: LiveData<Boolean>
        get()= _progressVisible



    fun publishUser(userParam: User) {
        user= userParam
    }

    fun onTeamCreateClick() {
        logger.debug("On team create clicked")

    }

    fun onInviteClick() {
        logger.debug("On invite clicked")
    }

    fun onGroupCreateClick() {
        logger.debug("On group create clicked")
    }
}