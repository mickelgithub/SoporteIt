package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.NotifType
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.domain.notification.Reply
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.NotificationItemInfoBinding
import es.samiralkalii.myapps.soporteit.databinding.NotificationItemReplyBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.PromptTextDialog
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.NotificationsFragment
import org.slf4j.LoggerFactory


private const val CLOSE_WITHOUT_ANIMATION= 0
private const val OPEN_WITH_ANIMATION= 1
private const val CLOSE_WITH_ANIMATION= 2
private const val ANIMATION_DURATION= 1300L

sealed class NotificationViewModelTemplate() {

    private val logger= LoggerFactory.getLogger(NotificationViewModelTemplate::class.java)

    class NotificationViewModelInfo(val notif: Notification, val adapter: NotificationAdapter,
                                    val homeNotificationsFragmentViewModel: HomeNotificationsFragmentViewModel): NotificationViewModelTemplate() {

        private val logger= LoggerFactory.getLogger(NotificationViewModelInfo::class.java)

        lateinit var viewHolder: NotificationAdapter.NotificationViewHolder

        private val _deleteVisible= MutableLiveData<Boolean>()
        val deleteVisible: LiveData<Boolean>
            get() = _deleteVisible

        private val _backgroundColor= MutableLiveData<Int>(R.color.notif_backgroud_new)
        val backgroundColor: LiveData<Int>
            get() = _backgroundColor

        private val _open= MutableLiveData<Int>()
        val open: LiveData<Int>
            get() = _open

        init {
            init()
        }

        fun init() {
            _open.value= CLOSE_WITHOUT_ANIMATION
            if (notif.state== NotifState.PENDING) {
                _backgroundColor.value= R.color.notif_backgroud_new
            } else {
                _backgroundColor.value= R.color.notif_backgroud_read
            }
            _deleteVisible.value = (notif.state == NotifState.READ)
        }

        fun onNotificationItemViewClick() {
            logger.debug("clicked.....")
            if (notif.type == NotifType.INFO) {
                onItemInfoClick(viewHolder.binding as NotificationItemInfoBinding)
            }
        }

        fun onDeleteClick() {
            logger.debug("delete clicked.....")
            homeNotificationsFragmentViewModel.deleteNotification(notif.id)
            adapter.notifications.removeAt(viewHolder.adapterPosition)
            adapter.notifyItemRemoved(viewHolder.adapterPosition)
        }

        private fun onItemInfoClick(notificationItemInfoBinding: NotificationItemInfoBinding) {
            if (isItemClosed()) {
                _open.value= OPEN_WITH_ANIMATION
                if (notif.state== NotifState.PENDING) {
                    notificationItemInfoBinding.animationOk.playAnimation()
                    notif.state= NotifState.READ
                    homeNotificationsFragmentViewModel.updateNotificationStateRead(notif.id, NotifState.READ)
                    viewHolder.binding.root.postDelayed({
                        _deleteVisible.value= true
                        _backgroundColor.value= R.color.notif_backgroud_read
                        notificationItemInfoBinding.invalidateAll()
                    }, ANIMATION_DURATION)
                }
            } else {
                _open.value= CLOSE_WITH_ANIMATION
            }
            notificationItemInfoBinding.invalidateAll()
        }

        private fun isItemClosed()= (_open.value== CLOSE_WITH_ANIMATION ||
                _open.value== CLOSE_WITHOUT_ANIMATION)

    }

    //**********************************************************************************************

