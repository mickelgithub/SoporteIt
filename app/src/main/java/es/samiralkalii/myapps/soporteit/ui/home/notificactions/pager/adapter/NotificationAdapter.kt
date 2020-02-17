package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.common.adapter.DataBindingAdapter
import es.samiralkalii.myapps.soporteit.ui.common.adapter.DataBindingViewHolder
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.NotificationsFragment
import org.slf4j.LoggerFactory


class NotificationAdapter(val homeNotificationsFragmentViewModel: HomeNotificationsFragmentViewModel, val fragment: NotificationsFragment): DataBindingAdapter<NotificationAdapter.NotificationViewModel>(DiffCallback()) {

    private val logger= LoggerFactory.getLogger(NotificationAdapter::class.java)

    private lateinit var recyclerView: RecyclerView

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

    override fun onBindViewHolder(
        holder: DataBindingViewHolder<NotificationViewModel>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        logger.debug("onBindViewHolder---------------"+ position)
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationViewModel>() {
        override fun areItemsTheSame(oldItem: NotificationViewModel, newItem: NotificationViewModel): Boolean {
            if (oldItem is NotificationViewModel.LoadingItem && newItem is NotificationViewModel.LoadingItem) {
                return true
            } else if (oldItem is NotificationViewModel.LoadingItem && newItem !is NotificationViewModel.LoadingItem){
                return false;
            } else if (oldItem !is NotificationViewModel.LoadingItem && newItem is NotificationViewModel.LoadingItem){
                return false;
            } else {
                return oldItem.notif!!.id== newItem.notif!!.id
            }
        }

        override fun areContentsTheSame(oldItem: NotificationViewModel, newItem: NotificationViewModel): Boolean {
            if (oldItem is NotificationViewModel.LoadingItem && newItem is NotificationViewModel.LoadingItem) {
                return true
            } else if (oldItem is NotificationViewModel.LoadingItem && newItem !is NotificationViewModel.LoadingItem){
                return false;
            } else if (oldItem !is NotificationViewModel.LoadingItem && newItem is NotificationViewModel.LoadingItem){
                return false;
            } else {
                return oldItem.notif!!.id== newItem.notif!!.id
            }
        }
    }

    sealed class NotificationViewModel(val notif: Notification?) {

        private val logger= LoggerFactory.getLogger(NotificationViewModel::class.java)

        open fun onClick() {
            logger.debug("asdasdasdasd......................................................")
            logger.debug("se ha hecho click en ${notif!!.id}")
        }

        object LoadingItem: NotificationViewModel(null)
        class InfoNotificationViewModel(notif: Notification): NotificationViewModel(notif)
        class ReplyNotificationViewModel(notif: Notification): NotificationViewModel(notif)
    }



}