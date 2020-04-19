package es.samiralkalii.myapps.soporteit.ui.home.home.adapter

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.MemberUserItemBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.AlertDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.showDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentStates
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragmentViewModel
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.animators.animateRevealView
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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

    class MemberUserViewModel(val user: User, val homeFragmentViewModel: HomeFragmentViewModel): MemberUserViewModelTemplate() {

        private val logger= LoggerFactory.getLogger(MemberUserViewModel::class.java)

        lateinit var viewHolder: MemberUserAdapter.MemberUserViewHolder

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

        fun onMemberStateImageClick() {
            when {
                _memberStateImage.value== R.drawable.ko -> {
                    //Se trata del boss confirmando un miembro
                    showConfirmationDialog()

                }
            }
        }

        fun showConfirmationDialog() {
            (viewHolder.itemView.context as AppCompatActivity).showDialog(AlertDialog.newInstanceForMessage(viewHolder.itemView.context.getString(R.string.member_verification_title),
                viewHolder.itemView.context.getString(R.string.member_verification_title_msg, user.email.substring(0, user.email.indexOf("@"))),
                viewHolder.itemView.context.getString(R.string.confirm),
                {
                    logger.debug("CONFIRMAMOS EL USUARIO ${user.email}......................")
                    confirmDenyMember((viewHolder.binding as MemberUserItemBinding).memberStateImage, true)
                },
                viewHolder.itemView.context.getString(R.string.deny),
                {
                    logger.debug("Rechazamos EL USUARIO ${user.email}......................")
                    confirmDenyMember((viewHolder.binding as MemberUserItemBinding).memberStateImage, true)

                }))
        }

        fun confirmDenyMember(view: View, isConfirmed: Boolean) {
            val errorHandler = CoroutineExceptionHandler { _, error ->
                logger.error(error.toString(), error)
                var message = R.string.not_controled_error
                if (error is FirebaseFirestoreException) {
                    if (error.code == FirebaseFirestoreException.Code.UNAVAILABLE) {
                        message = R.string.no_internet_connection
                    }
                } else {
                    message= R.string.not_controled_error
                }
                val messagedesc= view.context.resources.getString(message)
                homeFragmentViewModel.updateDialogState(MyDialog.DialogState.UpdateMessage(messagedesc))
            }
            homeFragmentViewModel.updateDialogState(MyDialog.DialogState.ShowProgressDialog())
            homeFragmentViewModel.viewModelScope.launch(errorHandler) {
                async(Dispatchers.IO) {
                    homeFragmentViewModel.confirmDenyMember(user.id, isConfirmed)
                }.await()
                homeFragmentViewModel.updateDialogState(MyDialog.DialogState.UpdateSuccess())
                view.animateRevealView({view.visibility= View.GONE})
            }
        }







    }

    object MemberUserViewModelLoading: MemberUserViewModelTemplate()

    object MemberUserViewModelEmpty: MemberUserViewModelTemplate()

}