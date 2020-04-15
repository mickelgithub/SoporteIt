package es.samiralkalii.myapps.soporteit.ui.home.home

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentHomeBinding
import es.samiralkalii.myapps.soporteit.ui.BaseFragment
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.HomeViewModel
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserAdapter
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
import es.samiralkalii.myapps.soporteit.ui.home.home.dialog.CreateTeamDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.dialog.InviteMemberDialog
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.NotificationCategory
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter.NotificationAdapter
import es.samiralkalii.myapps.soporteit.ui.home.notificactions.pager.adapter.NotificationViewModelTemplate
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.toBundle
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class HomeFragment: BaseFragment() {

    companion object {
        fun newInstance(bundle: Bundle) = HomeFragment().apply { arguments= bundle }
    }

    private val logger= LoggerFactory.getLogger(HomeFragment::class.java)

    private val viewModel: HomeFragmentViewModel by viewModel()
    /*private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(activity!!)[HomeViewModel::class.java]
    }*/
    private lateinit var binding: FragmentHomeBinding

    override fun initUI(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel= viewModel
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun initStateObservation() {
        viewModel.init()

        //create Tream
        viewModel.progressVisible.observe(this, Observer {
            when (it) {
                /*MyDialog.DialogState.ShowDialog    -> CreateTeamDialog.showDialog(activity!!.supportFragmentManager)
                MyDialog.DialogState.ShowLoading   -> CreateTeamDialog.showLoading()
                MyDialog.DialogState.ShowSuccess   -> CreateTeamDialog.showSuccess()
                is MyDialog.DialogState.ShowMessage -> CreateTeamDialog.showMessage(it.message)*/
            }
        })


        /*viewModel.teamAddedOk.observe(this, Observer {
            it.getContentIfNotHandled().let { screenState ->
                if (screenState is ScreenState.Render) {
                    //processTeamAdded(screenState)
                }
            }
        })*/
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.groupsRecycleView.setHasFixedSize(true)
        binding.groupsRecycleView.adapter =
            MemberUserAdapter(mutableListOf<MemberUserViewModelTemplate>(), viewModel)
        binding.groupsRecycleView.addItemDecoration(
            DividerItemDecoration(
                activity!!,
                LinearLayout.VERTICAL
            ).apply {
                setDrawable(
                    ColorDrawable(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.colorPrimaryDark
                        )
                    )
                )
            })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        /*if (viewModel.user.isBoss) {
            inflater.inflate(R.menu.menu_home_fragment, menu)
            //if (viewModel.user.teamCreated) {
            if (true) {
                menu.findItem(R.id.menu_item_create_team).setVisible(false)
            } else {
                menu.findItem(R.id.menu_item_create_group).setVisible(false)
                menu.findItem(R.id.menu_item_invite).setVisible(false)
            }
        }*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /*return when (item.itemId) {
            R.id.menu_item_create_team -> {
                logger.debug("opcion ${item.title} clicked")
                //viewModel.updateDialogCreateState(MyDialog.DialogState.ShowDialog)
                true
            }
            R.id.menu_item_invite -> {
                showInviteMemberDialog()
                true
            }
            R.id.menu_item_create_group -> {
                logger.debug("opcion ${item.title} clicked")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }*/
        return super.onOptionsItemSelected(item)
    }

    /*private fun showInviteMemberDialog() {
        var inviteDialog: InviteMemberDialog?= activity!!.supportFragmentManager.findFragmentByTag(InviteMemberDialog::class.java.simpleName) as InviteMemberDialog?
        if (inviteDialog== null) {
            //inviteDialog= InviteMemberDialog.newInstance(user.toBundle())
            inviteDialog.show(activity!!.supportFragmentManager, InviteMemberDialog::class.java.simpleName)
        }
    }*/

}