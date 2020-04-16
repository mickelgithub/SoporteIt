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
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
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


    private val _progressVisible= MutableLiveData<MyDialog.DialogState>()
    val progressVisible: LiveData<MyDialog.DialogState>
        get()= _progressVisible

    fun updateDialogState(dialog: MyDialog.DialogState) {
        _progressVisible.value= dialog
    }

    private val _items= MutableLiveData<List<MemberUserViewModelTemplate>?>()
    val items: LiveData<List<MemberUserViewModelTemplate>?>
        get() = _items


    //end create team

    fun init() {

    }






}