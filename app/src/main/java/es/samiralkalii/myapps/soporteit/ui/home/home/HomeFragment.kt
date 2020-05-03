package es.samiralkalii.myapps.soporteit.ui.home.home

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentHomeBinding
import es.samiralkalii.myapps.soporteit.databinding.MemberUserItemBinding
import es.samiralkalii.myapps.soporteit.ui.BaseFragment
import es.samiralkalii.myapps.soporteit.ui.dialog.LoadingDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserAdapter
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.animators.animateRevealView
import es.samiralkalii.myapps.soporteit.ui.util.convertDpToPixels
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
        logger.debug("initUI........")
        binding= FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel= viewModel
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        setHasOptionsMenu(true)
        (activity!! as AppCompatActivity).supportActionBar?.title= resources.getString(R.string.my_team)
        return binding.root
    }

    override fun initStateObservation() {

        binding.swipeContainer.setOnRefreshListener {
            viewModel.init(true)
        }

        binding.swipeContainer.setDistanceToTriggerSync(activity!!.convertDpToPixels(250F).toInt())

        binding.groupsRecycleView.apply {
            setHasFixedSize(true)
            adapter =
                MemberUserAdapter(mutableListOf<MemberUserViewModelTemplate>(), viewModel)
        }


        /*val resId = R.anim.layout_animation_fall_down
        val animation: LayoutAnimationController = AnimationUtils.loadLayoutAnimation(context, resId)
        binding.groupsRecycleView.setLayoutAnimation(animation)*/

        /*binding.groupsRecycleView.addItemDecoration(
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
            })*/

        viewModel.getGroupsActionState.observe(this, Observer {
            it.getContentIfNotHandled()?.let { screenState ->
                if (screenState is ScreenState.Render) {
                    processGetGroupActionState(screenState)
                }
            }
        })

        viewModel.progressVisible.observe(this, Observer {
            LoadingDialog.processDialog(it, activity!!.supportFragmentManager)
        })


        viewModel.items.observe(this, Observer {
            (binding.groupsRecycleView.adapter as MemberUserAdapter).setData(it)
        })

        viewModel.refreshingState.observe(this, Observer {
            it?.let {
                it.getContentIfNotHandled()?.let {
                    if (it) {
                        binding.swipeContainer.isRefreshing= false
                    }
                }
            }
        })
    }

    private fun processGetGroupActionState(screenState: ScreenState.Render<HomeFragmentStates.GetGroupsState>) {
        when (screenState.renderState) {
            is HomeFragmentStates.GetGroupsState.GetGroupsStateOk -> {
                viewModel.updateItems(screenState.renderState.members)
            }
            is HomeFragmentStates.GetGroupsState.ShowMessage -> {
                val messagedesc= if (screenState.renderState.messageParams.isNotEmpty()) resources.getString(screenState.renderState.message, *screenState.renderState.messageParams.toTypedArray()) else
                    resources.getString(screenState.renderState.message)
                viewModel.updateItems(listOf())
                viewModel.updateDialogState(MyDialog.DialogState.ShowMessageDialog(messagedesc))
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    fun updateModelUserConfirmed(user: String, isConfirmed: Boolean, internal: Boolean) {
        val adapter= binding.groupsRecycleView.adapter as MemberUserAdapter
        val memberUserViewModel= adapter.members.filter {
            it is MemberUserViewModelTemplate.MemberUserViewModel && it.user.id== user
        }.first() as MemberUserViewModelTemplate.MemberUserViewModel
        (memberUserViewModel.viewHolder.binding as MemberUserItemBinding).memberStateImage.let {
            it.postDelayed({
                it.animateRevealView({it.visibility= View.GONE})
                if (isConfirmed) {
                    val newItem= MemberUserViewModelTemplate.MemberUserViewModel(memberUserViewModel.user.copy(membershipConfirmation = "S"), viewModel)
                    val position= adapter.members.indexOf(memberUserViewModel)
                    viewModel.updateItem(position, newItem)
                    adapter.members[position]= newItem
                    adapter.notifyItemChanged(position)
                    if (internal) {
                        it.postDelayed({viewModel.init()}, 1000)
                    }
                } else {
                    val position= adapter.members.indexOf(memberUserViewModel)
                    viewModel.removeItem(position)
                    adapter.members.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
            }, 400)
        }
    }

    override fun onResume() {
        super.onResume()
        logger.debug("onResume...............")
    }

    override fun onStart() {
        super.onStart()
        logger.debug("onStart.................")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.debug("onDestroyView...............")
    }
}