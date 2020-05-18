package es.samiralkalii.myapps.soporteit.ui.home.home

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.onNavDestinationSelected
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

private const val DISTANCE_TO_TRIGGER_SWIP= 250F
private const val DELAY_1000= 1000L
private const val DELAY_400= 400L

class HomeFragment: BaseFragment() {

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
            logger.debug("initUI******")
            binding= FragmentHomeBinding.inflate(inflater, container, false).apply {
                viewModel= this@HomeFragment.viewModel
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
        } else {
            logger.debug("****el valor de email validado es ${args.isEmailValidated} y por tanto no hacemos nada en initUI")
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
                it.getContentIfNotHandled()?.let {
                    if (it) {
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

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (menu.size()== 0) {
            requireActivity().menuInflater.inflate(R.menu.menu_home, menu)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        viewModel.uiModel.user.value?.let {
            if (it.isEmailVerified) {
                inflater.inflate(R.menu.menu_home, menu)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
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
                    if (internal) {
                        it.postDelayed({viewModel.init(null)}, DELAY_1000)
                    }
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

}