package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.databinding.LoadingItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.ViewHolderNotificationItemInfoBinding
import es.samiralkalii.myapps.soporteit.ui.common.adapter.RecyclerBaseAdapter
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.NotificationsFragment
import me.markosullivan.swiperevealactionbuttons.SwipeRevealLayout
import org.slf4j.LoggerFactory


class NotificationAdapter(val notifications: MutableList<Notification>, val homeNotificationsFragmentViewModel: HomeNotificationsFragmentViewModel, val fragment: NotificationsFragment): RecyclerBaseAdapter() {

    private val logger= LoggerFactory.getLogger(NotificationAdapter::class.java)

    private lateinit var recyclerView: RecyclerView

    class NotificationViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            if (binding is ViewHolderNotificationItemInfoBinding) {
                binding.notification= notification
                binding.executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        if (viewType== 0) {
            return NotificationViewHolder(ViewHolderNotificationItemInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply { lifecycleOwner= fragment.viewLifecycleOwner })
        } else {
            return NotificationViewHolder(LoadingItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
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
    }

    override fun getItemViewType(position: Int): Int {
        if (notifications[position].id.isNotBlank()) {
            return 0
        } else {
            return 1
        }
    }

    override fun getLayoutIdForPosition(position: Int): Int {
        return
    }

    override fun getViewModel(position: Int): Any? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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