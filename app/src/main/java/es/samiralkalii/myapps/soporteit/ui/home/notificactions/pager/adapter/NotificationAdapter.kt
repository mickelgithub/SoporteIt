package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.databinding.LoadingItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.ViewHolderNotificationItemBinding


class NotificationAdapter(val notifications: MutableList<Notification>): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

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
}