    class NotificationViewModelReply(val notif: Notification, val adapter: NotificationAdapter,
                                     val homeNotificationsFragmentViewModel: HomeNotificationsFragmentViewModel,
                                     val fragment: NotificationsFragment): NotificationViewModelTemplate(), PromptTextDialog.OnTextEnteredListener {

        private val logger= LoggerFactory.getLogger(NotificationViewModelReply::class.java)

        lateinit var viewHolder: NotificationAdapter.NotificationViewHolder

        private val _deleteVisible= MutableLiveData<Boolean>()
        val deleteVisible: LiveData<Boolean>
            get() = _deleteVisible

        private val _backgroundColor= MutableLiveData<Int>(R.color.notif_backgroud_new)
        val backgroundColor: LiveData<Int>
            get() = _backgroundColor

        private val _open= MutableLiveData<Int>()
        val open: LiveData<Int>
            get() = _open

        var promptTextDialog: PromptTextDialog?= null

        init {
            init()
        }

        fun init() {
            _open.value= CLOSE_WITHOUT_ANIMATION
            if (notif.state== NotifState.PENDING) {
                _backgroundColor.value= R.color.notif_backgroud_new
            } else {
                _backgroundColor.value= R.color.notif_backgroud_read
            }
            _deleteVisible.value = (notif.state == NotifState.READ)
        }

        fun onNotificationItemViewClick() {
            logger.debug("clicked.....")
            if (notif.type== NotifType.ACTION_INVITE_TEAM) {
                onItemReplyClick(viewHolder.binding as NotificationItemReplyBinding)
            }

        }

        fun onNotificationItemViewLongClick() {
            logger.debug("Long click.....")
        }

        fun onDeleteClick() {
            logger.debug("delete clicked.....")
            homeNotificationsFragmentViewModel.deleteNotification(notif.id)
            adapter.notifications.removeAt(viewHolder.adapterPosition)
            adapter.notifyItemRemoved(viewHolder.adapterPosition)
        }

        private fun showPromptTextDialog() {
            promptTextDialog= fragment.activity!!.supportFragmentManager.findFragmentByTag(PromptTextDialog::class.java.simpleName) as PromptTextDialog?
            if (promptTextDialog== null) {
                promptTextDialog= PromptTextDialog.newInstance("Introduzca el motivo", "El motivo no puede estar vacio", this).apply {
                    show(fragment.activity!!.supportFragmentManager, PromptTextDialog::class.java.simpleName)
                }
            }
        }

        fun onKoClick() {
            logger.debug("KO clicked.....")
            showPromptTextDialog()
        }

        fun onOkClick() {
            logger.debug("OK click")
            notif.state= NotifState.READ
            homeNotificationsFragmentViewModel.replyNotification(notif.id, Reply.OK, "")
            _backgroundColor.value= R.color.notif_backgroud_read
            _open.value= CLOSE_WITH_ANIMATION
            (viewHolder.binding as NotificationItemReplyBinding).invalidateAll()
            viewHolder.binding.root.postDelayed({
                _deleteVisible.value= true
                (viewHolder.binding as NotificationItemReplyBinding).invalidateAll()
            }, ANIMATION_DURATION)
        }

        private fun onItemReplyClick(notificationItemReplyBinding: NotificationItemReplyBinding) {
            if (isItemClosed()) {
                _open.value= OPEN_WITH_ANIMATION
            } else {
                _open.value= CLOSE_WITH_ANIMATION

            }
            notificationItemReplyBinding.invalidateAll()
        }

        private fun isItemClosed()= (_open.value== CLOSE_WITH_ANIMATION ||
                _open.value== CLOSE_WITHOUT_ANIMATION)

        override fun onTextEntered(reasonKo: String) {
            promptTextDialog?.dismiss()
            promptTextDialog= null
            viewHolder.binding.root.postDelayed({
                notif.state= NotifState.READ
                homeNotificationsFragmentViewModel.replyNotification(notif.id, Reply.KO, reasonKo)
                _backgroundColor.value= R.color.notif_backgroud_read
                _open.value= CLOSE_WITH_ANIMATION
                (viewHolder.binding as NotificationItemReplyBinding).invalidateAll()
                viewHolder.binding.root.postDelayed({
                    _deleteVisible.value= true
                    (viewHolder.binding as NotificationItemReplyBinding).invalidateAll()
                }, ANIMATION_DURATION)
            }, 200)
        }

    }

    object NotificationViewModelLoading: NotificationViewModelTemplate()

}