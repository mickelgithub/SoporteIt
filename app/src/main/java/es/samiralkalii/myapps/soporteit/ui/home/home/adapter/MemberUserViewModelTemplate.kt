package es.samiralkalii.myapps.soporteit.ui.home.home.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentViewModel
import org.slf4j.LoggerFactory

sealed class MemberUserViewModelTemplate {

    class GroupMemberUserViewModel(val groupName: String): MemberUserViewModelTemplate() {

        private val _grName= MutableLiveData<String?>()
        val gnName: LiveData<String?>
            get() = _grName

        init {
            init()
        }

        fun init() {
            _grName.value= groupName
        }

    }

    class MemberUserViewModel(val user: User, homeFragmentViewModel: HomeFragmentViewModel): MemberUserViewModelTemplate() {

        private val logger= LoggerFactory.getLogger(MemberUserViewModel::class.java)

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
        }

        fun onMemberStateImageClick(user: String) {
            when {
                _memberStateImage.value== R.drawable.ko -> {
                    //Se trata del boss confirmando un miembro
                    logger.debug("Vamos a confirmar el miembro ")

                }
            }
        }





    }

    object MemberUserViewModelLoading: MemberUserViewModelTemplate()

    object MemberUserViewModelEmpty: MemberUserViewModelTemplate()
}