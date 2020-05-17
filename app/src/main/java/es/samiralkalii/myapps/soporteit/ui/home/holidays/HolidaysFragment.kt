package es.samiralkalii.myapps.soporteit.ui.home.holidays

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentHolidaysBinding
import es.samiralkalii.myapps.soporteit.databinding.FragmentHomeBinding
import es.samiralkalii.myapps.soporteit.ui.BaseFragment
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class HolidaysFragment: BaseFragment() {

    private val logger= LoggerFactory.getLogger(HolidaysFragment::class.java)

    override val viewModel: HolidaysFragmentViewModel by viewModel()
    private lateinit var binding: FragmentHolidaysBinding

    override fun initUI(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logger.debug("OnCreateView")
        binding= FragmentHolidaysBinding.inflate(inflater, container, false)
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