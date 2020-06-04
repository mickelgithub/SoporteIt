package es.samiralkalii.myapps.soporteit.ui.home.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.EmptyItemViewBindingImpl
import es.samiralkalii.myapps.soporteit.databinding.HeaderGroupMembersItemBinding
import es.samiralkalii.myapps.soporteit.databinding.LoadingItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.MemberUserItemBinding
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.util.Constants


class MemberUserAdapter(val members: MutableList<MemberUserViewModelTemplate>, val fragment: HomeFragment): RecyclerView.Adapter<MemberUserAdapter.MemberUserViewHolder>() {

    //private val logger= LoggerFactory.getLogger(MemberUserAdapter::class.java)

    private lateinit var recyclerView: RecyclerView

    private val deleteUserActionModeCallback by lazy {
        DeleteUserActionModeCallback(this, fragment)
    }

    class MemberUserViewHolder(val binding: ViewDataBinding, val fragment: HomeFragment, val deleteUserActionModeCallback: DeleteUserActionModeCallback?): RecyclerView.ViewHolder(binding.root) {

        fun bind(memberUserViewModel: MemberUserViewModelTemplate, memberUserAdapter: MemberUserAdapter?) {
            when (binding) {
                is MemberUserItemBinding -> {
                    val item= memberUserViewModel as MemberUserViewModelTemplate.MemberUserViewModel
                    binding.item= item
                    binding.fragment= fragment
                    memberUserViewModel.viewHolder= this
                    this.itemView.setOnLongClickListener {
                        if (item.group.name.equals(Constants.GROUP_TODOS, true) && fragment.viewModel.uiModel.user.value!!.isBoss) {
                            (itemView.context as AppCompatActivity).startSupportActionMode(deleteUserActionModeCallback!!)
                            deleteUserActionModeCallback.selectUser(item)
                        }
                        true
                    }
                    this.itemView.setOnClickListener {
                        if (item.group.name.equals(Constants.GROUP_TODOS, true) && fragment.viewModel.uiModel.user.value!!.isBoss) {
                            deleteUserActionModeCallback!!.selectUser(item)
                        }
                        true
                    }
                    binding.invalidateAll() //executePendingBindings()
                }
                is HeaderGroupMembersItemBinding -> {
                    binding.item= memberUserViewModel as MemberUserViewModelTemplate.GroupMemberUserViewModel
                    memberUserViewModel.viewHolder= this
                    //memberUserViewModel.init()
                    binding.invalidateAll() //executePendingBindings()
                }
                else -> Unit
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= when (viewType) {
        R.layout.header_group_members_item -> MemberUserViewHolder(
            HeaderGroupMembersItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), fragment, null)
        R.layout.member_user_item -> MemberUserViewHolder(
            MemberUserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), fragment, deleteUserActionModeCallback)
        R.layout.empty_item_view -> MemberUserViewHolder(
            EmptyItemViewBindingImpl.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), fragment, null)
        else -> MemberUserViewHolder(
            LoadingItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), fragment, null)
    }

    override fun getItemCount()= members.size

    override fun onBindViewHolder(holder: MemberUserViewHolder, position: Int)= when (holder.itemViewType) {
        R.layout.header_group_members_item -> {
            holder.bind(members[position] as MemberUserViewModelTemplate.GroupMemberUserViewModel, null)
        }
        R.layout.member_user_item -> {
            val memberUserViewModel= members[position] as MemberUserViewModelTemplate.MemberUserViewModel
            holder.bind(memberUserViewModel, this)
        } else -> {
            holder.itemView.isClickable= false
        }
    }

    override fun getItemViewType(position: Int)= when (members[position]) {
        is MemberUserViewModelTemplate.GroupMemberUserViewModel -> R.layout.header_group_members_item
        is MemberUserViewModelTemplate.MemberUserViewModel -> R.layout.member_user_item
        MemberUserViewModelTemplate.MemberUserViewModelLoading -> R.layout.loading_item_view
        MemberUserViewModelTemplate.MemberUserViewModelEmpty -> R.layout.empty_item_view
    }

    private fun runLayoutAnimation() {
        val context: Context = recyclerView.context
        val controller: LayoutAnimationController =
            AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)
        recyclerView.apply {
            layoutAnimation = controller
            layoutAnimationListener = object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    members.forEachIndexed {
                        index, viewModel ->
                        if (viewModel== MemberUserViewModelTemplate.MemberUserViewModelEmpty ||
                                viewModel== MemberUserViewModelTemplate.MemberUserViewModelLoading ||
                                viewModel is MemberUserViewModelTemplate.GroupMemberUserViewModel) {
                            recyclerView.layoutManager?.findViewByPosition(index)?.clearAnimation()
                        }
                    }
                }
                override fun onAnimationEnd(animation: Animation?) { }
                override fun onAnimationRepeat(animation: Animation?) { }
            }
            scheduleLayoutAnimation()
        }
    }

    fun setData(data: List<MemberUserViewModelTemplate>?) {
        if (data!= null) {
            members.clear()
            members.addAll(data)
            notifyDataSetChanged()
            runLayoutAnimation()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView= recyclerView
    }


}