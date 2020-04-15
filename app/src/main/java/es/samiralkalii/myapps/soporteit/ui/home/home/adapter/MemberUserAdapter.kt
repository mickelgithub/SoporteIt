package es.samiralkalii.myapps.soporteit.ui.home.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.HeaderGroupMembersItemBinding
import es.samiralkalii.myapps.soporteit.databinding.LoadingItemViewBinding
import es.samiralkalii.myapps.soporteit.databinding.MemberUserItemBinding
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentViewModel
import org.slf4j.LoggerFactory

class MemberUserAdapter(val members: MutableList<MemberUserViewModelTemplate>, val homeFragmentViewModel: HomeFragmentViewModel): RecyclerView.Adapter<MemberUserAdapter.MemberUserViewHolder>() {

    private val logger= LoggerFactory.getLogger(MemberUserAdapter::class.java)


    class MemberUserViewHolder(val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(memberUserViewModel: MemberUserViewModelTemplate) {
            when (binding) {
                is MemberUserItemBinding -> {
                    binding.item= memberUserViewModel as MemberUserViewModelTemplate.MemberUserViewModel
                    //memberUserViewModel.viewHolder= this
                    memberUserViewModel.init()
                    binding.executePendingBindings()
                }
                is HeaderGroupMembersItemBinding -> {
                    binding.item= memberUserViewModel as MemberUserViewModelTemplate.GroupMemberUserViewModel
                    //memberUserViewModel.viewHolder= this
                    memberUserViewModel.init()
                    binding.executePendingBindings()
                }
                else -> Unit
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= when (viewType) {
        R.layout.header_group_members_item -> MemberUserViewHolder(
            HeaderGroupMembersItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
        R.layout.member_user_item -> MemberUserViewHolder(
            MemberUserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        else -> MemberUserViewHolder(
            LoadingItemViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount()= members.size

    override fun onBindViewHolder(holder: MemberUserViewHolder, position: Int) {
        if (holder.itemViewType== R.layout.header_group_members_item) {
            holder.bind(members[position] as MemberUserViewModelTemplate.GroupMemberUserViewModel)
        } else if (holder.itemViewType== R.layout.member_user_item) {
            holder.bind(members[position] as MemberUserViewModelTemplate.MemberUserViewModel)
        } else {
            holder.itemView.isClickable= false
        }
    }

    override fun getItemViewType(position: Int)= when (members[position]) {
        is MemberUserViewModelTemplate.GroupMemberUserViewModel -> R.layout.header_group_members_item
        is MemberUserViewModelTemplate.MemberUserViewModel -> R.layout.member_user_item
        MemberUserViewModelTemplate.MemberUserViewModelLoading -> R.layout.loading_item_view
    }
}