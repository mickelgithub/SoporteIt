package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.NotifType
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.NotificationItemInfoBinding
import org.slf4j.LoggerFactory

sealed class NotificationViewModelTemplate(val notif: Notification?, val adapter: NotificationAdapter?) {

    private val logger= LoggerFactory.getLogger(NotificationViewModelTemplate::class.java)


    class NotificationViewModel(notif: Notification, adapter: NotificationAdapter): NotificationViewModelTemplate(notif, adapter) {

        private val logger= LoggerFactory.getLogger(NotificationViewModel::class.java)

        lateinit var viewHolder: NotificationAdapter.NotificationViewHolder

        private val _deleteVisible= MutableLiveData<Boolean>()
        val deleteVisible: LiveData<Boolean>
            get() = _deleteVisible

        private val _backgroundColor= MutableLiveData<Int>(R.color.notif_backgroud_ini)
        val backgroundColor: LiveData<Int>
            get() = _backgroundColor

        private val _open= MutableLiveData<Int>()
        val open: LiveData<Int>
            get() = _open

        init {
            init()
        }

        fun init() {
            _open.value= 0
            notif?.let {
                if (it.state== NotifState.PENDING) {
                    _backgroundColor.value= R.color.notif_backgroud_ini
                } else {
                    _backgroundColor.value= R.color.notif_backgroud_read
                }
                _deleteVisible.value = (notif.state == NotifState.READ) ?: false
            }
        }

        fun onClick() {
            logger.debug("clicked.....")

            notif?.let {
                if (it.type== NotifType.INFO) {
                    val notificationItemInfoBinding= viewHolder!!.binding as NotificationItemInfoBinding
                    onItemInfoClick(notificationItemInfoBinding)
                }
            }

            //adapter?.notifyItemChanged(viewHolder.adapterPosition)
            //viewHolder.binding.invalidateAll()

        }

        private fun onItemInfoClick(notificationItemInfoBinding: NotificationItemInfoBinding) {
            notif?.let {
                if (_open.value== 2 || _open.value== 0) {
                    _open.value= 1
                    notificationItemInfoBinding.invalidateAll()
                    if (notificationItemInfoBinding.animationOk.visibility== View.VISIBLE) {
                        notificationItemInfoBinding.animationOk.playAnimation()
                    }
                    //notificationItemInfoBinding.swipeReveal.open(true)
                } else {
                    _open.value= 2
                    if (notif?.state== NotifState.PENDING) {
                        notif?.state= NotifState.READ
                        viewHolder!!.binding.root.postDelayed({
                            adapter?.notifyItemChanged(viewHolder!!.adapterPosition)
                        }, 300)
                    }
                    //notificationItemInfoBinding.swipeReveal.close(true)
                    notificationItemInfoBinding.invalidateAll()


                }
            }
        }

    }


    object NotificationViewModelLoading: NotificationViewModelTemplate(null, null)


}