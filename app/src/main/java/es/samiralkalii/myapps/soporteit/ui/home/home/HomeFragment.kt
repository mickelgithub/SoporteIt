package es.samiralkalii.myapps.soporteit.ui.home.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import org.slf4j.LoggerFactory

class HomeFragment: Fragment() {

    companion object {
        fun newInstance(bundle: Bundle) = HomeFragment().apply { arguments= bundle }
    }

    private val logger= LoggerFactory.getLogger(HomeFragment::class.java)


}