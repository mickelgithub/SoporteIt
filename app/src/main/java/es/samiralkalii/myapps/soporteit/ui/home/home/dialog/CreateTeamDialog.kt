package es.samiralkalii.myapps.soporteit.ui.home.home.dialog


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.DialogLoadingBinding


class CreateTeamDialog: BottomSheetDialogFragment() {

    companion object {

        private const val FRAGMENT_TAG= "dialog"
        const val DIALOG_DISMISS_DELAY= 2000L
        private const val DIALOG_FOR_MESSAGE_KEY= "message_dialog"

        var createTeamDialog: CreateTeamDialog?= null

        fun showMe(fragmentManager: FragmentManager) {
            if (createTeamDialog== null) {
                createTeamDialog= CreateTeamDialog().apply {
                    isCancelable= false
                }.also {
                    it.show(fragmentManager, FRAGMENT_TAG)
                }
            }
        }

        fun dismissMe(message: Int?) {
            createTeamDialog?.dismiss(message)
            createTeamDialog= null
        }

        fun showMeForAwhile(fragmentManager: FragmentManager, message: Int, delay: Long= DIALOG_DISMISS_DELAY) {
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
        }

    }

    private lateinit var binding: DialogLoadingBinding




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DialogLoadingBinding.inflate(inflater, container, false)
        arguments?.let { args ->
            args.getInt(DIALOG_FOR_MESSAGE_KEY)?.let { message ->
                binding.animationLoading.visibility= View.GONE
                binding.animationOk.visibility= View.GONE
                binding.message.apply {
                    visibility= View.VISIBLE
                    text= resources.getString(message)
                }
            }
        }

        return binding.root
    }

    fun dismiss(message: Int?) {
        var delay= 0L

        if (message!= null && message!= R.string.nothing) {
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
        }, delay)
    }



    sealed class DialogState {

        object ShowLoading: DialogState()
        object ShowSuccess: DialogState()
        class ShowMesage(val message: Int): DialogState()

    }


}