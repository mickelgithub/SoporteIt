package es.samiralkalii.myapps.soporteit.ui.home.notificactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.NotifType
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.profile.ProfileChangeState
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.usecase.notification.GetNotificationsUseCase
import es.samiralkalii.myapps.usecase.notification.NotificationCategory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class HomeNotificationsFragmentViewModel(private val getNotificationsUseCase: GetNotificationsUseCase): ViewModel() {

    private val logger = LoggerFactory.getLogger(HomeNotificationsFragmentViewModel::class.java)


    lateinit var user: User

    private val _receivedNotifications= MutableLiveData<List<Notification>>()
    val receivedNotifications: LiveData<List<Notification>>
        get() = _receivedNotifications

    fun publishUser(userParam: User) {
        user= userParam
    }

    fun getReceivedNotifications() {

        //_progressVisible.value= MyDialog.DialogState.ShowLoading
        val errorHandler = CoroutineExceptionHandler { _, error ->
            /*logger.error(error.toString(), error)
            when (error) {
                is FirebaseNetworkException -> {
                    _profileChangeState.postValue(
                        Event(
                            ScreenState.Render(
                                ProfileChangeState.ShowMessage(
                                    R.string.no_internet_connection)))
                    )
                }
                else -> {
                    _profileChangeState.postValue(
                        Event(
                            ScreenState.Render(
                                ProfileChangeState.ShowMessage(
                                    R.string.no_internet_connection)))
                    )
                }
            }*/
        }

        viewModelScope.launch(errorHandler) {
            val result = async(Dispatchers.IO) {
                getNotificationsUseCase.getNotifications("mfpm76M8sSg6xAwbGCgu1Cn62UY2", NotificationCategory.RECEIVED)
            }.await()

            _receivedNotifications.value = result
        }
    }

}