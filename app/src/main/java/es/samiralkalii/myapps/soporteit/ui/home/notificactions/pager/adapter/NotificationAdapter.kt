package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.LoadingItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.NotificationItemInfoBinding
import es.samiralkalii.myapps.soporteit.databinding.NotificationItemReplyBinding
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.NotificationsFragment
import org.slf4j.LoggerFactory


class NotificationAdapter(val notifications: MutableList<NotificationViewModelTemplate>, val homeNotificationsFragmentViewModel: HomeNotificationsFragmentViewModel, val fragment: NotificationsFragment): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    private val logger= LoggerFactory.getLogger(NotificationAdapter::class.java)

    private lateinit var recyclerView: RecyclerView

    class NotificationViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(notifViewModel: NotificationViewModelTemplate.NotificationViewModelInfo) {
            notifViewModel.viewHolder= this
            notifViewModel.init()
            when (binding) {
                is NotificationItemInfoBinding -> {
                    binding.item= notifViewModel
                    binding.executePendingBindings()
                }
                is NotificationItemReplyBinding -> {
                    binding.item= notifViewModel
                    binding.executePendingBindings()
                }
                else -> Unit
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        if (viewType== R.layout.notification_item_info) {
            return NotificationViewHolder(NotificationItemInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            return NotificationViewHolder(LoadingItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        if (holder.itemViewType== R.layout.notification_item_info) {
            holder.bind(notifications[position] as NotificationViewModelTemplate.NotificationViewModelInfo)
            /*val bindingHolder= (holder.binding as NotificationItemInfoBinding)
            val itemTop= bindingHolder.notificationItemTop
            val itemBottom= bindingHolder.notificationItemBottom
            val revealLayout= holder.binding.root as SwipeRevealLayout
            revealLayout.close(false)
            val animationOk= holder.binding.animationOk
            bindingHolder.delete.visibility= View.GONE*/
            //revealLayout.dragLock(true)
            /*if (homeNotificationsFragmentViewModel.isInfoNotification(notifications[position]) && notifications[position].state== NotifState.PENDING) {
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
                bindingHolder.ko.visibility= View.VISIBLE
                bindingHolder.ok.visibility= View.VISIBLE
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




            }*/
        } else {
            holder.itemView.isClickable= false
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (notifications[position]== NotificationViewModelTemplate.NotificationViewModelLoading) {
            return R.layout.loading_item_view
        }
        return R.layout.notification_item_info
    }

    override fun getItemCount()= notifications.size

    fun setData(data: List<NotificationViewModelTemplate> ) {
        notifications.clear()
        notifications.addAll(data)
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView= recyclerView
    }

}