package es.samiralkalii.myapps.soporteit.ui.home.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Team
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
import es.samiralkalii.myapps.soporteit.ui.logup.LogupState
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.teammanagement.AddTeamUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetGroupsUseCase
import es.samiralkalii.myapps.usecase.usermanagment.GetUserUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class HomeFragmentViewModel(private val getGroupsUseCase: GetGroupsUseCase,
                            private val getUserUseCase: GetUserUseCase): ViewModel() {

    private val logger = LoggerFactory.getLogger(HomeFragmentViewModel::class.java)

    lateinit var user: User


    private val _progressVisible= MutableLiveData<MyDialog.DialogState>()
    val progressVisible: LiveData<MyDialog.DialogState>
        get()= _progressVisible

    fun updateDialogState(dialog: MyDialog.DialogState) {
        _progressVisible.value= dialog
    }

    private val _items= MutableLiveData<List<MemberUserViewModelTemplate>?>()
    val items: LiveData<List<MemberUserViewModelTemplate>?>
        get() = _items

    private val _getGroupsActionState= MutableLiveData<Event<ScreenState<HomeFragmentStates.GetGroupsState>>>()
    val getGroupsActionState: LiveData<Event<ScreenState<HomeFragmentStates.GetGroupsState>>>
        get() = _getGroupsActionState


    //end create team



    fun updateItems(items: List<MemberUserViewModelTemplate>) {
        _items.value= items
    }

    init {
        init()
    }

    fun init() {
        //_progressVisible.value= MyDialog.DialogState.ShowProgressDialog()
        _items.value= listOf(MemberUserViewModelTemplate.MemberUserViewModelLoading)
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            var message= R.string.not_controled_error
            if (error is FirebaseFirestoreException) {
                if (error.code== FirebaseFirestoreException.Code.UNAVAILABLE) {
                    message= R.string.no_internet_connection
                }
            }
            _getGroupsActionState.postValue(Event(ScreenState.Render(HomeFragmentStates.GetGroupsState.ShowMessage(message))))
        }
        viewModelScope.launch(errorHandler) {

            val groupList= async(Dispatchers.IO) {
                user= getUserUseCase()
                getGroupsUseCase(user)
            }.await()
            var result= listOf<MemberUserViewModelTemplate>(MemberUserViewModelTemplate.MemberUserViewModelEmpty)
            if (!groupList.isEmpty) {
                result= groupList.groups.map {
                    val items= mutableListOf<MemberUserViewModelTemplate>()
                    items.add(MemberUserViewModelTemplate.GroupMemberUserViewModel(it.name))
                    items.addAll(it.members.map { userItem -> MemberUserViewModelTemplate.MemberUserViewModel(userItem, this@HomeFragmentViewModel) })
                    items
                }.flatMap({it})
            }
            _getGroupsActionState.value= Event(ScreenState.Render(HomeFragmentStates.GetGroupsState.GetGroupsStateOk(result)))
        }
    }

    fun confirmMember(user: String) {

    }






}