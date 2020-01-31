package es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.databinding.FragmentNotificationsBinding
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.HomeNotificationsFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter.NotificationsController
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

enum class NotificationCategory {
    SENT, RECEIVED
}

const val NOTIFICATION_CATEGORY_KEY= "notification_category_key"

class NotificationsFragment: Fragment() {

    private val logger= LoggerFactory.getLogger(NotificationsFragment::class.java)
    private val parentViewModel: HomeNotificationsFragmentViewModel by viewModel()

    private lateinit var binding: FragmentNotificationsBinding

    private lateinit var notificationCategory: NotificationCategory

    companion object {
        fun newInstance(bundle: Bundle) = NotificationsFragment().apply { arguments= bundle }
    }

    private lateinit var user: User
    private val notifsController= NotificationsController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationCategory= arguments?.getSerializable(NOTIFICATION_CATEGORY_KEY) as NotificationCategory ?: NotificationCategory.RECEIVED


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentNotificationsBinding.inflate(inflater, container, false)
        binding.viewModel= parentViewModel
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.notifsRecyclerView.layoutManager= LinearLayoutManager(activity)
        binding.notifsRecyclerView.adapter= notifsController.adapter

        if (notificationCategory== NotificationCategory.RECEIVED) {
            parentViewModel.getReceivedNotifications()
            parentViewModel.receivedNotifications.observe(this, Observer {
                notifsController.setData(it)
            })
        } else {
            parentViewModel.getSentNotifications()
            parentViewModel.sentNotifications.observe(this, Observer {
                notifsController.setData(it)
            })
        }
    }
}