package es.samiralkalii.myapps.soporteit.ui.home.home.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.home.home.confirmmember.ConfirmMemberDialog
import es.samiralkalii.myapps.soporteit.ui.util.*
import org.slf4j.LoggerFactory

sealed class MemberUserViewModelTemplate {

    class GroupMemberUserViewModel(val groupName: String): MemberUserViewModelTemplate()

    class MemberUserViewModel(val user: User, val hostUser: User): MemberUserViewModelTemplate() {

        private val logger= LoggerFactory.getLogger(MemberUserViewModel::class.java)

        lateinit var viewHolder: MemberUserAdapter.MemberUserViewHolder
        //lateinit var memberUserAdapter: MemberUserAdapter

        private val _email= MutableLiveData<String?>()
        val email: LiveData<String?>
            get() = _email

        private val _firstName= MutableLiveData<String?>()
        val firstName: LiveData<String?>
            get() = _firstName

        private val _profileImage= MutableLiveData<String?>()
        val profileImage: LiveData<String?>
            get() = _profileImage

        private val _profileBackColor= MutableLiveData<Int?>()
        val profileBackColor: LiveData<Int?>
            get() = _profileBackColor

        private val _profileTextColor= MutableLiveData<Int?>()
        val profileTextColor: LiveData<Int?>
            get() = _profileTextColor

        private val _memberStateImage= MutableLiveData<Int?>()
        val memberStateImage: LiveData<Int?>
            get() = _memberStateImage

        private val _isBoss= MutableLiveData<Boolean?>()
        val isBoss: LiveData<Boolean?>
            get() = _isBoss

        init {
            init()
        }

        fun init() {
            _email.value= user.email
            _profileImage.value= user.remoteProfileImage
            _profileBackColor.value= user.profileBackColor
            _profileTextColor.value= user.profileTextColor
            _firstName.value= user.firstName
            _memberStateImage.value= when {
                !user.isBoss && user.membershipConfirmation== "" -> R.drawable.ko
                else -> null
            }
            _isBoss.value= user.isBoss
        }

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