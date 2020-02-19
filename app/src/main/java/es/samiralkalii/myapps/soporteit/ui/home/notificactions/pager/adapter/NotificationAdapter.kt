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

        fun bind(notifViewModel: NotificationViewModelTemplate) {
            when (binding) {
                is NotificationItemInfoBinding -> {
                    binding.item= notifViewModel as NotificationViewModelTemplate.NotificationViewModelInfo
                    notifViewModel.viewHolder= this
                    notifViewModel.init()
                    binding.executePendingBindings()
                }
                is NotificationItemReplyBinding -> {
                    binding.item= notifViewModel as NotificationViewModelTemplate.NotificationViewModelReply
                    notifViewModel.viewHolder= this
                    notifViewModel.init()
                    binding.executePendingBindings()
                }
                else -> Unit
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder= when (viewType) {
            R.layout.notification_item_info -> NotificationViewHolder(NotificationItemInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            R.layout.notification_item_reply -> NotificationViewHolder(NotificationItemReplyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else ->  NotificationViewHolder(LoadingItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NotificationAdapter.NotificationViewHolder, position: Int) {
        if (holder.itemViewType== R.layout.notification_item_info) {
            holder.bind(notifications[position] as NotificationViewModelTemplate.NotificationViewModelInfo)
        } else if (holder.itemViewType== R.layout.notification_item_reply) {
            holder.bind(notifications[position] as NotificationViewModelTemplate.NotificationViewModelReply)
        } else {
            holder.itemView.isClickable= false
        }
    }

    override fun getItemViewType(position: Int): Int {
        when (notifications[position]) {
            NotificationViewModelTemplate.NotificationViewModelLoading ->
                return R.layout.loading_item_view
            is NotificationViewModelTemplate.NotificationViewModelInfo ->
                return R.layout.notification_item_info
            is NotificationViewModelTemplate.NotificationViewModelReply ->
                return R.layout.notification_item_reply
        }
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