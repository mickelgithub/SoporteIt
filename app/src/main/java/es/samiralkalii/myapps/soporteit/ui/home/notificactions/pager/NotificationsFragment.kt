package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.EpoxyTouchHelper
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.NotificationItemBindingModel_
import es.samiralkalii.myapps.soporteit.databinding.FragmentNotificationsBinding
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragment
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter.NotificationsController
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
    private val notifsSentController= NotificationsController()
    private val notifsReceivedController= NotificationsController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("onCreate...."+ this.hashCode())
        notificationCategory= arguments?.getSerializable(NOTIFICATION_CATEGORY_KEY) as NotificationCategory ?: NotificationCategory.RECEIVED

        parentViewModel.receivedNotifications.observe(this, Observer {
            notifsReceivedController.setData(it)
        })

        parentViewModel.sentNotifications.observe(this, Observer {
            notifsSentController.setData(it)
        })

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

        if (notificationCategory== NotificationCategory.RECEIVED) {
            binding.notifsRecyclerView.adapter= notifsReceivedController.adapter
            parentViewModel.getReceivedNotifications()
        } else {
            binding.notifsRecyclerView.adapter= notifsSentController.adapter
            parentViewModel.getSentNotifications()
        }
        binding.notifsRecyclerView.addItemDecoration(DividerItemDecoration(activity!!, linearLayout.orientation))
        EpoxyTouchHelper.initSwiping(binding.notifsRecyclerView)
            .leftAndRight()
            .withTarget(NotificationItemBindingModel_::class.java)
            .andCallbacks(object: EpoxyTouchHelper.SwipeCallbacks<NotificationItemBindingModel_>() {
                override fun onSwipeCompleted(
                    model: NotificationItemBindingModel_?,
                    itemView: View?,
                    position: Int,
                    direction: Int
                ) {
                    if (notificationCategory== NotificationCategory.RECEIVED) {
                        notifsReceivedController.

                    } else {
                        notifsSentController.setData(notifsSentController.currentData)
                        notifsReceivedController.requestModelBuild()
                    }
                }

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