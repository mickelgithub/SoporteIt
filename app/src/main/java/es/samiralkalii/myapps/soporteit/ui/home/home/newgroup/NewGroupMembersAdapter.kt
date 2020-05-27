package es.samiralkalii.myapps.soporteit.ui.home.home.newgroup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.ErrorItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.LoadingItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.NewGroupMemberUserItemBinding

class NewGroupMembersAdapter(val members: MutableList<MemberUserNewGroupTemplate>): RecyclerView.Adapter<NewGroupMembersAdapter.NewGroupMemberUserViewHolder>() {

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
    }

    fun setData(data: List<MemberUserNewGroupTemplate>?) {
        if (data!= null) {
            members.clear()
            members.addAll(data)
            notifyDataSetChanged()
        }
    }

}