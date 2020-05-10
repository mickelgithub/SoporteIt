package es.samiralkalii.myapps.soporteit.ui.home.absences

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentAbsencesBinding
import es.samiralkalii.myapps.soporteit.ui.BaseFragment
import org.slf4j.LoggerFactory

class AbsencesFragment: BaseFragment() {

    private val logger = LoggerFactory.getLogger(AbsencesFragment::class.java)

    private lateinit var binding: FragmentAbsencesBinding
    override fun initUI(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        logger.debug("OnCreateView")
        binding= FragmentAbsencesBinding.inflate(inflater, container, false)
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