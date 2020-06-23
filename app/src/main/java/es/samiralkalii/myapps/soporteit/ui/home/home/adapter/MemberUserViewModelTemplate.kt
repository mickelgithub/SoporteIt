package es.samiralkalii.myapps.soporteit.ui.home.home.adapter

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Group
import es.samiralkalii.myapps.domain.teammanagement.KEY_GROUP_MAP_ID
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.AlertDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.showDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentViewModel

import es.samiralkalii.myapps.soporteit.ui.home.home.confirmmember.ConfirmMemberDialog
import es.samiralkalii.myapps.soporteit.ui.util.*
import es.samiralkalii.myapps.soporteit.ui.util.Constants.Companion.GROUP_INTERNALS
import es.samiralkalii.myapps.soporteit.ui.util.Constants.Companion.GROUP_ALL
import org.slf4j.LoggerFactory

sealed class MemberUserViewModelTemplate {

    class GroupMemberUserViewModel(val user: User, val group: Group, var subItems: List<MemberUserViewModel>, val viewModel: HomeFragmentViewModel): MemberUserViewModelTemplate() {

        private val logger= LoggerFactory.getLogger(GroupMemberUserViewModel::class.java)

        lateinit var viewHolder: MemberUserAdapter.MemberUserViewHolder

        private val _expandImage= MutableLiveData(R.drawable.ic_right)
        val expandImage: LiveData<Int>
            get() = _expandImage

        fun setExpanded(expand: Boolean) {
            _expandImage.value= if (expand) R.drawable.ic_down else R.drawable.ic_right
        }

        val isExpanded
            get() = _expandImage.value== R.drawable.ic_down

        fun onExpandClick() {
            viewModel.onExpandClick(this)
        }

        fun onGroupOverflowMenuClick(v: View) {
            PopupMenu(v.context, v).apply {
                setOnMenuItemClickListener { when (it.itemId) {
                    R.id.delete_group -> {
                        val confirmDialog= AlertDialog.newInstanceForMessage(
                            v.resources.getString(R.string.confirm_operacion),
                            v.resources.getString(R.string.confirm_group_deletion, group.name),
                            v.resources.getString(R.string.confirm),
                            {viewModel.onDeleteGroup(group.id)},
                            v.resources.getString(R.string.cancel),
                            {}
                        )
                        (v.context as AppCompatActivity).showDialog(confirmDialog)
                        true
                    }
                    R.id.update_group -> {
                        viewModel.updateGroup(group)
                        true
                    }
                    else -> {
                        false
                    }
                } }
                inflate(R.menu.menu_group_overflow)
                if (group.name in listOf(GROUP_ALL, GROUP_INTERNALS) || !user.isBoss) {
                    menu.findItem(R.id.delete_group).isVisible= false
                    menu.findItem(R.id.update_group).isVisible= false
                }
                show()
            }
        }

    }

    class MemberUserViewModel(val user: User, val hostUser: User, val group: Group): MemberUserViewModelTemplate() {

        private val logger= LoggerFactory.getLogger(MemberUserViewModel::class.java)

        lateinit var viewHolder: MemberUserAdapter.MemberUserViewHolder

        private val _email= MutableLiveData(user.email)
        val email: LiveData<String>
            get() = _email

        private val _firstName= MutableLiveData(user.firstName)
        val firstName: LiveData<String>
            get() = _firstName

        private val _profileImage= MutableLiveData(user.remoteProfileImage)
        val profileImage: LiveData<String>
            get() = _profileImage

        private val _profileBackColor= MutableLiveData(user.profileBackColor)
        val profileBackColor: LiveData<Int>
            get() = _profileBackColor

        private val _profileTextColor= MutableLiveData(user.profileTextColor)
        val profileTextColor: LiveData<Int>
            get() = _profileTextColor

        private val _memberStateImage= MutableLiveData(if (!user.isBoss && user.membershipConfirmation== "") R.drawable.ko else null)
        val memberStateImage: LiveData<Int?>
            get() = _memberStateImage

        private val _isBoss= MutableLiveData(user.isBoss)
        val isBoss: LiveData<Boolean>
            get() = _isBoss

        private val _isSelected= MutableLiveData<Boolean>(false)
        val isSelected: LiveData<Boolean>
            get() = _isSelected

        fun onMemberStateImageClick() {
            when (_memberStateImage.value) {
                R.drawable.ko -> {
                    //Se trata del boss confirmando un miembro
                    logger.debug("CONFIRMAMOS EL USUARIO ${user.email}......................")
                    showConfirmMemberDialog(user.id, user.email, user.remoteProfileImage,
                        user.firstName, user.profileTextColor, user.profileBackColor, user.areaId, user.departmentId, group.id)
                }
            }
        }

        private fun showConfirmMemberDialog(user: String, email: String, remoteProfileImage: String,
                                            name: String, profileTextColor: Int, profileBackColor: Int, area: String, department: String, group: String) {
            var confirmMemberDialog: ConfirmMemberDialog?= (viewHolder.itemView.context as AppCompatActivity).supportFragmentManager.findFragmentByTag(
                ConfirmMemberDialog::class.java.simpleName) as ConfirmMemberDialog?
            if (confirmMemberDialog== null) {
                val bundle= bundleOf(KEY_ID to user,
                    KEY_EMAIL to email,
                    KEY_REMOTE_PROFILE_IMAGE to remoteProfileImage,
                    KEY_NAME to name,
                    KEY_PROFILE_TEXT_COLOR to profileTextColor,
                    KEY_PROFILE_BACK_COLOR to profileBackColor,
                    KEY_AREA_ID to area,
                    KEY_DEPARTMENT_ID to department,
                    KEY_GROUP_MAP_ID to group
                )
                confirmMemberDialog= ConfirmMemberDialog.newInstance(bundle)
                confirmMemberDialog.show((viewHolder.itemView.context as AppCompatActivity).supportFragmentManager, ConfirmMemberDialog::class.java.simpleName)
            }
        }

        fun select(selected: Boolean) {
            _isSelected.value= selected
            viewHolder.binding.invalidateAll()
        }
    }

    object MemberUserViewModelLoading: MemberUserViewModelTemplate()

    object MemberUserViewModelEmpty: MemberUserViewModelTemplate()

}