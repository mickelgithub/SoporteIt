package es.samiralkalii.myapps.soporteit.ui.home.home.dialog


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.samiralkalii.myapps.soporteit.databinding.DialogCreateTeamBinding
import org.slf4j.LoggerFactory


class CreateTeamDialog: BottomSheetDialogFragment() {

    companion object {

        private const val FRAGMENT_TAG= "dialog"
        const val DIALOG_DISMISS_DELAY= 2000L
        private const val DIALOG_FOR_MESSAGE_KEY= "message_dialog"

        fun showMe(fragmentManager: FragmentManager) {
           CreateTeamDialog().apply {
               isCancelable= false
               show(fragmentManager, FRAGMENT_TAG)
           }
        }



        /*fun showMeForAwhile(fragmentManager: FragmentManager, message: Int, delay: Long= DIALOG_DISMISS_DELAY) {
            if (createTeamDialog== null) {
                val bundle= Bundle().apply {
                    putInt(DIALOG_FOR_MESSAGE_KEY, message)
                }
                createTeamDialog= CreateTeamDialog().apply {
                    isCancelable= false
                    arguments= bundle
                }.also {
                    it.show(fragmentManager, FRAGMENT_TAG)
                    Handler().postDelayed({
                        createTeamDialog?.dismiss()
                        createTeamDialog= null
                    }, delay)
                }
            }
        }*/

    }

    private val logger = LoggerFactory.getLogger(CreateTeamDialog::class.java)
    private lateinit var binding: DialogCreateTeamBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("OnCreate...")
    }

    fun dismissMe() {
        dismiss()
    }

    var teamName: String= ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        logger.debug("onCreateView")

        binding= DialogCreateTeamBinding.inflate(inflater, container, false)
        /*arguments?.let { args ->
            args.getInt(DIALOG_FOR_MESSAGE_KEY)?.let { message ->
                binding.animationLoading.visibility= View.GONE
                binding.animationOk.visibility= View.GONE
                binding.message.apply {
                    visibility= View.VISIBLE
                    text= resources.getString(message)
                }
            }
        }*/
        binding.fragment= this
        return binding.root
    }

    fun dismiss(message: Int?) {
        var delay= 0L

        /*if (message!= null && message!= R.string.nothing) {
            binding.message.visibility = View.VISIBLE
            binding.message.text = activity!!.resources.getString(message)
            binding.animationLoading.visibility = View.GONE
            binding.animationOk.visibility = View.GONE
            delay = DIALOG_DISMISS_DELAY
        } else if (message== null) {
            binding.message.visibility= View.GONE
            binding.animationLoading.visibility= View.GONE
            binding.animationOk.apply {
                visibility= View.VISIBLE
                playAnimation()
            }
            delay= DIALOG_DISMISS_DELAY
        }
        Handler().postDelayed({
            this.dismiss()
        }, delay)*/
    }

    fun onCreateTeamButtonClick() {
        logger.debug("button create team clicked....")


    }



    sealed class DialogState {

        object ShowLoading: DialogState()
        object ShowSuccess: DialogState()
        class ShowMesage(val message: Int): DialogState()

    }


}