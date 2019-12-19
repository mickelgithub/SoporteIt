package es.samiralkalii.myapps.soporteit.ui.home.home

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentHomeBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.FRAGMENT_TAG
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel
import es.samiralkalii.myapps.soporteit.ui.home.home.dialog.CreateTeamDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.dialog.InviteMemberDialog
import es.samiralkalii.myapps.soporteit.ui.home.isBoss
import es.samiralkalii.myapps.soporteit.ui.home.teammanagment.dialog.AlertDialogForMemberInvitation
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.toUser
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class HomeFragment: Fragment(),
    AlertDialogForMemberInvitation.OnMemberSelectionListener,
    CreateTeamDialog.OnCreateTeamListener, InviteMemberDialog.OnInviteMemberListener {

    companion object {
        fun newInstance(bundle: Bundle) = HomeFragment().apply { arguments= bundle }
    }

    private val logger= LoggerFactory.getLogger(HomeFragment::class.java)

    private val viewModel: HomeFragmentViewModel by viewModel()
    private val homeViewModel: HomeViewModel by lazy {
        ViewModelProviders.of(activity!!)[HomeViewModel::class.java]
    }

    private lateinit var user: User
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("OnCreate")
        user= (arguments as Bundle).toUser()
        viewModel.publishUser(user)
        setHasOptionsMenu(true)

        //create Tream
        viewModel.dialogCreateTeamState.observe(this, Observer {
            when (it) {
                MyDialog.DialogState.ShowDialog    -> CreateTeamDialog.showDialog(activity!!.supportFragmentManager)
                MyDialog.DialogState.ShowLoading   -> CreateTeamDialog.showLoading()
                MyDialog.DialogState.ShowSuccess   -> CreateTeamDialog.showSuccess()
                is MyDialog.DialogState.ShowMessage -> CreateTeamDialog.showMessage(it.message)
            }
        })


        viewModel.teamAddedOk.observe(this, Observer {
            it.getContentIfNotHandled().let { screenState ->
                if (screenState is ScreenState.Render) {
                    processTeamAdded(screenState)
                }
            }
        })



        //end create Team

        viewModel.allUsers.observe(this, Observer { event ->
            event.getContentIfNotHandled().let {
                if (it!= null) {
                    updateUsers(it)
                }
            }
        })
    }

    private fun processTeamAdded(screenState: ScreenState.Render<HomeFragmentChangeState>) {
        when (screenState.renderState) {
            HomeFragmentChangeState.teamAddedOk -> {
                homeViewModel.updateTeamCreated(viewModel.user)
                viewModel.updateDialogCreateState(MyDialog.DialogState.ShowSuccess)
                Handler().postDelayed({
                    (activity as AppCompatActivity).supportActionBar?.title= resources.getString(R.string.team_created_title, viewModel.user.team)
                    (activity as AppCompatActivity).invalidateOptionsMenu()
                }, MyDialog.DIALOG_DISMISS_DELAY)
            }
            is HomeFragmentChangeState.ShowMessage -> {
                viewModel.updateDialogCreateState(MyDialog.DialogState.ShowMessage(screenState.renderState.message))
            }
        }
    }

    //create team
    override fun onCreateTeam(team: String) {
        logger.debug("Vamos a crear el team $team")
        viewModel.onTeamCreateClick(team)
    }
    //end create team

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logger.debug("OnCreateView")
        binding= FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel= viewModel
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        return binding.root
    }




    override fun onMemeberSelected(user: String) {

    }

    override fun LoadUsers(users: List<User>) {

    }

    override fun updateUsers(users: List<User>) {

        InviteMemberDialog.loadUsers(users)
        InviteMemberDialog.showDialog()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (viewModel.user.isBoss()) {
            inflater.inflate(R.menu.menu_home_fragment, menu)
            if (viewModel.user.teamCreated) {
                menu.findItem(R.id.menu_item_create_team).setVisible(false)
            } else {
                menu.findItem(R.id.menu_item_create_group).setVisible(false)
                menu.findItem(R.id.menu_item_invite).setVisible(false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_create_team -> {
                logger.debug("opcion ${item.title} clicked")
                viewModel.updateDialogCreateState(MyDialog.DialogState.ShowDialog)
                true
            }
            R.id.menu_item_invite -> {
                InviteMemberDialog.showDialogLoadingData(activity!!.supportFragmentManager)
                viewModel.loadAllUsers()
                true
            }
            R.id.menu_item_create_group -> {
                logger.debug("opcion ${item.title} clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun dismissInviteMemberDialog() {
        InviteMemberDialog.dismissMe()
    }

}