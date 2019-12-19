package es.samiralkalii.myapps.soporteit.ui.home.home.dialog

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.InviteMemberDialogBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import org.slf4j.LoggerFactory

class InviteMemberDialog: MyDialog() {

    private val logger = LoggerFactory.getLogger(InviteMemberDialog::class.java)

    private lateinit var onInviteMember: OnInviteMemberListener

    private lateinit var adapter: MembersSuggestAdapter

    private lateinit var binding: InviteMemberDialogBinding

    var member: String = ""

    companion object {

        var inviteMemberDialog: InviteMemberDialog?= null

        fun showDialogLoadingData(fragmentManager: FragmentManager)= InviteMemberDialog().apply {
            if (inviteMemberDialog== null) {
                inviteMemberDialog = InviteMemberDialog().apply {
                    isCancelable = false
                    show(fragmentManager, FRAGMENT_TAG)
                }
            }
        }

        fun showDialog() {
            inviteMemberDialog?.let {
                it.binding.members.isEnabled= true
                it.binding.inviteMember.visibility= View.VISIBLE
                it.binding.animationOk.visibility= View.GONE
                it.binding.message.visibility= View.GONE
                it.binding.animationLoading.visibility= View.GONE
            }
        }

        fun showLoading() {
            val tempInviteMemberDialog= inviteMemberDialog!!
            tempInviteMemberDialog.binding.members.isEnabled= false
            tempInviteMemberDialog.binding.inviteMember.visibility= View.GONE
            tempInviteMemberDialog.binding.animationOk.visibility= View.GONE
            tempInviteMemberDialog.binding.message.visibility= View.GONE
            tempInviteMemberDialog.binding.animationLoading.visibility= View.VISIBLE
        }

        fun showSuccess() {
            val tempInviteMemberDialog= inviteMemberDialog!!
            tempInviteMemberDialog.binding.members.isEnabled= false
            tempInviteMemberDialog.binding.inviteMember.visibility= View.GONE
            tempInviteMemberDialog.binding.animationLoading.visibility= View.GONE
            tempInviteMemberDialog.binding.message.visibility= View.GONE
            tempInviteMemberDialog.binding.animationOk.apply {
                visibility= View.VISIBLE
                playAnimation()
            }
            Handler().postDelayed({
                tempInviteMemberDialog.dismiss()
                inviteMemberDialog= null
            }, DIALOG_DISMISS_DELAY)
        }

        fun showMessage(message: Int) {
            val tempInviteMemberDialog= inviteMemberDialog!!
            tempInviteMemberDialog.binding.members.isEnabled= false
            tempInviteMemberDialog.binding.inviteMember.visibility= View.GONE
            tempInviteMemberDialog.binding.animationLoading.visibility= View.GONE
            tempInviteMemberDialog.binding.animationOk.visibility= View.GONE
            tempInviteMemberDialog.binding.message.visibility= View.VISIBLE
            tempInviteMemberDialog.binding.message.setText(message)
            Handler().postDelayed({
                tempInviteMemberDialog.dismiss()
                inviteMemberDialog= null
            }, DIALOG_DISMISS_DELAY)
        }

        fun loadUsers(users: List<User>) {
            inviteMemberDialog?.let {
                it.adapter.setData(users)
            }
        }

        fun dismissMe() {
            inviteMemberDialog?.dismiss()
            inviteMemberDialog= null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        onInviteMember= (context as AppCompatActivity).supportFragmentManager.findFragmentByTag(
            HomeFragment::class.java.simpleName) as OnInviteMemberListener

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        logger.debug("onCreateView")
        binding= InviteMemberDialogBinding.inflate(inflater, container, false)
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        adapter= MembersSuggestAdapter(activity!!, android.R.layout.simple_dropdown_item_1line)
        binding.members.setAdapter(adapter)
        binding.members.threshold= 4

        binding.members.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank() && s.toString().length>= 4) {
                    adapter.setData(adapter._data.filter { user ->
                        user.email.contains(s)
                    })
                    adapter.notifyDataSetChanged()
                }
            }
        })
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
            view.findViewById<ImageView>(R.id.image).setImageDrawable(context.getDrawable(R.drawable.profile))
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

    fun onInviteMemberClick() {

    }

    fun dismissMe() {
        InviteMemberDialog.dismissMe()
    }

    interface OnInviteMemberListener {
        fun onMemeberSelected(user: String)
        fun LoadUsers(users: List<User>)
    }
}