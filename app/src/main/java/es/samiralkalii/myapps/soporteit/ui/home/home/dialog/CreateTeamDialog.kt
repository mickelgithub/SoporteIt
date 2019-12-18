package es.samiralkalii.myapps.soporteit.ui.home.home.dialog


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.samiralkalii.myapps.soporteit.databinding.DialogCreateTeamBinding
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog
import es.samiralkalii.myapps.soporteit.ui.dialog.MyDialog.Companion.FRAGMENT_TAG
import es.samiralkalii.myapps.soporteit.ui.home.home.HomeFragment
import org.slf4j.LoggerFactory


class CreateTeamDialog: MyDialog() {



    private val logger = LoggerFactory.getLogger(CreateTeamDialog::class.java)
    private lateinit var binding: DialogCreateTeamBinding

    private lateinit var onCreateTeamListener: OnCreateTeamListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("OnCreate...")
    }

    var teamName: String= ""

    override fun onAttach(context: Context) {
        super.onAttach(context)

        onCreateTeamListener= (context as AppCompatActivity).supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName) as OnCreateTeamListener

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        logger.debug("onCreateView")
        binding= DialogCreateTeamBinding.inflate(inflater, container, false)
        binding.lifecycleOwner= viewLifecycleOwner
        binding.fragment= this
        return binding.root
    }

    companion object {

        var createTeamDialog: CreateTeamDialog?= null

        fun showDialog(fragmentManager: FragmentManager) {

            if (createTeamDialog == null) {
                createTeamDialog = CreateTeamDialog().apply {
                    isCancelable = false
                }.also {
                    it.show(fragmentManager, FRAGMENT_TAG)
                }
            }
        }

        fun showLoading() {
            val tempCreateTeamDialog= createTeamDialog!!
            tempCreateTeamDialog.binding.teamInputLayout.editText?.isEnabled= false
            tempCreateTeamDialog.binding.createTeam.visibility= View.GONE
            tempCreateTeamDialog.binding.animationOk.visibility= View.GONE
            tempCreateTeamDialog.binding.message.visibility= View.GONE
            tempCreateTeamDialog.binding.animationLoading.visibility= View.VISIBLE
        }

        fun showSuccess() {
            val tempCreateTeamDialog= createTeamDialog!!
            tempCreateTeamDialog.binding.teamInputLayout.editText?.isEnabled= false
            tempCreateTeamDialog.binding.createTeam.visibility= View.GONE
            tempCreateTeamDialog.binding.animationLoading.visibility= View.GONE
            tempCreateTeamDialog.binding.message.visibility= View.GONE
            tempCreateTeamDialog.binding.animationOk.apply {
                visibility= View.VISIBLE
                playAnimation()
            }
            Handler().postDelayed({
                tempCreateTeamDialog.dismiss()
                createTeamDialog= null
            }, DIALOG_DISMISS_DELAY)
        }

        fun showMessage(message: Int) {
            val tempCreateTeamDialog= createTeamDialog!!
            tempCreateTeamDialog.binding.teamInputLayout.editText?.isEnabled= false
            tempCreateTeamDialog.binding.createTeam.visibility= View.GONE
            tempCreateTeamDialog.binding.animationLoading.visibility= View.GONE
            tempCreateTeamDialog.binding.animationOk.visibility= View.GONE
            tempCreateTeamDialog.binding.message.visibility= View.VISIBLE
            tempCreateTeamDialog.binding.message.setText(message)
            Handler().postDelayed({
                tempCreateTeamDialog.dismiss()
                createTeamDialog= null
            }, DIALOG_DISMISS_DELAY)
        }

    }

    fun onCreateTeamClickec() {
        onCreateTeamListener.onCreateTeam(teamName)
    }


    interface OnCreateTeamListener {
        fun onCreateTeam(team: String)
    }



}