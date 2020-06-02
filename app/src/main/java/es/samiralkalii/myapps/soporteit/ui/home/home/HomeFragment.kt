package es.samiralkalii.myapps.soporteit.ui.home.home

import android.app.SearchManager
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
import es.samiralkalii.myapps.domain.teammanagement.Group
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_ID
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_MAP_ID
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_NAME
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.FragmentHomeBinding
import es.samiralkalii.myapps.soporteit.databinding.MemberUserItemBinding
import es.samiralkalii.myapps.soporteit.ui.BaseFragment
import es.samiralkalii.myapps.soporteit.ui.dialog.LoadingDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserAdapter
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate
import es.samiralkalii.myapps.soporteit.ui.home.home.newgroup.NewGroupDialog
import es.samiralkalii.myapps.soporteit.ui.util.*
import es.samiralkalii.myapps.soporteit.ui.util.Constants.Companion.OPERATION_NEW
import es.samiralkalii.myapps.soporteit.ui.util.Constants.Companion.OPERATION_UPDATE
import es.samiralkalii.myapps.soporteit.ui.util.animators.animateRevealView
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory


private const val DISTANCE_TO_TRIGGER_SWIP= 250F
private const val DELAY_400= 400L

class HomeFragment: BaseFragment(), SearchView.OnQueryTextListener {

    private val logger= LoggerFactory.getLogger(HomeFragment::class.java)

    override val viewModel: HomeFragmentViewModel by viewModel()
    /*private val homeViewModel: HomeViewModel by lazy {
        ViewModelProvider(activity!!)[HomeViewModel::class.java]
    }*/
    private lateinit var binding: FragmentHomeBinding
    private val args: HomeFragmentArgs by navArgs()
    private val navController by lazy { findNavController() }

    override fun initUI(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (args.isEmailValidated) {
            binding= FragmentHomeBinding.inflate(inflater, container, false).apply {
                uiModel= this@HomeFragment.viewModel.uiModel
                lifecycleOwner= viewLifecycleOwner
                fragment= this@HomeFragment
                swipeContainer.setDistanceToTriggerSync(requireActivity().convertDpToPixels(DISTANCE_TO_TRIGGER_SWIP).toInt())
                groupsRecycleView.apply {
                    setHasFixedSize(true)
                    adapter =
                        MemberUserAdapter(mutableListOf(), this@HomeFragment)
                }
                swipeContainer.setOnRefreshListener {
                    this@HomeFragment.viewModel.initData(true)
                }
                executePendingBindings()
            }
            setHasOptionsMenu(true)
            return binding.root
        }
        return null
    }

