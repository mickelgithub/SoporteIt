package es.samiralkalii.myapps.soporteit.ui.home.home.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.databinding.DialogMemberConfirmationBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.util.*
import org.slf4j.LoggerFactory
import org.koin.android.viewmodel.ext.android.viewModel

class ConfirmMemberDialog: MyDialog() {

    private val logger = LoggerFactory.getLogger(ConfirmMemberDialog::class.java)

    private lateinit var binding: DialogMemberConfirmationBinding

    val viewModel: ConfirmMemberDialogViewModel by viewModel()

    companion object {

        fun newInstance(bundle: Bundle)= ConfirmMemberDialog().apply {
            this.arguments= bundle
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger.debug("onAttach....")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true

        val bundle= Bundle().apply {
            val user = arguments!!.getString(KEY_ID, "")
            val email = arguments!!.getString(KEY_EMAIL, "")
            val remoteProfileImage = arguments!!.getString(KEY_REMOTE_PROFILE_IMAGE, "")
            val name = arguments!!.getString(KEY_NAME, "")
            val profileTextColor = arguments!!.getInt(KEY_PROFILE_TEXT_COLOR, -1)
            val profileBackColor = arguments!!.getInt(KEY_PROFILE_BACK_COLOR, -1)
            viewModel.publishUser(user, email, remoteProfileImage,
                name, profileTextColor, profileBackColor)
            logger.debug("onCreate...")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        logger.debug("onCreateView")
        binding= DialogMemberConfirmationBinding.inflate(inflater, container, false)
        binding.lifecycleOwner= viewLifecycleOwner
        binding.viewModel= viewModel
        binding.executePendingBindings()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        logger.debug("OnStop.....")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.debug("onDestroyView...")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("OnDestroy...")
    }

    class ConfirmMemberDialogViewModel(private val getProfilesUseCase: GetProfilesUseCase): ViewModel() {


        private lateinit var user: String

        fun publishUser(user: String, email: String, remoteProfileImage: String,
                        name: String, profileTextColor: Int, profileBackColor: Int) {
            this.user= user
            _email.value= email.substring(0, email.indexOf("@"))
            _profileImage.value= remoteProfileImage
            _firstName.value= name
            _profileTextColor.value= profileTextColor
            _profileBackColor.value= profileBackColor

        }

        val _firstName= MutableLiveData<String>("")
        val firstName: LiveData<String>
            get()= _firstName

        val _profileImage= MutableLiveData<String>("")
        val profileImage: LiveData<String>
            get()= _profileImage

        val _profileTextColor= MutableLiveData<Int?>()
        val profileTextColor: LiveData<Int?>
            get()= _profileTextColor

        val _profileBackColor= MutableLiveData<Int?>()
        val profileBackColor: LiveData<Int?>
            get()= _profileBackColor

        val profile= MutableLiveData<String>("")
        val holidayDaysValue= MutableLiveData<String>("")

        private val _profiles= MutableLiveData<List<String>>(listOf("Programador Junior", "Programador Señor", "Analista Programador"))
        val profiles: LiveData<List<String>>
            get() = _profiles

        private val _holidayDays= MutableLiveData<List<String>>(listOf("20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"))
        val holidayDays: LiveData<List<String>>
            get() = _holidayDays

        private val _email= MutableLiveData<String>("samiralkalii@gmail.com")
        val email: LiveData<String>
            get() = _email

        private val _profileError= MutableLiveData<Int?>()
        val profileError: LiveData<Int?>
            get() = _profileError

        private val _holidayDaysError= MutableLiveData<Int?>()
        val holidayDaysError: LiveData<Int?>
            get() = _holidayDaysError

        private val _showLoading= MutableLiveData<Boolean>(true)
        val showLoading: LiveData<Boolean>
            get() = _showLoading

        private var _internal= MutableLiveData<Boolean>(false)
        val internal: LiveData<Boolean>
            get() = _internal

        fun onInternalExternalClick() {
            val oldValue= _internal.value!!
            _internal.value= !oldValue
        }
    }
}