package es.samiralkalii.myapps.soporteit.ui.home.schedulers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentSchedulersBinding
import es.samiralkalii.myapps.soporteit.ui.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class SchedulersFragment: BaseFragment() {

    private val logger= LoggerFactory.getLogger(SchedulersFragment::class.java)

    override val viewModel: SchedulersFragmentViewModel by viewModel()
    private lateinit var binding: FragmentSchedulersBinding


    override fun initUI(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logger.debug("OnCreateView")
        binding= FragmentSchedulersBinding.inflate(inflater, container, false)
        //binding.viewModel= viewModel
        binding.lifecycleOwner= viewLifecycleOwner
        //binding.fragment= this
        binding.executePendingBindings()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun initStateObservation() {
    }
}