    override fun initStateObservation() {

        viewModel.uiModel.getGroupsActionState.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { screenState ->
                if (screenState is ScreenState.Render) {
                    processGetGroupActionState(screenState)
                }
            }
        })

        viewModel.uiModel.progressVisible.observe(viewLifecycleOwner, Observer {
            LoadingDialog.processDialog(it, requireActivity().supportFragmentManager)
        })

        viewModel.uiModel.items.observe(viewLifecycleOwner, Observer {
            (binding.groupsRecycleView.adapter as MemberUserAdapter).setData(it)
        })

        viewModel.uiModel.refreshingState.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.getContentIfNotHandled()?.let { refresh ->
                    if (refresh) {
                        binding.swipeContainer.isRefreshing= false
                    }
                }
            }
        })

        viewModel.uiModel.user.observe(viewLifecycleOwner, Observer {
            it?.let {
                requireActivity().invalidateOptionsMenu()
            }
        })

        viewModel.uiModel.searchViewVisibility.observe(viewLifecycleOwner, Observer {
            requireActivity().invalidateOptionsMenu()
        })

        viewModel.uiModel.updateGroup.observe(viewLifecycleOwner, Observer {
            it?.let {
                it.getContentIfNotHandled()?.let { group ->
                    showNewGroupForUpdate(group)
                }
            }
        })

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

    private fun initSearchView(menu: Menu) {
        if (viewModel.uiModel.searchViewVisibility.value!!) {
            val searchManager = getSystemService(requireActivity(), SearchManager::class.java)
            val searchMenuItem = menu.findItem(R.id.search)
            searchMenuItem.isVisible= true
            val searchView = searchMenuItem.actionView as SearchView
            searchView.setSearchableInfo(searchManager!!.getSearchableInfo(requireActivity().componentName))
            searchView.isSubmitButtonEnabled= false
            searchView.setOnQueryTextListener(this)
        } else {
            menu.findItem(R.id.search).isVisible= false
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (menu.size()== 0) {
            requireActivity().menuInflater.inflate(R.menu.menu_home_fragment, menu)
            viewModel.uiModel.user.value?.let {
                if (!it.isBoss) {
                    menu.findItem(R.id.new_group).isVisible = false
                }
            }
        }
        initSearchView(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        viewModel.uiModel.user.value?.let {
            if (it.isEmailVerified) {
                inflater.inflate(R.menu.menu_home_fragment, menu)
                if (!it.isBoss) {
                    menu.findItem(R.id.new_group).isVisible= false
                }
                initSearchView(menu)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem)= when (item.itemId) {
        R.id.new_group -> {
            viewModel.uiModel.user.value?.let { user ->
                showNewGroupDialog(user.id, user.areaId, user.departmentId, viewModel.myGroups.groups)
                }
            true
        }
        else -> {
            item.onNavDestinationSelected(navController)
        }
    }

    fun updateModelUserConfirmed(user: String, isConfirmed: Boolean, internal: Boolean) {
        val adapter= binding.groupsRecycleView.adapter as MemberUserAdapter
        val memberUserViewModel= adapter.members.filter {
            it is MemberUserViewModelTemplate.MemberUserViewModel && it.user.id== user
        }.first() as MemberUserViewModelTemplate.MemberUserViewModel
        (memberUserViewModel.viewHolder.binding as MemberUserItemBinding).memberStateImage.let {
            it.postDelayed({
                it.animateRevealView{it.visibility= View.GONE}
                if (isConfirmed) {
                    val newItem= MemberUserViewModelTemplate.MemberUserViewModel(memberUserViewModel.user.copy(membershipConfirmation = "S"), viewModel.uiModel.user.value!!)
                    val position= adapter.members.indexOf(memberUserViewModel)
                    viewModel.updateItem(position, newItem)
                    adapter.members[position]= newItem
                    adapter.notifyItemChanged(position)
                    /*if (internal) {
                        it.postDelayed({viewModel.init(null)}, DELAY_1000)
                    }*/
                    viewModel.initData(true)
                } else {
                    val position= adapter.members.indexOf(memberUserViewModel)
                    viewModel.removeItem(position)
                    adapter.members.removeAt(position)
                    adapter.notifyItemRemoved(position)
                }
            }, DELAY_400)
        }
    }

    fun onProfileImageClick(user: String) {
        val action= HomeFragmentDirections.actionHomeFragmentToProfileFragment(user)
        navController.navigate(action)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let {
            viewModel.filterGroups(it)
        }
        return true
    }

    private fun showNewGroupDialog(boss: String, area: String, department: String, groups: List<Group>) {
        var newGroupDialog: NewGroupDialog?= requireActivity().supportFragmentManager.findFragmentByTag(
            NewGroupDialog::class.java.simpleName) as NewGroupDialog?
        if (newGroupDialog== null) {
            val bundle= bundleOf(
                KEY_OPERATION to OPERATION_NEW,
                KEY_ID to boss,
                KEY_AREA_ID to area,
                KEY_DEPARTMENT_ID to department,
                KEY_GROUPS to groups.map { it.name.toUpperCase() }.reduce {
                        element1, element2 -> element1+ char_separator+ element2
                })
            newGroupDialog= NewGroupDialog.newInstance(bundle)
            newGroupDialog.show(requireActivity().supportFragmentManager, NewGroupDialog::class.java.simpleName)
        }
    }

    fun showNewGroupForUpdate(group: Group) {

        viewModel.uiModel.user.value?.let { user ->
            var newGroupDialog: NewGroupDialog?= requireActivity().supportFragmentManager.findFragmentByTag(
                NewGroupDialog::class.java.simpleName) as NewGroupDialog?
            if (newGroupDialog== null) {
                val bundle= bundleOf(
                    KEY_OPERATION to OPERATION_UPDATE,
                    KEY_ID to user.id,
                    KEY_AREA_ID to user.areaId,
                    KEY_DEPARTMENT_ID to user.departmentId,
                    KEY_GROUPS to viewModel.myGroups.groups.map { it.name.toUpperCase() }.reduce {
                            element1, element2 -> element1+ char_separator+ element2
                    },
                    KEY_GROUP_NAME to group.name,
                    KEY_GROUP_MAP_ID to group.id,
                    KEY_GROUP_USERS to group.members.map{ it.id }.reduce{
                        element1, element2 -> element1+ char_separator+ element2
                })
                newGroupDialog= NewGroupDialog.newInstance(bundle)
                newGroupDialog.show(requireActivity().supportFragmentManager, NewGroupDialog::class.java.simpleName)
            }
        }
    }

}