package es.samiralkalii.myapps.soporteit.ui.home.teammanagment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.teammanagement.AddTeamUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetAllUsersButBosesUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class TeamMangementViewModel(private val addTeamUseCase: AddTeamUseCase, private val getAllUsersButBosesUseCase: GetAllUsersButBosesUseCase): ViewModel() {

    private val logger = LoggerFactory.getLogger(TeamMangementViewModel::class.java)

    lateinit var user: User

    private val _progressVisible= MutableLiveData<Boolean>(false)
    val progressVisible: LiveData<Boolean>
        get()= _progressVisible

    private val _addTeamButtonEnabled= MutableLiveData<Boolean>(true)
    val addTeamButtonEnabled: LiveData<Boolean>
        get() = _addTeamButtonEnabled

    private val _teamAddedOk= MutableLiveData<Event<ScreenState<TeamManagementChangeState>>>()
    val teamAddedOk: LiveData<Event<ScreenState<TeamManagementChangeState>>>
        get() = _teamAddedOk

    private val _allUsers= MutableLiveData<List<User>>()
    val allUsers: LiveData<List<User>>
        get() = _allUsers


    fun publishUser(userParam: User) {
        user= userParam
        _addTeamButtonEnabled.value= !user.teamCreated
    }

    fun onTeamCreateClick(teamName: String) {
        logger.debug("On team create clicked")

        _progressVisible.value= true
        val errorHandler = CoroutineExceptionHandler { _, error ->
            _progressVisible.postValue(false)
            logger.error(error.toString(), error)
            when (error) {
                is FirebaseNetworkException -> {
                    _teamAddedOk.postValue(
                        Event(
                            ScreenState.Render(
                                TeamManagementChangeState.ShowMessage(
                                    R.string.no_internet_connection)))
                    )
                }
                else -> {
                    _teamAddedOk.postValue(
                        Event(
                            ScreenState.Render(
                                TeamManagementChangeState.ShowMessage(
                                    R.string.no_internet_connection)))
                    )
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            async(Dispatchers.IO) {
                addTeamUseCase(Team(teamName), user.id)
            }.await()
            _progressVisible.value = false
            _addTeamButtonEnabled.value= false
            _teamAddedOk.value= Event(ScreenState.Render(TeamManagementChangeState.teamAddedOk))
        }

    }

    fun loadAllUsers() {
        val errorHandler = CoroutineExceptionHandler { _, error ->
            _progressVisible.postValue(false)
            logger.error(error.toString(), error)
            when (error) {
                is FirebaseNetworkException -> {
                    /*_teamAddedOk.postValue(
                        Event(
                            ScreenState.Render(
                                TeamManagementChangeState.ShowMessage(
                                    R.string.no_internet_connection)))
                    )*/
                }
                else -> {
                    /*_teamAddedOk.postValue(
                        Event(
                            ScreenState.Render(
                                TeamManagementChangeState.ShowMessage(
                                    R.string.no_internet_connection)))
                    )*/
                }
            }
        }

        viewModelScope.launch(errorHandler) {
            val result= async(Dispatchers.IO) {
                getAllUsersButBosesUseCase()
            }.await()
            _allUsers.value= result
        }
    }

    /*fun onInviteClick() {
        logger.debug("On invite clicked")
        //loadAllUsers()
    }*/

    fun onGroupCreateClick() {
        logger.debug("On group create clicked")
    }
}