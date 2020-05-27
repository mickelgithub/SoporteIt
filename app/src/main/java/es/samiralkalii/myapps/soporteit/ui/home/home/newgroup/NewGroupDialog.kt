package es.samiralkalii.myapps.soporteit.ui.home.home.newgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.DialogNewGroupBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import es.samiralkalii.myapps.soporteit.ui.util.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class NewGroupDialog: MyDialog() {

    private val logger = LoggerFactory.getLogger(NewGroupDialog::class.java)

    private lateinit var binding: DialogNewGroupBinding

    val viewModel: NewGroupDialogViewModel by viewModel()

    private val itemTouchHelper= ItemTouchHelper(SwipeToDeleteCallback())

    companion object {

        fun newInstance(bundle: Bundle)= NewGroupDialog()
            .apply {
            this.arguments= bundle
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val hostActivity= requireArguments()
        val boss = hostActivity.getString(KEY_ID, "")
        val area= hostActivity.getString(KEY_AREA_ID, "")
        val department =  hostActivity.getString(KEY_DEPARTMENT_ID, "")
        val groups= hostActivity.getString(KEY_GROUPS, "").split(char_separator)
        viewModel.publishUser(boss, area, department, groups)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding= DialogNewGroupBinding.inflate(inflater, container, false)
        binding.lifecycleOwner= viewLifecycleOwner
        binding.uiModel= viewModel.uiModel
        binding.interactor= viewModel

        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = NewGroupMembersAdapter(mutableListOf())
        }
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.uiModel.dismissDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    val homeFragment= getCallingFragment()
                    if (homeFragment!= null) {
                        //homeFragment.updateModelUserConfirmed(user, viewModel.confirmUser, viewModel.internal.value!!)
                    }
                    dismiss()
                }
            }
        })
        viewModel.uiModel.dialogCancelable.observe(viewLifecycleOwner, Observer{
            isCancelable= it
        })
        viewModel.uiModel.itemsLiveData.observe(viewLifecycleOwner, Observer {
            (binding.recyclerView.adapter as NewGroupMembersAdapter).setData(it)
            if (!it.isEmpty() && it.fold(true) {
                        condition, element -> condition && element is MemberUserNewGroupTemplate.MemberUserNewGroupViewModel
                }) {
                itemTouchHelper.attachToRecyclerView(binding.recyclerView)
            }

        })
        viewModel.uiModel.error.observe(viewLifecycleOwner, Observer {
            if (it!= 0) {
                (binding.recyclerView.adapter as NewGroupMembersAdapter).setData(listOf(
                    MemberUserNewGroupTemplate.MemberUserNewGroupViewModelError(requireActivity().resources.getString(it))
                ))
            }
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

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position= viewHolder.adapterPosition
            (this@NewGroupDialog.binding.recyclerView.adapter as NewGroupMembersAdapter).members.removeAt(position)
            (this@NewGroupDialog.binding.recyclerView.adapter as NewGroupMembersAdapter).notifyItemRemoved(position)
        }

    }

}

