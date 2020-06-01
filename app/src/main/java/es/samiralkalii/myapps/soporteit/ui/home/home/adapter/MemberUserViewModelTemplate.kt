package es.samiralkalii.myapps.soporteit.ui.home.home.adapter

import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.domain.teammanagement.Group
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.dialog.AlertDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.showDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentViewModel

import es.samiralkalii.myapps.soporteit.ui.home.home.confirmmember.ConfirmMemberDialog
import es.samiralkalii.myapps.soporteit.ui.util.*
import org.slf4j.LoggerFactory

sealed class MemberUserViewModelTemplate {

    class GroupMemberUserViewModel(val user: User, val group: Group, val viewModel: HomeFragmentViewModel): MemberUserViewModelTemplate() {

        private val logger= LoggerFactory.getLogger(GroupMemberUserViewModel::class.java)

        lateinit var viewHolder: MemberUserAdapter.MemberUserViewHolder

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
                if (group.name in listOf(GROUP_TODOS, GROUP_INTERNALS) || !user.isBoss) {
                    menu.findItem(R.id.delete_group).isVisible= false
                    menu.findItem(R.id.update_group).isVisible= false
                }
                show()
            }
        }

    }

    class MemberUserViewModel(val user: User, val hostUser: User): MemberUserViewModelTemplate() {

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

        fun onMemberStateImageClick() {
            when (_memberStateImage.value) {
                R.drawable.ko -> {
                    //Se trata del boss confirmando un miembro
                    logger.debug("CONFIRMAMOS EL USUARIO ${user.email}......................")
                    showConfirmMemberDialog(user.id, user.email, user.remoteProfileImage,
                        user.firstName, user.profileTextColor, user.profileBackColor, user.areaId)
                }
            }
        }

        private fun showConfirmMemberDialog(user: String, email: String, remoteProfileImage: String,
                                            name: String, profileTextColor: Int, profileBackColor: Int, area: String) {
            var confirmMemberDialog: ConfirmMemberDialog?= (viewHolder.itemView.context as AppCompatActivity).supportFragmentManager.findFragmentByTag(
                ConfirmMemberDialog::class.java.simpleName) as ConfirmMemberDialog?
            if (confirmMemberDialog== null) {
                val bundle= bundleOf(KEY_ID to user,
                    KEY_EMAIL to email,
                    KEY_REMOTE_PROFILE_IMAGE to remoteProfileImage,
                    KEY_NAME to name,
                    KEY_PROFILE_TEXT_COLOR to profileTextColor,
                    KEY_PROFILE_BACK_COLOR to profileBackColor,
                    KEY_AREA_ID to area
                )
                confirmMemberDialog= ConfirmMemberDialog.newInstance(bundle)
                confirmMemberDialog.show((viewHolder.itemView.context as AppCompatActivity).supportFragmentManager, ConfirmMemberDialog::class.java.simpleName)
            }
        }
    }

    object MemberUserViewModelLoading: MemberUserViewModelTemplate()

    object MemberUserViewModelEmpty: MemberUserViewModelTemplate()

}