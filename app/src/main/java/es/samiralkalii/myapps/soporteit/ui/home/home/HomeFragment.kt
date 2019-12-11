package es.samiralkalii.myapps.soporteit.ui.home.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.FRAGMENT_TAG
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel
import es.samiralkalii.myapps.soporteit.ui.home.isBoss
import es.samiralkalii.myapps.soporteit.ui.home.isProfilePending
import es.samiralkalii.myapps.soporteit.ui.home.teammanagment.TeamManagementChangeState
import es.samiralkalii.myapps.soporteit.ui.home.teammanagment.dialog.AlertDialogForMemberInvitation
import es.samiralkalii.myapps.soporteit.ui.splash.SplashActivity
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.toUser
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class HomeFragment: Fragment(), AlertDialogForMemberInvitation.OnMemberSelectionListener {

    companion object {
        fun newInstance(bundle: Bundle) = HomeFragment().apply { arguments= bundle }
    }

    private val logger= LoggerFactory.getLogger(HomeFragment::class.java)

    private val viewModel: HomeFragmentViewModel by viewModel()
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProviders.of(activity!!)[HomeViewModel::class.java]
    }

    private lateinit var user: User

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

        viewModel.allUsers.observe(this, Observer {
            updateUsers(it)
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


    override fun onMemeberSelected(user: String) {

    }

    override fun updateUsers(users: List<User>) {
        val invitationFragment= activity!!.supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (invitationFragment is AlertDialogForMemberInvitation) {
            invitationFragment.updateUsers(users)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (viewModel.user.isBoss()) {
            //menu.findItem(R.id.menu_item_create_team).setVisible(false)
            inflater.inflate(R.menu.menu_home_fragment, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_create_team -> {
                //homeViewModel.updateGoto(SplashActivity.Companion.GOTO.TEAM_MANAGEMENT)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }






}