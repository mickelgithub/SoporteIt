package es.samiralkalii.myapps.soporteit.ui.home.teammanagment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.databinding.FragmentTeamManagementBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.AlertDialog
import es.samiralkalii.myapps.soporteit.ui.home.teammanagment.dialog.AlertDialogForMemberInvitation
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.toUser
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class TeamMangementFragment: Fragment(), AlertDialogForMemberInvitation.OnMemberSelectionListener {

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

        viewModel.teamAddedOk.observe(this, Observer {
            it.getContentIfNotHandled().let { screenState ->
                if (screenState is ScreenState.Render) {
                    processTeamAdded(screenState)
                }
            }
        })

    }

    private fun processTeamAdded(screenState: ScreenState.Render<TeamManagementChangeState>) {
        when (screenState.renderState) {
            TeamManagementChangeState.teamAddedOk -> {
                homeViewModel.updateTeamCreated()
                Toast.makeText(activity!!, "Operación realizada con exito", Toast.LENGTH_LONG).show()
            }
            is TeamManagementChangeState.ShowMessage -> {
                Toast.makeText(activity!!, resources.getString(screenState.renderState.message), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onTeamCreateClick() {
        val alertDialog= AlertDialog.newInstanceForInput("Introduzca el nombre del equipo", {teamName -> viewModel.onTeamCreateClick(teamName)})
        val ft = activity!!.supportFragmentManager.beginTransaction()
        val prev =   activity!!.supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        alertDialog.show(ft, "dialog")

    }

    fun onInviteClick() {
        val alertDialog= AlertDialogForMemberInvitation.newInstance(TeamMangementFragment::class.java.simpleName)
        val ft = activity!!.supportFragmentManager.beginTransaction()
        val prev =   activity!!.supportFragmentManager.findFragmentByTag("dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)
        viewModel.loadAllUsers()
        alertDialog.show(ft, "dialog")
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

    override fun onMemeberSelected(user: String) {

    }

    override fun updateUsers(users: List<User>) {

    }


}