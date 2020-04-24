package es.samiralkalii.myapps.soporteit.ui.home.home.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.*
import com.google.firebase.firestore.FirebaseFirestoreException
import es.samiralkalii.myapps.domain.teammanagement.Profiles
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.DialogMemberConfirmationBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import es.samiralkalii.myapps.soporteit.ui.util.*
import es.samiralkalii.myapps.usecase.teammanagement.ConfirmDenyMemberUseCase
import es.samiralkalii.myapps.usecase.teammanagement.GetProfilesUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class ConfirmMemberDialog: MyDialog() {

    private val logger = LoggerFactory.getLogger(ConfirmMemberDialog::class.java)

    private lateinit var binding: DialogMemberConfirmationBinding

    val viewModel: ConfirmMemberDialogViewModel by viewModel()

    private lateinit var user: String

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

        val bundle= Bundle().apply {
            val user = arguments!!.getString(KEY_ID, "")
            this@ConfirmMemberDialog.user= user
            val email = arguments!!.getString(KEY_EMAIL, "")
            val remoteProfileImage = arguments!!.getString(KEY_REMOTE_PROFILE_IMAGE, "")
            val name = arguments!!.getString(KEY_NAME, "")
            val profileTextColor = arguments!!.getInt(KEY_PROFILE_TEXT_COLOR, -1)
            val profileBackColor = arguments!!.getInt(KEY_PROFILE_BACK_COLOR, -1)
            val area= arguments!!.getString(KEY_AREA_ID, "")
            viewModel.publishUser(user, email, remoteProfileImage,
                name, profileTextColor, profileBackColor, area)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.dismissDialog.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    val homeFragment= activity!!.supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName) as HomeFragment
                    if (homeFragment!= null) {
                        homeFragment.updateModelUserConfirmed(user)
                    }
                    dismiss()
                }
            }
        })
        viewModel.dialogCancelable.observe(viewLifecycleOwner, Observer{
            isCancelable= it
        })
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

    class ConfirmMemberDialogViewModel(private val getProfilesUseCase: GetProfilesUseCase,
    private val confirmDenyMemberUseCase: ConfirmDenyMemberUseCase): ViewModel() {

        private val logger = LoggerFactory.getLogger(ConfirmMemberDialogViewModel::class.java)

        private lateinit var user: String
        private lateinit var area: String
        private val holidaysData= listOf("20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30")

        private lateinit var profilesData: Profiles

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

        private val _profiles= MutableLiveData<List<String>>()
        val profiles: LiveData<List<String>>
            get() = _profiles

        private val _holidayDays= MutableLiveData<List<String>>(holidaysData)
        val holidayDays: LiveData<List<String>>
            get() = _holidayDays

        private val _email= MutableLiveData<String>()
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

        private var _confirmActionError= MutableLiveData(false)
        val confirmActionError: LiveData<Boolean>
            get() = _confirmActionError

        private var _confirmActionErrorMsg= MutableLiveData<Int?>()
        val confirmActionErrorMsg: LiveData<Int?>
            get() = _confirmActionErrorMsg

        private val _formEnabled= MutableLiveData<Boolean>(false)
        val formEnabled: LiveData<Boolean>
            get() = _formEnabled

        private val _dismissDialog= MutableLiveData<Boolean>(false)
        val dismissDialog: LiveData<Boolean>
            get() = _dismissDialog

        private val _dialogCancelable= MutableLiveData(true)
        val dialogCancelable: LiveData<Boolean>
            get() = _dialogCancelable

        fun init() {
            val errorHandler = CoroutineExceptionHandler { _, error ->
                logger.error(error.toString(), error)
                var message= R.string.not_controled_error
                if (error is FirebaseFirestoreException) {
                    if (error.code.ordinal== FirebaseFirestoreException.Code.UNAVAILABLE.ordinal) {
                        message= R.string.no_internet_connection
                    }
                }
                _confirmActionError.postValue( true)
                _confirmActionErrorMsg.postValue(message)
                _showLoading.postValue(false)
                _dialogCancelable.postValue(true)
            }
            _dialogCancelable.value= false
            viewModelScope.launch(errorHandler) {
                profilesData= async(Dispatchers.IO) {
                    getProfilesUseCase(area)
                }.await()
                _profiles.value= profilesData.profiles.map { it.name }
                _showLoading.value= false
                _formEnabled.value= true
                _dialogCancelable.value= true
            }
        }

        private fun getMediatorLiveDataForConfirmButtonEnabledState()= MediatorLiveData<Boolean>().apply {
            value= false
            var profileCorrect= false
            var holidayDaysCorrect= false
            var formEnabledValue= false
            addSource(profile, { x -> x?.let {
                profileCorrect= it.isNotBlank()
                value= profileCorrect && holidayDaysCorrect && formEnabledValue
            }
            })
            addSource(holidayDaysValue, { x -> x?.let {
                holidayDaysCorrect= it.isNotBlank()
                value= profileCorrect && holidayDaysCorrect && formEnabledValue
            }
            })
            addSource(formEnabled, { x -> x?.let {
                formEnabledValue= it
                value= profileCorrect && holidayDaysCorrect && formEnabledValue
            }
            })

        }

        val buttonConfirmEnabled= getMediatorLiveDataForConfirmButtonEnabledState()

        fun publishUser(user: String, email: String, remoteProfileImage: String,
                        name: String, profileTextColor: Int, profileBackColor: Int, area: String) {
            this.user= user
            _email.value= email.substring(0, email.indexOf("@"))
            _profileImage.value= remoteProfileImage
            _firstName.value= name
            _profileTextColor.value= profileTextColor
            _profileBackColor.value= profileBackColor
            this.area= area
            //we load profiles
            init()
        }

        fun onInternalExternalClick() {
            val oldValue= _internal.value!!
            _internal.value= !oldValue
            _internal.value?.let {
                if (it) {
                    holidayDaysValue.value= "26"
                } else {
                    _holidayDays.value= holidaysData
                }
            }
        }

        fun onConfirmButtonClick() {
            val errorHandler = CoroutineExceptionHandler { _, error ->
                logger.error(error.toString(), error)
                var message= R.string.not_controled_error
                if (error is FirebaseFirestoreException) {
                    if (error.code.ordinal== FirebaseFirestoreException.Code.UNAVAILABLE.ordinal) {
                        message= R.string.no_internet_connection
                    }
                }
                _confirmActionError.postValue( true)
                _confirmActionErrorMsg.postValue(message)
                _showLoading.postValue(false)
                _dialogCancelable.postValue(true)
            }
            _showLoading.value= true
            _formEnabled.value= false
            _dialogCancelable.value= false
            viewModelScope.launch(errorHandler) {
                val profileId= profilesData.getProfileId(profile.value!!)
                async(Dispatchers.IO) {
                    confirmDenyMemberUseCase(user, true, profile.value!!, profileId,
                        holidayDaysValue.value!!.toInt(), _internal.value!!)
                }.await()
                _showLoading.value= false
                _dismissDialog.value= true
            }
        }

    }
}