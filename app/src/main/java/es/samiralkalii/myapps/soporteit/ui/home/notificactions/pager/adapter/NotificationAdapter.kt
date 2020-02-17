package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.common.adapter.DataBindingAdapter
import es.samiralkalii.myapps.soporteit.ui.common.adapter.DataBindingViewHolder
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.NotificationsFragment
import org.slf4j.LoggerFactory


class NotificationAdapter(val homeNotificationsFragmentViewModel: HomeNotificationsFragmentViewModel, val fragment: NotificationsFragment): DataBindingAdapter<NotificationAdapter.NotificationViewModel>(DiffCallback()) {

    private val logger= LoggerFactory.getLogger(NotificationAdapter::class.java)

    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            NotificationViewModel.LoadingItem -> R.layout.loading_item_view
            is NotificationViewModel.InfoNotificationViewModel -> R.layout.view_holder_notification_item_info
            is NotificationViewModel.ReplyNotificationViewModel -> R.layout.view_holder_notification_item_reply
        }

    /*override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        if (holder.itemViewType== 0) {
            holder.bind(notifications[position])
            val bindingHolder= (holder.binding as ViewHolderNotificationItemInfoBinding)
            val itemTop= bindingHolder.notificationItemTop
            val itemBottom= bindingHolder.notificationItemBottom
            val revealLayout= holder.binding.root as SwipeRevealLayout
            revealLayout.close(false)
            val animationOk= holder.binding.animationOk
            bindingHolder.delete.visibility= View.GONE
            //revealLayout.dragLock(true)
            if (homeNotificationsFragmentViewModel.isInfoNotification(notifications[position]) && notifications[position].state== NotifState.PENDING) {
                revealLayout.dragLock(true)
                itemTop.setOnClickListener({v ->
                    homeNotificationsFragmentViewModel.updateNotificationStateRead(notifications[position])
                    bindingHolder.delete.visibility= View.GONE
                    revealLayout.open(true)
                    itemBottom.visibility= View.VISIBLE
                    animationOk.playAnimation()
                    revealLayout.postDelayed({
                        revealLayout.close(true)
                        itemBottom.visibility= View.INVISIBLE
                        notifications[position].state= NotifState.READ
                        fragment.updateDeletedMenuItemState(notifications)
                    }, 1100)
                    revealLayout.postDelayed({
                        holder.binding.invalidateAll()
                        notifyItemChanged(position)
                        //recyclerView.invalidateItemDecorations()
                    }, 1300)

                })
            } else if (homeNotificationsFragmentViewModel.isInfoNotification(notifications[position])) {
                revealLayout.dragLock(false)
                itemTop.setOnClickListener { null }
                animationOk.visibility= View.GONE
                bindingHolder.delete.visibility= View.VISIBLE
                bindingHolder.delete.setOnClickListener({v ->
                    logger.debug("delete clicked.....")
                })

            } else if (homeNotificationsFragmentViewModel.isReplyNotification(notifications[position]) &&
                notifications[position].state== NotifState.PENDING) {
                animationOk.visibility= View.GONE
                bindingHolder.delete.visibility= View.GONE
                revealLayout.dragLock(true)
                itemTop.setOnClickListener({v ->
                    //homeNotificationsFragmentViewModel.updateNotificationStateRead(notifications[position])
                    itemBottom.visibility= View.VISIBLE
                    revealLayout.open(true)
                    animationOk.playAnimation()
                    /*revealLayout.postDelayed({
                        revealLayout.close(true)
                        itemBottom.visibility= View.INVISIBLE
                        //notifications[position].state= NotifState.READ
                        //fragment.updateDeletedMenuItemState(notifications)
                    }, 1100)
                    revealLayout.postDelayed({
                        holder.binding.invalidateAll()
                        notifyItemChanged(position)
                        //recyclerView.invalidateItemDecorations()
                    }, 1300)*/
                    revealLayout.dragLock(false)

                })




            }
        } else {
            holder.itemView.isClickable= false
        }
    }*/


    override fun onViewAttachedToWindow(holder: DataBindingViewHolder<NotificationViewModel>) {
        super.onViewAttachedToWindow(holder)
        logger.debug("onViewAttached....")
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationViewModel>() {

        override fun areItemsTheSame(oldItem: NotificationViewModel, newItem: NotificationViewModel)=
            if (oldItem is NotificationViewModel.LoadingItem && newItem is NotificationViewModel.LoadingItem) {
                true
            } else if (oldItem is NotificationViewModel.LoadingItem && newItem !is NotificationViewModel.LoadingItem){
                false;
            } else if (oldItem !is NotificationViewModel.LoadingItem && newItem is NotificationViewModel.LoadingItem){
                false;
            } else {
                oldItem.notif!!.id== newItem.notif!!.id
            }

        override fun areContentsTheSame(oldItem: NotificationViewModel, newItem: NotificationViewModel)=
            if (oldItem is NotificationViewModel.LoadingItem && newItem is NotificationViewModel.LoadingItem) {
                true
            } else if (oldItem is NotificationViewModel.LoadingItem && newItem !is NotificationViewModel.LoadingItem){
                false;
            } else if (oldItem !is NotificationViewModel.LoadingItem && newItem is NotificationViewModel.LoadingItem){
                false;
            } else {
                oldItem.notif!!.id== newItem.notif!!.id &&
                        oldItem.backgroundColor.value== newItem.backgroundColor.value &&
                        oldItem.open.value== newItem.open.value



            }
    }

    override fun onBindViewHolder(
        holder: DataBindingViewHolder<NotificationViewModel>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)

        logger.debug("onBindViewHolder---------------"+ position)
        val item= getItem(position)
        /*when (item) {
            is NotificationAdapter.NotificationViewModel.InfoNotificationViewModel -> item.init()
        }*/
    }

    sealed class NotificationViewModel(val notif: Notification?, val parentViewModel: HomeNotificationsFragmentViewModel?, val notificationAdapter: NotificationAdapter?) {

        private val logger= LoggerFactory.getLogger(NotificationViewModel::class.java)
        //0 initial state, so close without animation
        //1 open state, we have to open with animation
        //2 close state, we have to close with animation
        private val _open= MutableLiveData<Int>()
        val open: LiveData<Int>
            get() = _open

        private val _backgroundColor= MutableLiveData<Int>()
        val backgroundColor: LiveData<Int>
            get() = _backgroundColor

        init {
            init()
        }



        open fun init() {
            _open.value= 0
            _backgroundColor.value= if (notif?.state== NotifState.PENDING) R.color.notif_backgroud_ini else R.color.notif_backgroud_read
        }



        open fun onClick() {
            logger.debug("se ha hecho click en ${notif!!.id}")
            when(this) {
                NotificationViewModel.LoadingItem -> {}
                is NotificationViewModel.InfoNotificationViewModel -> {

                }
                is NotificationViewModel.ReplyNotificationViewModel -> {

                }
            }
            if (_open.value== 0)
                _open.value= 1
            else if (_open.value== 1)
                _open.value= 2
            else if (_open.value== 2) {
                _open.value= 1
            }

            notificationAdapter!!.notifyItemChanged(notificationAdapter.currentList.indexOf(this))

        }

        object LoadingItem: NotificationViewModel(null, null, null)
        class InfoNotificationViewModel(notif: Notification, parentViewModel: HomeNotificationsFragmentViewModel, notificationAdapter: NotificationAdapter):
            NotificationViewModel(notif, parentViewModel, notificationAdapter)
        class ReplyNotificationViewModel(notif: Notification, parentViewModel: HomeNotificationsFragmentViewModel, notificationAdapter: NotificationAdapter):
            NotificationViewModel(notif, parentViewModel, notificationAdapter)
    }



}