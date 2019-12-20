package es.samiralkalii.myapps.soporteit.ui.home.teammanagment.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import org.slf4j.LoggerFactory


private const val KEY_FRAGMENT_TAG= "fragment"

class AlertDialogForMemberInvitation: DialogFragment() {

    private val logger = LoggerFactory.getLogger(AlertDialogForMemberInvitation::class.java)

    private var fragmentParent: String= ""

    private lateinit var onMemberSelection: OnMemberSelectionListener

    private lateinit var adapter: MembersSuggestAdapter


    companion object {

        fun newInstance(fragmentParent: String)= AlertDialogForMemberInvitation().apply {
            arguments= Bundle().apply {
                putString(KEY_FRAGMENT_TAG, fragmentParent)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger.debug("onAttach........................")
        val args= arguments
        args?.let {
            fragmentParent = args[KEY_FRAGMENT_TAG] as String
        }
        try {
            onMemberSelection= (context as AppCompatActivity).supportFragmentManager.findFragmentByTag(fragmentParent) as OnMemberSelectionListener
        } catch (e: ClassCastException) {
            throw IllegalStateException("${context.toString()} must implement AlertDialogForMemberInvitation.OnMemberSelectedListener" )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("Oncreate..........................................................")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        logger.debug("onCreateDialog.......................................................")

        val builder= MaterialAlertDialogBuilder(context, R.style.MyDialogCustomTheme)
        builder.setTitle(activity!!.resources.getString(R.string.invite)+ "...")
        builder.setCancelable(false)
        val view= LayoutInflater.from(activity!!).inflate(R.layout.invite_member_dialog, null)
        val membersAutoComplete= view.findViewById<AppCompatAutoCompleteTextView>(R.id.members)
        adapter= MembersSuggestAdapter(activity!!, android.R.layout.simple_dropdown_item_1line)
        membersAutoComplete.setAdapter(adapter)
        membersAutoComplete.threshold= 4

        membersAutoComplete.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank() && s.toString().length>= 4) {
                    adapter.setData(adapter._data.filter {
                        it.email.contains(s)
                    })
                    adapter.notifyDataSetChanged()
                }
            }
        })

        builder.setView(view)


        builder.setPositiveButton("OK" ) { dialog, _ ->
            logger.debug("pulsadoooooooooooooooo el button")
        }

        return builder.create()
    }

    fun updateUsers(users: List<User>) {
        adapter.initData(users)
    }

    private inner class MembersSuggestAdapter(context: Context, val resource: Int): ArrayAdapter<User>(context, resource), Filterable {

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
            var view: View= convertView ?: LayoutInflater.from(context).inflate(R.layout.invite_member_item, parent, false)
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

    interface OnMemberSelectionListener {
        fun onMemeberSelected(user: String)
        fun updateUsers(users: List<User>)
    }

}

