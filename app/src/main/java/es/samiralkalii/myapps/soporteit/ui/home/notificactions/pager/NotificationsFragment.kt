package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.NotifState
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentNotificationsBinding
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragment
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter.NotificationAdapter
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter.NotificationViewModelTemplate
import org.slf4j.LoggerFactory


enum class NotificationCategory {
    SENT, RECEIVED
}

const val NOTIFICATION_CATEGORY_KEY= "notification_category_key"

class NotificationsFragment: Fragment() {

    private val logger= LoggerFactory.getLogger(NotificationsFragment::class.java)

    private val parentViewModel: HomeNotificationsFragmentViewModel by lazy {
        ViewModelProviders.of(activity!!.supportFragmentManager.findFragmentByTag(
            HomeNotificationsFragment::class.java.simpleName)!!)[HomeNotificationsFragmentViewModel::class.java]
    }

    private lateinit var binding: FragmentNotificationsBinding

    private lateinit var notificationCategory: NotificationCategory

    companion object {
        fun newInstance(bundle: Bundle) = NotificationsFragment().apply { arguments= bundle }
    }

    private lateinit var user: User

    private var showDeleteNotificationsItemMenu= false

    private fun isThereReadNotifs(notifs: List<Notification>)= notifs.filter { it.state== NotifState.READ }.size> 0

    fun updateDeletedMenuItemState(notifs: List<Notification>) {
        showDeleteNotificationsItemMenu= isThereReadNotifs(notifs)
        (activity as AppCompatActivity).invalidateOptionsMenu()
        if (notifs!= null && notifs.size>0) {
            showDeleteNotificationsItemMenu= isThereReadNotifs(notifs)
        } else {
            showDeleteNotificationsItemMenu= false
        }
        (activity as AppCompatActivity).invalidateOptionsMenu()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("onCreate...."+ this.hashCode())
        setHasOptionsMenu(true)
        notificationCategory= arguments?.getSerializable(NOTIFICATION_CATEGORY_KEY) as NotificationCategory ?: NotificationCategory.RECEIVED

        if (notificationCategory== NotificationCategory.RECEIVED) {
            parentViewModel.receivedNotifications.observe(this, Observer {
                (binding.notifsRecyclerView.adapter as NotificationAdapter).setData(
                    it.map { NotificationViewModelTemplate.NotificationViewModel(it, binding.notifsRecyclerView.adapter as NotificationAdapter) })
                updateDeletedMenuItemState(it)
            })
        } else {
            parentViewModel.sentNotifications.observe(this, Observer {
                (binding.notifsRecyclerView.adapter as NotificationAdapter).setData(
                    it.map { NotificationViewModelTemplate.NotificationViewModel(it, binding.notifsRecyclerView.adapter as NotificationAdapter) })
                updateDeletedMenuItemState(it)
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logger.debug("onCreateView...."+ this.hashCode())
        binding= FragmentNotificationsBinding.inflate(inflater, container, false)
        binding.viewModel= parentViewModel
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        logger.debug("onPause"+ this.hashCode())
    }

    override fun onStop() {
        super.onStop()
        logger.debug("onStop"+ this.hashCode())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notifsRecyclerView.setHasFixedSize(true)
        binding.notifsRecyclerView.adapter= NotificationAdapter(mutableListOf<NotificationViewModelTemplate>(), parentViewModel, this)
        if (notificationCategory== NotificationCategory.RECEIVED) {
            (binding.notifsRecyclerView.adapter as NotificationAdapter).setData(listOf(NotificationViewModelTemplate.NotificationViewModelLoading))
            parentViewModel.getReceivedNotifications()
        } else {
            (binding.notifsRecyclerView.adapter as NotificationAdapter).setData(listOf(NotificationViewModelTemplate.NotificationViewModelLoading))
            parentViewModel.getSentNotifications()
        }
        binding.notifsRecyclerView.addItemDecoration(DividerItemDecoration(activity!!, LinearLayout.VERTICAL).apply {
            setDrawable(ColorDrawable(ContextCompat.getColor(activity!!, R.color.colorPrimaryDark)))
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.debug("onDestroyView...."+ this.hashCode())
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("onDestroy....."+ this.hashCode())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home_notifications, menu)
        menu.findItem(R.id.menu_item_delete_notifications).setVisible(showDeleteNotificationsItemMenu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_delete_notifications -> {
                logger.debug("opcion ${item.title} clickeded...")

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}