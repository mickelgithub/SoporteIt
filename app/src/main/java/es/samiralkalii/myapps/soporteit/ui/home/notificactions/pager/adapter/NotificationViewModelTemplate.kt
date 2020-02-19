package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.NotifType
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.NotificationItemInfoBinding
import es.samiralkalii.myapps.soporteit.databinding.NotificationItemReplyBinding
import org.slf4j.LoggerFactory


private const val CLOSE_WITHOUT_ANIMATION= 0
private const val OPEN_WITH_ANIMATION= 1
private const val CLOSE_WITH_ANIMATION= 2
private const val ANIMATION_DURATION= 500L

sealed class NotificationViewModelTemplate() {

    private val logger= LoggerFactory.getLogger(NotificationViewModelTemplate::class.java)

    class NotificationViewModelInfo(val notif: Notification, val adapter: NotificationAdapter): NotificationViewModelTemplate() {

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
            _open.value= CLOSE_WITHOUT_ANIMATION
            init()
        }

        fun init() {
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
            logger.debug("onDelete click...")
        }

        private fun onItemInfoClick(notificationItemInfoBinding: NotificationItemInfoBinding) {
            if (isItemClosed()) {
                _open.value= OPEN_WITH_ANIMATION
                if (notif.state== NotifState.PENDING) {
                    notificationItemInfoBinding.animationOk.playAnimation()
                    notif.state= NotifState.READ
                    viewHolder.binding.root.postDelayed({
                        adapter.notifyItemChanged(viewHolder.adapterPosition)
                    }, ANIMATION_DURATION)
                }
                notificationItemInfoBinding.invalidateAll()
            } else {
                _open.value= CLOSE_WITH_ANIMATION
                notificationItemInfoBinding.invalidateAll()
            }
        }

        private fun isItemClosed()= (_open.value== CLOSE_WITH_ANIMATION ||
                _open.value== CLOSE_WITHOUT_ANIMATION)

    }

    //**********************************************************************************************

    class NotificationViewModelReply(val notif: Notification, val adapter: NotificationAdapter): NotificationViewModelTemplate() {

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

        init {
            _open.value= CLOSE_WITHOUT_ANIMATION
            init()
        }

        fun init() {
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
            logger.debug("onDelete click...")
        }

        fun onKoClick() {
            logger.debug("KO click")
        }

        fun onOkClick() {
            logger.debug("OK click")
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

    }

    object NotificationViewModelLoading: NotificationViewModelTemplate()

}