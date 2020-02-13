package es.samiralkalii.myapps.soporteit.ui.home.notificactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.NotifType
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.usecase.notification.GetNotificationsUseCase
import es.samiralkalii.myapps.usecase.notification.NotificationCategory
import es.samiralkalii.myapps.usecase.notification.UpdateNotificationStateUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class HomeNotificationsFragmentViewModel(private val getNotificationsUseCase: GetNotificationsUseCase,
                                         private val updateNotificationStateUseCase: UpdateNotificationStateUseCase): ViewModel() {

    private val logger = LoggerFactory.getLogger(HomeNotificationsFragmentViewModel::class.java)


    private lateinit var user: User

    private val _receivedNotifications= MutableLiveData<List<Notification>>()
    val receivedNotifications: LiveData<List<Notification>>
        get() = _receivedNotifications

    private val _sentNotifications= MutableLiveData<List<Notification>>()
    val sentNotifications: LiveData<List<Notification>>
        get() = _sentNotifications

    fun publishUser(userParam: User) {
        this.user= userParam
    }


    fun getReceivedNotifications() {

        //_progressVisible.value= MyDialog.DialogState.ShowLoading
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            /*when (error) {
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
                getNotificationsUseCase(user.id, NotificationCategory.RECEIVED)
            }.await()
            _receivedNotifications.value = result
        }
    }

    fun getSentNotifications() {

        //_progressVisible.value= MyDialog.DialogState.ShowLoading
        val errorHandler = CoroutineExceptionHandler { _, error ->
            logger.error(error.toString(), error)
            /*when (error) {
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
                getNotificationsUseCase(user.id, NotificationCategory.SENT)
            }.await()
            _sentNotifications.value = result
        }
    }

    override fun onCleared() {
        super.onCleared()
        logger.debug("cleared.......")
    }

    fun updateNotificationStateRead(notification: Notification) {
        viewModelScope.launch {
            val result = async(Dispatchers.IO) {
                updateNotificationStateUseCase(user.id, notification.id, NotifState.READ)
            }.await()
        }
    }

    fun isInfoNotification(notification: Notification)=  (notification.type== NotifType.INFO)

    fun isReplyNotification(notification: Notification)=  (notification.type.toString().startsWith("ACTION"))

}