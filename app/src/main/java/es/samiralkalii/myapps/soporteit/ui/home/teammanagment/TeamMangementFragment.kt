package es.samiralkalii.myapps.soporteit.ui.home.teammanagment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.databinding.FragmentTeamManagementBinding
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel
import es.samiralkalii.myapps.soporteit.ui.util.toUser
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class TeamMangementFragment: Fragment() {

    companion object {
        fun newInstance(bundle: Bundle) = TeamMangementFragment().apply { arguments= bundle }
    }

    private val logger= LoggerFactory.getLogger(TeamMangementFragment::class.java)

    private val viewModel: TeamMangementViewModel by viewModel()
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProviders.of(activity!!)[HomeViewModel::class.java]
    }

    private lateinit var user: User

    private lateinit var binding: FragmentTeamManagementBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("OnCreate")
        user= (arguments as Bundle).toUser()
        viewModel.publishUser(user)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logger.debug("OnCreateView")
        binding= FragmentTeamManagementBinding.inflate(inflater, container, false)
        binding.viewModel= viewModel
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        return binding.root
    }


}