package es.samiralkalii.myapps.soporteit.ui.home.home.newgroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import es.samiralkalii.myapps.domain.User

sealed class MemberUserNewGroupTemplate {

    object MemberUserNewGroupViewModelLoading: MemberUserNewGroupTemplate()

    class MemberUserNewGroupViewModelError(val error: String): MemberUserNewGroupTemplate()

    class MemberUserNewGroupViewModel(val user: User): MemberUserNewGroupTemplate() {

        private val _email= MutableLiveData(user.email.substring(0, user.email.indexOf("@")))
        val email: LiveData<String>
            get() = _email

        private val _firstName= MutableLiveData(user.firstName)
        val firstName: LiveData<String>
            get() = _firstName

        private val _profileImage= MutableLiveData(user.remoteProfileImage ?: "")
        val profileImage: LiveData<String>
            get() = _profileImage

        private val _profileBackColor= MutableLiveData(user.profileBackColor)
        val profileBackColor: LiveData<Int>
            get() = _profileBackColor

        private val _profileTextColor= MutableLiveData(user.profileTextColor)
        val profileTextColor: LiveData<Int>
            get() = _profileTextColor

    }

}
