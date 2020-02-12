package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.NotifType
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.databinding.LoadingItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.ViewHolderNotificationItemBinding
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.NotificationsFragment
import me.markosullivan.swiperevealactionbuttons.SwipeRevealLayout
import org.slf4j.LoggerFactory


class NotificationAdapter(val notifications: MutableList<Notification>, val homeNotificationsFragmentViewModel: HomeNotificationsFragmentViewModel, val fragment: NotificationsFragment): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val logger= LoggerFactory.getLogger(NotificationAdapter::class.java)

    private lateinit var recyclerView: RecyclerView

    class NotificationViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            if (binding is ViewHolderNotificationItemBinding) {
                binding.notification= notification
                binding.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        if (viewType== 0) {
            return NotificationViewHolder(ViewHolderNotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            return NotificationViewHolder(LoadingItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

    }



    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        if (holder.itemViewType== 0) {
            holder.bind(notifications[position])
            val bindingHolder= (holder.binding as ViewHolderNotificationItemBinding)
            val itemTop= bindingHolder.notificationItemTop
            val itemBottom= bindingHolder.notificationItemBottom
            val revealLayout= holder.binding.root as SwipeRevealLayout
            val animationOk= holder.binding.animationOk
            bindingHolder.delete.visibility= View.GONE
            //revealLayout.dragLock(true)
            if (homeNotificationsFragmentViewModel.isInfoNotification(notifications[position]) && notifications[position].state== NotifState.PENDING) {
                revealLayout.dragLock(true)
                itemTop.setOnClickListener({v ->

                    revealLayout.dragLock(true)
                    homeNotificationsFragmentViewModel.updateNotificationStateRead(notifications[position])
                    bindingHolder.delete.visibility= View.GONE
                    revealLayout.open(true)
                    itemBottom.visibility= View.VISIBLE
                    bindingHolder.delete.visibility= View.GONE
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

                })/* else {
                        revealLayout.dragLock(false)
                        bindingHolder.delete.visibility= View.VISIBLE
                        bindingHolder.delete.setOnClickListener({v ->
                            logger.debug("delete clicked.....")
                        })
                        animationOk.visibility= View.GONE
                    }*/
                } else if (homeNotificationsFragmentViewModel.isInfoNotification(notifications[position])) {
                revealLayout.dragLock(false)
                itemTop.setOnClickListener { null }
                bindingHolder.delete.visibility= View.VISIBLE
                bindingHolder.delete.setOnClickListener({v ->
                    logger.debug("delete clicked.....")
                })
                animationOk.visibility= View.GONE
            }



        } else {
            holder.itemView.isClickable= false
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (notifications[position].id.isNotBlank()) {
            return 0
        } else {
            return 1
        }
    }

    override fun getItemCount()= notifications.size

    fun setData(data: List<Notification> ) {
        notifications.clear()
        notifications.addAll(data)
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView= recyclerView
    }
}