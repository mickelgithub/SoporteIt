package es.samiralkalii.myapps.soporteit.ui.home.home.newgroup

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_ID
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_MAP_ID
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_NAME
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.DialogNewGroupBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import es.samiralkalii.myapps.soporteit.ui.util.*
import es.samiralkalii.myapps.soporteit.ui.util.Constants.Companion.OPERATION_UPDATE
import org.koin.android.viewmodel.ext.android.viewModel


class NewGroupDialog: MyDialog() {

    private lateinit var binding: DialogNewGroupBinding

    val viewModel: NewGroupDialogViewModel by viewModel()

    private lateinit var itemTouchHelper: ItemTouchHelper

    companion object {

        fun newInstance(bundle: Bundle)= NewGroupDialog()
            .apply {
            this.arguments= bundle
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        itemTouchHelper= ItemTouchHelper(SwipeToDeleteCallback())
        AndroidUtility.hideKeyboard(requireActivity())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args= requireArguments()
        val operation= args.getString(KEY_OPERATION, "")
        val boss = args.getString(KEY_ID, "")
        val area= args.getString(KEY_AREA_ID, "")
        val department =  args.getString(KEY_DEPARTMENT_ID, "")
        val groupUsers= args.getString(KEY_GROUP_USERS, "").split(char_separator)
        val groupName= args.getString(KEY_GROUP_NAME, "")
        val groupId= args.getString(KEY_GROUP_MAP_ID, "")
        val groups: List<String>
        if (operation== OPERATION_UPDATE) {
            groups= args.getString(KEY_GROUPS, "").split(char_separator).filter { !it.equals(groupName, true) }
        } else {
            groups= args.getString(KEY_GROUPS, "").split(char_separator)
        }
        viewModel.publishInitInfo(boss, area, department, groups, operation, groupUsers, groupName, groupId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding= DialogNewGroupBinding.inflate(inflater, container, false).apply {
            lifecycleOwner= viewLifecycleOwner
            uiModel= viewModel.uiModel
            interactor= viewModel
            recyclerView.apply {
                setHasFixedSize(true)
                adapter = NewGroupMembersAdapter(mutableListOf())
                ViewCompat.setNestedScrollingEnabled(this, true)
            }
            executePendingBindings()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiModel.dismissDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    val homeFragment= getCallingFragment()
                    if (homeFragment!= null) {
                        binding.root.postDelayed({
                            dismiss()
                            homeFragment.viewModel.initData()}, 3000)
                    }
                }
            }
        })
        viewModel.uiModel.dialogCancelable.observe(viewLifecycleOwner, Observer{
            isCancelable= it
        })
        viewModel.uiModel.itemsLiveData.observe(viewLifecycleOwner, Observer {
            (binding.recyclerView.adapter as NewGroupMembersAdapter).setData(it)
            if (it.isNotEmpty() && it.fold(true) {
                        condition, element -> condition && element is MemberUserNewGroupTemplate.MemberUserNewGroupViewModel
                }) {
                itemTouchHelper.attachToRecyclerView(binding.recyclerView)
            } else {
                itemTouchHelper.attachToRecyclerView(null)
            }
        })
        viewModel.uiModel.error.observe(viewLifecycleOwner, Observer {
            if (it!= 0) {
                (binding.recyclerView.adapter as NewGroupMembersAdapter).setData(listOf(
                    MemberUserNewGroupTemplate.MemberUserNewGroupViewModelError(requireActivity().resources.getString(it))
                ))
            }
        })
        viewModel.uiModel.disable_recyclerview_swipping.observe(viewLifecycleOwner, Observer {
            itemTouchHelper.attachToRecyclerView(null)
        })
    }

    private fun getCallingFragment(): HomeFragment {
        val navHost = requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let {fragment->
                return fragment as HomeFragment
            }
        }
        throw java.lang.IllegalStateException("Algun error de estado")
    }

    private inner class SwipeToDeleteCallback: ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

        private val icon= ContextCompat.getDrawable(requireActivity(),
            R.drawable.delete)!!
        private val background= ColorDrawable(ContextCompat.getColor(requireActivity(), R.color.light_gray))

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position= viewHolder.adapterPosition
            viewModel.deleteItemAt(position)
            (this@NewGroupDialog.binding.recyclerView.adapter as NewGroupMembersAdapter).apply {
                members.removeAt(position)
                notifyItemRemoved(position)
            }
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            val itemView = viewHolder.itemView
            val backgroundCornerOffset =
                20 //so background is behind the rounded corners of itemView
            val iconMargin = (itemView.height - icon.intrinsicHeight) / 2
            val iconTop =
                itemView.top + (itemView.height - icon.intrinsicHeight) / 2
            val iconBottom = iconTop + icon.intrinsicHeight
            if (dX > 0) { // Swiping to the right
                val iconLeft = itemView.left + iconMargin
                val iconRight = itemView.left + iconMargin + icon.intrinsicWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset, itemView.bottom
                )
            } else if (dX < 0) { // Swiping to the left
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
            } else { // view is unSwiped
                background.setBounds(0, 0, 0, 0)
            }
            background.draw(c)
            icon.draw(c)
        }

    }

}

