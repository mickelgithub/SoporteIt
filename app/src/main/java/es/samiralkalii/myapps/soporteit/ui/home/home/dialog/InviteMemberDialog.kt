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
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.InviteMemberDialogBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import es.samiralkalii.myapps.soporteit.ui.util.Event
import es.samiralkalii.myapps.soporteit.ui.util.ScreenState
import es.samiralkalii.myapps.soporteit.ui.util.bindImgSrc
import es.samiralkalii.myapps.usecase.teammanagement.GetAllUsersButBosesAndNoTeamUseCase
import kotlinx.android.synthetic.main.invite_member_dialog.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.bind
import org.koin.android.viewmodel.ext.android.viewModel
import org.slf4j.LoggerFactory

class InviteMemberDialog(): MyDialog() {

    private val logger = LoggerFactory.getLogger(InviteMemberDialog::class.java)

    private lateinit var onInviteMember: OnInviteMemberListener

    private lateinit var adapter: MembersSuggestAdapter

    private lateinit var binding: InviteMemberDialogBinding

    val viewModel: InviteMemberDialogViewModel by viewModel()


    companion object {

        fun newInstance()= InviteMemberDialog()

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
        logger.debug("onCreate...")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        logger.debug("onCreateView......")
        binding= InviteMemberDialogBinding.inflate(inflater, container, false)
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug("onViewCreated...")
        adapter= MembersSuggestAdapter(view.context, android.R.layout.simple_dropdown_item_1line)
        binding.members.setAdapter(adapter)
        binding.members.threshold = 4
        if (savedInstanceState!= null) {
            binding.members.post(Runnable{
                binding.members.dismissDropDown()
                })
        }

        binding.members.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank() && s.toString().length>= 4) {
                    adapter.setData(adapter._data.filter { user ->
                        user.email.startsWith(s)
                    })
                    adapter.notifyDataSetChanged()
                }
            }
        })

        viewModel.dialogState.observe(this, Observer{
            when (it) {
                DialogState.ShowLoadingData -> {
                    viewModel.member= activity!!.resources.getString(R.string.wait_while_loading_data)
                    binding.members.isEnabled= false
                    binding.inviteMember.visibility= View.GONE
                    binding.animationOk.visibility= View.GONE
                    binding.message.visibility= View.GONE
                    binding.animationLoading.visibility= View.VISIBLE
                }
                DialogState.ShowLoading -> {
                    binding.members.isEnabled= false
                    binding.inviteMember.visibility= View.GONE
                    binding.animationOk.visibility= View.GONE
                    binding.message.visibility= View.GONE
                    binding.animationLoading.visibility= View.VISIBLE
                }
                DialogState.ShowDialog -> {
                    if (activity!!.resources.getString(R.string.wait_while_loading_data)== viewModel.member) {
                        viewModel.member= ""
                        binding.invalidateAll()
                    }
                    binding.members.isEnabled= true
                    binding.inviteMember.visibility= View.VISIBLE
                    binding.animationOk.visibility= View.GONE
                    binding.message.visibility= View.GONE
                    binding.animationLoading.visibility= View.GONE
                }
                DialogState.ShowSuccess -> {
                    viewModel.member= ""
                    binding.members.isEnabled= false
                    binding.inviteMember.visibility= View.GONE
                    binding.animationOk.visibility= View.VISIBLE
                    binding.message.visibility= View.GONE
                    binding.animationLoading.visibility= View.GONE
                    Handler().postDelayed({
                        binding.animationOk.visibility= View.GONE
                        binding.members.isEnabled= true
                        binding.inviteMember.visibility= View.VISIBLE

                    }, DIALOG_DISMISS_DELAY)
                }
                is DialogState.ShowMessage -> {
                    binding.members.isEnabled= true
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
            var view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.invite_member_item, parent, false)
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

    class InviteMemberDialogViewModel(private val getAllUsersButBosesAndNoTeamUseCase: GetAllUsersButBosesAndNoTeamUseCase): ViewModel() {

        private val logger = LoggerFactory.getLogger(InviteMemberDialogViewModel::class.java)

        private val _dialogState= MutableLiveData<DialogState>()
        val dialogState: LiveData<DialogState>
        get() = _dialogState

        private val _users= MutableLiveData<List<User>>()
        val users: LiveData<List<User>>
        get() = _users

        var member: String = ""

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
                        /*_teamAddedOk.postValue(
                            Event(
                                ScreenState.Render(
                                    TeamManagementChangeState.ShowMessage(
                                        R.string.no_internet_connection)))
                        )*/
                    }
                    else -> {
                        _dialogState.postValue(DialogState.ShowMessage(R.string.no_internet_connection))
                        /*_teamAddedOk.postValue(
                            Event(
                                ScreenState.Render(
                                    TeamManagementChangeState.ShowMessage(
                                        R.string.no_internet_connection)))
                        )*/
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
            _dialogState.value= DialogState.ShowLoading
            viewModelScope.launch {
                logger.debug("Estamos invitando a $member, espere....")
                async {
                    delay(10000)
                }.await()
                _dialogState.value= DialogState.ShowDialog

            }

        }
    }
}