package es.samiralkalii.myapps.soporteit.ui.dialog

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.DialogLoadingBinding


class LoadingDialog: MyDialog() {

    companion object {

        var loadingDialog: LoadingDialog?= null

        fun showLoading(fragmentManager: FragmentManager) {
            if (loadingDialog== null) {
                loadingDialog= LoadingDialog().apply {
                    isCancelable= false
                }.also {
                    it.show(fragmentManager, FRAGMENT_TAG)
                }
            }
        }

        fun dismissMe(message: Int?) {
            loadingDialog?.dismiss(message)
            loadingDialog= null
        }

        fun dismissMeInmediatly() {
            loadingDialog?.dismissInmediatly()
            loadingDialog= null
        }

        fun showMessageDialogForAwhile(fragmentManager: FragmentManager, message: Int, delay: Long= DIALOG_DISMISS_DELAY, messageColor: Int= R.color.red_error) {
            if (loadingDialog== null) {
                val bundle= Bundle().apply {
                    putInt(DIALOG_FOR_MESSAGE_KEY, message)
                    putInt(DIALOG_MESSAGE_COLOR, messageColor)
                }
                loadingDialog= LoadingDialog().apply {
                    isCancelable= false
                    arguments= bundle
                }.also {
                    it.show(fragmentManager, FRAGMENT_TAG)
                    Handler().postDelayed({
                        loadingDialog?.dismiss()
                        loadingDialog= null
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
            val color= args.getInt(DIALOG_MESSAGE_COLOR)
            args.getInt(DIALOG_FOR_MESSAGE_KEY).let { message ->
                binding.animationLoading.visibility= View.GONE
                binding.animationOk.visibility= View.GONE
                binding.message.apply {
                    visibility= View.VISIBLE
                    text= resources.getString(message)
                    setTextColor(ContextCompat.getColor(context, color))
                }
            }
        }

        return binding.root
    }

    private fun dismiss(message: Int?) {
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

    private fun dismissInmediatly() {
        this.dismiss()
    }

}