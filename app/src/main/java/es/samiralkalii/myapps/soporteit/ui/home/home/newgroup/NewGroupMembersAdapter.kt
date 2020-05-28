package es.samiralkalii.myapps.soporteit.ui.home.home.newgroup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ErrorItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.LoadingItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.NewGroupMemberUserItemBinding
import es.samiralkalii.myapps.soporteit.databinding.SuccessItemViewBinding
import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate

class NewGroupMembersAdapter(val members: MutableList<MemberUserNewGroupTemplate>): RecyclerView.Adapter<NewGroupMembersAdapter.NewGroupMemberUserViewHolder>() {

    private lateinit var recyclerView: RecyclerView

    class NewGroupMemberUserViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(memberUserNewGroup: MemberUserNewGroupTemplate) {
            when (binding) {
                is NewGroupMemberUserItemBinding -> {
                    binding.item= memberUserNewGroup as MemberUserNewGroupTemplate.MemberUserNewGroupViewModel
                    binding.invalidateAll() //executePendingBindings()
                }
                is ErrorItemViewBinding -> {
                    binding.item= memberUserNewGroup as MemberUserNewGroupTemplate.MemberUserNewGroupViewModelError
                    binding.invalidateAll() //executePendingBindings()
                }
                else -> Unit
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= when (viewType) {
        R.layout.new_group_member_user_item -> NewGroupMemberUserViewHolder(
            NewGroupMemberUserItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        R.layout.loading_item_view -> NewGroupMemberUserViewHolder(
            LoadingItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        R.layout.success_item_view -> NewGroupMemberUserViewHolder(
            SuccessItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        else -> NewGroupMemberUserViewHolder(
            ErrorItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount()= members.size

    override fun onBindViewHolder(holder: NewGroupMemberUserViewHolder, position: Int)= when (holder.itemViewType) {
        R.layout.new_group_member_user_item -> {
            holder.bind(members[position] as MemberUserNewGroupTemplate.MemberUserNewGroupViewModel)
        }
        R.layout.error_item_view -> {
            holder.bind(members[position] as MemberUserNewGroupTemplate.MemberUserNewGroupViewModelError)
        }
        else -> {
        }
    }

    override fun getItemViewType(position: Int)= when (members[position]) {
        is MemberUserNewGroupTemplate.MemberUserNewGroupViewModel -> R.layout.new_group_member_user_item
        is MemberUserNewGroupTemplate.MemberUserNewGroupViewModelError -> R.layout.error_item_view
        MemberUserNewGroupTemplate.MemberUserNewGroupViewModelLoading -> R.layout.loading_item_view
        MemberUserNewGroupTemplate.MemberUserNewGroupViewModelSuccess -> R.layout.success_item_view
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
                        if (viewModel== MemberUserNewGroupTemplate.MemberUserNewGroupViewModelLoading ||
                            viewModel is MemberUserNewGroupTemplate.MemberUserNewGroupViewModelError) {
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

    fun setData(data: List<MemberUserNewGroupTemplate>?) {
        if (data!= null) {
            members.clear()
            members.addAll(data)
            notifyDataSetChanged()
            runLayoutAnimation()
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

}