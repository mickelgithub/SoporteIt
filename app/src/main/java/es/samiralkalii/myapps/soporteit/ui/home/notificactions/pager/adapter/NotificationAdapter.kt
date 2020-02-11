package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.databinding.LoadingItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.ViewHolderNotificationItemBinding
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import me.markosullivan.swiperevealactionbuttons.SwipeRevealLayout


class NotificationAdapter(val notifications: MutableList<Notification>, val homeNotificationsFragmentViewModel: HomeNotificationsFragmentViewModel): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

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
            val itemTop= (holder.binding as ViewHolderNotificationItemBinding).notificationItemTop
            val itemButtom= (holder.binding as ViewHolderNotificationItemBinding).notificationItemBottom
            val revealLayout= holder.binding.root as SwipeRevealLayout
            val animationOk= holder.binding.animationOk
            revealLayout.dragLock(true)
            itemTop.setOnClickListener({v ->
                homeNotificationsFragmentViewModel.updateNotificationStateRead(notifications[position])
                revealLayout.open(true)
                itemButtom.visibility= View.VISIBLE
                animationOk.playAnimation()
                revealLayout.postDelayed({
                    revealLayout.close(true)
                    itemButtom.visibility= View.INVISIBLE
                    notifications[position].state= NotifState.READ
                }, 1100)
                revealLayout.postDelayed({
                    holder.binding.invalidateAll()
                    recyclerView.invalidateItemDecorations()
                }, 1300)
            })

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