package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.databinding.ViewHolderNotificationItemBinding


class NotificationAdapter(val notifications: MutableList<Notification>): RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(val binding: ViewHolderNotificationItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.notification= notification
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(ViewHolderNotificationItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount()= notifications.size

    fun setData(data: List<Notification> ) {
        notifications.clear()
        notifications.addAll(data)
        notifyDataSetChanged()
    }
}