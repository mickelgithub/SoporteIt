package es.samiralkalii.myapps.soporteit.ui.home.home.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentViewModel

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

    class MemberUserViewModel(val user: User): MemberUserViewModelTemplate() {

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
            get() = profileTextColor

        private val _showConfirmImage= MutableLiveData<Boolean?>()
        val showConfirmImage: LiveData<Boolean?>
            get() = _showConfirmImage

        init {
            init()
        }

        fun init() {
            _email.value= user.email
            _profileImage.value= user.profileImage
            _profileBackColor.value= user.profileBackColor
            _profileTextColor.value= user.profileTextColor
            _showConfirmImage.value= !user.isBoss && user.membershipConfirmation== ""
        }



    }

    object MemberUserViewModelLoading: MemberUserViewModelTemplate()

    object MemberUserViewModelEmpty: MemberUserViewModelTemplate()
}