package es.samiralkalii.myapps.soporteit.ui.home.home.dialog

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.google.firebase.FirebaseNetworkException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.DialogInviteMemberBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import es.samiralkalii.myapps.soporteit.ui.util.bindImgSrc
import es.samiralkalii.myapps.soporteit.ui.util.toUser
import es.samiralkalii.myapps.usecase.teammanagement.GetAllUsersButBosesAndNoTeamUseCase
import es.samiralkalii.myapps.usecase.teammanagement.InviteUserUseCase
import kotlinx.coroutines.*
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class InviteMemberDialog(): MyDialog() {

    private val logger = LoggerFactory.getLogger(InviteMemberDialog::class.java)

    private lateinit var onInviteMember: OnInviteMemberListener

    private lateinit var adapter: MembersSuggestAdapter

    private lateinit var binding: DialogInviteMemberBinding

    val viewModel: InviteMemberDialogViewModel by viewModel()

    private lateinit var user: User


    companion object {

        fun newInstance(bundle: Bundle)= InviteMemberDialog().apply {
            this.arguments= bundle
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger.debug("onAttach....")
        onInviteMember= (context as AppCompatActivity).supportFragmentManager.findFragmentByTag(
            HomeFragment::class.java.simpleName) as OnInviteMemberListener

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
        user= (arguments as Bundle).toUser()
        viewModel.publishUser(user)
        logger.debug("onCreate...")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        logger.debug("onCreateView......")
        binding= DialogInviteMemberBinding.inflate(inflater, container, false)
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        binding.executePendingBindings()
        binding.holidayDaysPerYear.setSelection(binding.holidayDaysPerYear.text!!.length)
        return binding.root
    }

    fun clearErrors() {
        viewModel.holidayDaysError.value= null
        viewModel.membersError.value= null

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug("onViewCreated...")
        adapter= MembersSuggestAdapter(view.context, android.R.layout.simple_dropdown_item_1line)
        binding.members.setAdapter(adapter)
        binding.members.threshold = 4
        if (savedInstanceState!= null) {
            binding.members.post(Runnable {
                binding.members.dismissDropDown()
            })
        }

        binding.members.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearErrors()
                if (!s.isNullOrBlank() && s.toString().length>= 4) {
                    adapter.setData(adapter._data.filter { user ->
                        user.email.startsWith(s)
                    })
                    adapter.notifyDataSetChanged()
                }
            }
        })

        binding.holidayDaysPerYear.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearErrors()
            }
        })

        viewModel.dialogState.observe(this, Observer{
            when (it) {
                DialogState.ShowLoadingData -> {
                    viewModel.member.value= activity!!.resources.getString(R.string.wait_while_loading_data)
                    binding.members.isEnabled= false
                    binding.holidayDaysPerYear.isEnabled= false
                    binding.inviteMember.visibility= View.GONE
                    binding.animationOk.visibility= View.GONE
                    binding.message.visibility= View.GONE
                    binding.animationLoading.apply {
                        visibility= View.VISIBLE
                        playAnimation()
                    }
                }
                DialogState.ShowLoading -> {
                    binding.members.isEnabled= false
                    binding.holidayDaysPerYear.isEnabled= false
                    binding.inviteMember.visibility= View.GONE
                    binding.animationOk.visibility= View.GONE
                    binding.message.visibility= View.GONE
                    binding.animationLoading.apply {
                        visibility= View.VISIBLE
                        playAnimation()
                    }
                }
                DialogState.ShowDialog -> {
                    if (activity!!.resources.getString(R.string.wait_while_loading_data)== viewModel.member.value) {
                        viewModel.member.value= ""
                        binding.invalidateAll()
                    }
                    binding.members.isEnabled= true
                    binding.holidayDaysPerYear.isEnabled= true
                    binding.inviteMember.visibility= View.VISIBLE
                    binding.animationOk.visibility= View.GONE
                    binding.message.visibility= View.GONE
                    binding.animationLoading.visibility= View.GONE
                }
                DialogState.ShowSuccess -> {
                    binding.members.isEnabled= false
                    binding.holidayDaysPerYear.isEnabled= false
                    binding.inviteMember.visibility= View.GONE
                    binding.animationOk.apply {
                        visibility = View.VISIBLE
                        playAnimation()
                    }
                    binding.message.visibility= View.GONE
                    binding.animationLoading.visibility= View.GONE
                    Handler().postDelayed({
                        binding.animationOk.visibility= View.GONE
                        binding.members.isEnabled= true
                        binding.holidayDaysPerYear.isEnabled= true
                        binding.inviteMember.visibility= View.VISIBLE
                        viewModel.member.value= ""
                    }, DIALOG_DISMISS_DELAY)
                }
                is DialogState.ShowMessage -> {
                    binding.members.isEnabled= true
                    binding.holidayDaysPerYear.isEnabled= true
                    binding.inviteMember.visibility= View.GONE
                    binding.animationOk.visibility= View.GONE
                    binding.message.apply {
                        visibility= View.VISIBLE
                        setText(it.message)
                    }
                    binding.animationLoading.visibility= View.GONE
                }
            }
        })

        viewModel.users.observe(this, Observer {
                 adapter.initData(it)
            }
        )


    }

    private inner class MembersSuggestAdapter(context: Context, val resource: Int): ArrayAdapter<User>(context, resource),
        Filterable {

        val data= ArrayList<User>()
        val _data= ArrayList<User>()

        fun initData(users: List<User>) {
            data.clear()
            data.addAll(users)
            _data.clear()
            _data.addAll(users)
            notifyDataSetChanged()
        }

        fun setData(users: List<User>) {
            data.clear()
            data.addAll(users)
            notifyDataSetChanged()
        }


        override fun getCount(): Int {
            return data.size
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val user= data[position]
            android.R.layout.simple_dropdown_item_1line
            var view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_invite_member, parent, false)
            view.findViewById<TextView>(R.id.mail).text= user.email
            view.findViewById<ImageView>(R.id.image).bindImgSrc(if (user.remoteProfileImage.isNotBlank()) Uri.parse(user.remoteProfileImage) else null)
            return view
        }

        override fun getItem(position: Int): User? {
            return data[position]
        }

        override fun getFilter(): Filter {
            return object: Filter() {
                override fun performFiltering(constraint: CharSequence?): FilterResults {
                    val filterResults = FilterResults()
                    if (constraint != null) {
                        filterResults.values = data
                        filterResults.count = data.size
                    }
                    return filterResults
                }
                override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                    if (results != null && (results.count > 0)) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
                override fun convertResultToString(resultValue: Any?): CharSequence {
                    if (resultValue is User) {
                        return resultValue.email
                    }
                    return super.convertResultToString(resultValue)
                }
            }
        }
    }

    interface OnInviteMemberListener {
        fun onMemeberSelected(user: String)
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

    fun onInviteMemberClick() {
        viewModel.onInviteMemberClick()
    }

    class InviteMemberDialogViewModel(private val getAllUsersButBosesAndNoTeamUseCase: GetAllUsersButBosesAndNoTeamUseCase,
                                      private val inviteUserUseCase: InviteUserUseCase): ViewModel() {

        private val logger = LoggerFactory.getLogger(InviteMemberDialogViewModel::class.java)

        private val _dialogState= MutableLiveData<DialogState>()
        val dialogState: LiveData<DialogState>
        get() = _dialogState

        private val _users= MutableLiveData<List<User>>()
        val users: LiveData<List<User>>
        get() = _users

        private var _internal= MutableLiveData<Boolean>(true)
        val internal: LiveData<Boolean>
            get() = _internal

        var member= MutableLiveData<String>("")
        var holidayDays= MutableLiveData<String>(User.DEFAULT_HOLIDAY_DAYS_FOR_EXTERNALS.toString())
        val holidayDaysError= MutableLiveData<Int?>(null)
        val membersError= MutableLiveData<Int?>(null)

        lateinit var user: User

        fun publishUser(userParam: User) {
            user= userParam
        }

        fun onInternalExternalClick() {
            val oldValue= _internal.value!!
            _internal.value= !oldValue
        }

        init {
            loadAllUsersButBosesAndNoTeam()
        }

        private fun loadAllUsersButBosesAndNoTeam() {
            logger.debug("Cargando datos de remote database...")
            val errorHandler = CoroutineExceptionHandler { _, error ->
                logger.error(error.toString(), error)
                when (error) {
                    is FirebaseNetworkException -> {
                        _dialogState.postValue(DialogState.ShowMessage(R.string.no_internet_connection))
                    }
                    else -> {
                        _dialogState.postValue(DialogState.ShowMessage(R.string.not_controled_error))
                    }
                }
            }
            _dialogState.value= DialogState.ShowLoadingData
            viewModelScope.launch(errorHandler) {
                val result= async(Dispatchers.IO) {
                    getAllUsersButBosesAndNoTeamUseCase()
                }.await()
                _users.value= result
                _dialogState.value= DialogState.ShowDialog
            }
        }

        fun onInviteMemberClick() {
            if (holidayDays.value!!.isBlank()) {
                holidayDaysError.value = R.string.value_mandatory
            } else if (member.value!!.isBlank()) {
                membersError.value= R.string.value_mandatory
            } else {
                val userToInviteList= _users.value?.filter { user -> user.email== member.value }
                if (userToInviteList== null || userToInviteList.size== 0) {
                    membersError.value= R.string.user_not_exist
                } else {
                    val userToInvite = userToInviteList[0]
                    _dialogState.value = DialogState.ShowLoading
                    val errorHandler = CoroutineExceptionHandler { _, error ->
                        logger.error(error.toString(), error)
                        when (error) {
                            is FirebaseNetworkException -> {
                                _dialogState.postValue(DialogState.ShowMessage(R.string.no_internet_connection))
                            }
                            else -> {
                                _dialogState.postValue(DialogState.ShowMessage(R.string.no_internet_connection))
                            }
                        }
                    }
                    viewModelScope.launch(errorHandler) {

                        logger.debug("Estamos invitando a ${member.value}, espere....")
                        async(Dispatchers.IO) {
                            val localHolidayDays= holidayDays.value!!.toInt()
                            val localInternal= _internal.value!!
                            inviteUserUseCase(user, userToInvite, localInternal, localHolidayDays)
                        }.await()
                        _dialogState.value = DialogState.ShowSuccess

                    }
                }

            }
        }
    }
}