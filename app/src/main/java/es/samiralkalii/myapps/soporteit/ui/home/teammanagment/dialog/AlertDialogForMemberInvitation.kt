package es.samiralkalii.myapps.soporteit.ui.home.teammanagment.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.samiralkalii.myapps.domain.User
import es.samiralkalii.myapps.soporteit.R
import org.slf4j.LoggerFactory

private const val KEY_FRAGMENT_TAG= "fragment"

class AlertDialogForMemberInvitation: DialogFragment() {

    private val logger = LoggerFactory.getLogger(AlertDialogForMemberInvitation::class.java)

    private var fragmentParent: String= ""

    private lateinit var onMemberSelection: OnMemberSelectionListener

    private lateinit var members: LiveData<List<String>>


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
        members= onMemberSelection.getMembers()

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        logger.debug("onCreateDialog.......................................................")

        val builder= MaterialAlertDialogBuilder(context, R.style.MyDialogCustomTheme)
        builder.setTitle("Este es un tituloooooooooooooo")

        builder.setMessage("Este es un mensajeeeeeeeeeeeeeee")
        builder.setCancelable(false)

        builder.setPositiveButton("OK" ) { dialog, _ ->
            logger.debug("pulsadoooooooooooooooo el button")
        }



        return builder.create()
    }

    interface OnMemberSelectionListener {
        fun onMemeberSelected(user: String)
        fun updateUsers(users: List<User>)
    }

}

