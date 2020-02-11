package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.notification.Notification
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentNotificationsBinding
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragment
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter.NotificationAdapter
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("onCreate...."+ this.hashCode())
        notificationCategory= arguments?.getSerializable(NOTIFICATION_CATEGORY_KEY) as NotificationCategory ?: NotificationCategory.RECEIVED

        if (notificationCategory== NotificationCategory.RECEIVED) {
            parentViewModel.receivedNotifications.observe(this, Observer {
                (binding.notifsRecyclerView.adapter as NotificationAdapter).setData(it)
            })
        } else {
            parentViewModel.sentNotifications.observe(this, Observer {
                (binding.notifsRecyclerView.adapter as NotificationAdapter).setData(it)
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
        val linearLayout= LinearLayoutManager(activity)
        binding.notifsRecyclerView.layoutManager= linearLayout

        binding.notifsRecyclerView.adapter= NotificationAdapter(mutableListOf<Notification>(), parentViewModel)
        if (notificationCategory== NotificationCategory.RECEIVED) {
            (binding.notifsRecyclerView.adapter as NotificationAdapter).setData(listOf(Notification(id="")))
            parentViewModel.getReceivedNotifications()
        } else {
            (binding.notifsRecyclerView.adapter as NotificationAdapter).setData(listOf(Notification(id="")))
            parentViewModel.getSentNotifications()
        }
        binding.notifsRecyclerView.addItemDecoration(DividerItemDecoration(activity!!, linearLayout.orientation).apply {
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
}