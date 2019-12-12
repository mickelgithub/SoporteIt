package es.samiralkalii.myapps.soporteit.ui.dialog

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.samiralkalii.myapps.soporteit.databinding.DialogLoadingBinding


class LoadingDialog: BottomSheetDialogFragment() {

    companion object {

        const val FRAGMENT_TAG= "dialog"

        var loadingDialog: LoadingDialog?= null

        fun showMe(fragmentManager: FragmentManager) {
            if (loadingDialog!= null) {
                throw Exception("Loading is already showing.....")
            }
            loadingDialog= LoadingDialog().apply {
                isCancelable= false
            }.also {
                it.show(fragmentManager, FRAGMENT_TAG)
            }
        }

        fun dismissMe(message: Int?) {
            loadingDialog?.dismiss(message)
        }

    }

    private lateinit var binding: DialogLoadingBinding




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DialogLoadingBinding.inflate(inflater, container, false)

        return binding.root
    }

    fun dismiss(message: Int?) {

        if (message!= null) {
            binding.message.visibility= View.VISIBLE
            binding.message.text= activity!!.resources.getString(message)
            binding.animationLoading.visibility= View.GONE
            binding.animationOk.visibility= View.GONE
        } else {
            binding.message.visibility= View.GONE
            binding.animationLoading.visibility= View.GONE
            binding.animationOk.visibility= View.VISIBLE
        }
        Handler().postDelayed({
            this.dismiss()
        }, 200)
    }



    sealed class DialogState {

        object ShowLoading: DialogState()
        object ShowSuccess: DialogState()
        class ShowMesage(val message: Int): DialogState()

    }


}