package es.samiralkalii.myapps.soporteit.ui.home.notificactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentHomeNotificationsBinding
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.NOTIFICATION_CATEGORY_KEY
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.NotificationCategory
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.NotificationsFragment
import es.samiralkalii.myapps.soporteit.ui.util.toBundle
import es.samiralkalii.myapps.soporteit.ui.util.toUser
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class HomeNotificationsFragment: Fragment() {

    private val logger= LoggerFactory.getLogger(HomeNotificationsFragment::class.java)

    private lateinit var binding: FragmentHomeNotificationsBinding

    private lateinit var user: User

    private val viewModel: HomeNotificationsFragmentViewModel by viewModel()
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(requireActivity())[HomeViewModel::class.java]
    }

    companion object {
        fun newInstance(bundle: Bundle) = HomeNotificationsFragment().apply { arguments= bundle }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("OnCreate")
        user= (arguments as Bundle).toUser()
        viewModel.publishUser(user)
        setHasOptionsMenu(true)

        viewModel.teamInvitationReply.observe(this, Observer {
            homeViewModel.updateUser(user, it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logger.debug("OnCreateView")
        binding= FragmentHomeNotificationsBinding.inflate(inflater, container, false)
        binding.viewModel= viewModel
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.notificationsPager.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                return true
            }
        })
        binding.notificationsPager.adapter= NotificationsPageAdapter(this, user)


    }

    class NotificationsPageAdapter(val fragment: Fragment, val user: User): FragmentPagerAdapter(fragment.childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int)= if (position== 0) NotificationsFragment.newInstance(user.toBundle().apply { putSerializable(NOTIFICATION_CATEGORY_KEY, NotificationCategory.RECEIVED) }) else
            NotificationsFragment.newInstance(Bundle().apply {
                putSerializable(NOTIFICATION_CATEGORY_KEY, NotificationCategory.SENT)
            })

        //tendremos 2 pestañas, enviados y recibidos
        override fun getCount()= 2

        override fun getPageTitle(position: Int)= if (position== 0) fragment.requireActivity().resources.getString(R.string.notif_received) else fragment.requireActivity().resources.getString(R.string.notif_sent)
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("onDestroy.....")

    }
}