package es.samiralkalii.myapps.soporteit.ui.home.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.databinding.FragmentProfileBinding
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel
import es.samiralkalii.myapps.soporteit.ui.util.toUser
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class ProfileFragment: Fragment() {

    private val logger= LoggerFactory.getLogger(ProfileFragment::class.java)

    private val viewModel: ProfileViewModel by viewModel()
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProviders.of(activity!!)[HomeViewModel::class.java]
    }

    private lateinit var user: User

    private lateinit var binding: FragmentProfileBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user= (arguments as Bundle).toUser()

        viewModel.publishUser(user)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentProfileBinding.inflate(inflater, container, false)
        binding.viewModel= viewModel
        val rootView= binding.root
        logger.debug("${homeViewModel.hashCode()}...from fragment")
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)






    }

    companion object {
        fun newInstance(bundle: Bundle) = ProfileFragment().apply { arguments= bundle }
    }


}