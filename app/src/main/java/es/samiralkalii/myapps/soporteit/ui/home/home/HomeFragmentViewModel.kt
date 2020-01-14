package es.samiralkalii.myapps.soporteit.ui.home.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.teammanagement.AddTeamUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class HomeFragmentViewModel(private val addTeamUseCase: AddTeamUseCase): ViewModel() {

    private val logger = LoggerFactory.getLogger(HomeFragmentViewModel::class.java)

    lateinit var user: User


    //create team
    private val _dialogCreateTeamState= MutableLiveData<MyDialog.DialogState>()
    val dialogCreateTeamState: LiveData<MyDialog.DialogState>
        get() = _dialogCreateTeamState

    fun updateDialogCreateState(state: MyDialog.DialogState) {
        _dialogCreateTeamState.value= state
    }

    private val _teamAddedOk= MutableLiveData<Event<ScreenState<HomeFragmentChangeState>>>()
    val teamAddedOk: LiveData<Event<ScreenState<HomeFragmentChangeState>>>
        get() = _teamAddedOk


    //end create team

    fun publishUser(userParam: User) {
        user= userParam
    }

    fun onTeamCreateClick(teamName: String) {

        logger.debug("On team create clicked")


        _dialogCreateTeamState.value= MyDialog.DialogState.ShowLoading
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            when (error) {
                is FirebaseNetworkException -> {
                    _teamAddedOk.postValue(Event(ScreenState.Render(HomeFragmentChangeState.ShowMessage(R.string.no_internet_connection))))
                }
                else -> {
                    _teamAddedOk.postValue(Event(ScreenState.Render(HomeFragmentChangeState.ShowMessage(R.string.no_internet_connection))))
                }
            }
        }
        viewModelScope.launch(errorHandler) {
            val result= async(Dispatchers.IO) {
                addTeamUseCase(Team(name= teamName, nameInsensitive = teamName.toUpperCase(), boss = user.id))
            }.await()
            if (result.isNotBlank()) {
                user.team= teamName
                user.teamId= result
                _teamAddedOk.value= Event(ScreenState.Render(HomeFragmentChangeState.teamAddedOk))
            } else {
                _teamAddedOk.postValue(Event(ScreenState.Render(HomeFragmentChangeState.ShowMessage(R.string.team_already_exist))))
            }
        }

    }

    /*fun loadAllUsers() {
        val errorHandler = CoroutineExceptionHandler { _, error ->
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
            _allUsers.value= Event(result)
        }
    }*/

    /*fun onInviteClick() {
        logger.debug("On invite clicked")
        //loadAllUsers()
    }*/

    fun onGroupCreateClick() {
        logger.debug("On group create clicked")
    }




}