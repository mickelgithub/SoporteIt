package es.samiralkalii.myapps.soporteit.ui.dialog

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentManager
import es.samiralkalii.myapps.soporteit.R
import es.samiralkalii.myapps.soporteit.databinding.DialogLoadingBinding
import org.koin.android.ext.android.bind


class LoadingDialog: MyDialog() {

    companion object {

        //var loadingDialog: LoadingDialog?= null

        fun processDialog(dialogState: DialogState, fragmentManager: FragmentManager) {
            when (dialogState) {
                is DialogState.ShowProgressDialog -> {
                    showProgressDialog(fragmentManager, dialogState.message)
                }
                is DialogState.ShowMessageDialog -> {
                    showMessageDialog(fragmentManager, dialogState.message, dialogState.error)
                }
                is DialogState.UpdateSuccess -> {
                    updateSuccessAndHide(fragmentManager, dialogState.delay)
                }
                is DialogState.UpdateMessage -> {
                    updateMessageAndHide(fragmentManager, dialogState.message, dialogState.delay, dialogState.error)
                }
                is DialogState.HideDialog -> {
                    hideDialog(fragmentManager, dialogState.delay)
                }
            }
        }
        private fun showProgressDialog(fragmentManager: FragmentManager, message: Int) {
            var loadingDialog: LoadingDialog?= fragmentManager.findFragmentByTag(FRAGMENT_TAG) as LoadingDialog?
            if (loadingDialog== null) {
                LoadingDialog().apply {
                    isCancelable= false
                    if (message> -1) {
                        Bundle().apply {
                            putInt(DIALOG_FOR_MESSAGE_KEY, message)
                        }.also {
                            arguments= it
                        }
                    }
                }.also {
                    it.show(fragmentManager, FRAGMENT_TAG)
                }
            }
        }
        private fun showMessageDialog(fragmentManager: FragmentManager, message: Int, error: Boolean) {
            var loadingDialog: LoadingDialog?= fragmentManager.findFragmentByTag(FRAGMENT_TAG) as LoadingDialog?
            if (loadingDialog== null) {
                LoadingDialog().apply {
                    isCancelable= true
                    Bundle().apply {
                        putInt(DIALOG_FOR_MESSAGE_KEY, message)
                        if (!error) {
                            putBoolean(DIALOG_ERROR_INDICATION_KEY, error)
                        }
                    }.also {
                        arguments= it
                    }
                }.also {
                    it.show(fragmentManager, FRAGMENT_TAG)
                }
            }
        }
        private fun updateSuccessAndHide(fragmentManager: FragmentManager, delay: Long) {
            var loadingDialog: LoadingDialog?= fragmentManager.findFragmentByTag(FRAGMENT_TAG) as LoadingDialog?
            loadingDialog?.updateSuccessAndHide(delay)
        }
        private fun updateMessageAndHide(fragmentManager: FragmentManager, message: Int, delay: Long, error: Boolean) {
            var loadingDialog: LoadingDialog?= fragmentManager.findFragmentByTag(FRAGMENT_TAG) as LoadingDialog?
            loadingDialog?.updateMessageAndHide(message, delay, error)
        }
        private fun hideDialog(fragmentManager: FragmentManager, delay: Long) {
            var loadingDialog: LoadingDialog?= fragmentManager.findFragmentByTag(FRAGMENT_TAG) as LoadingDialog?
            loadingDialog?.dismissDialog(delay)
        }
    }

    private lateinit var binding: DialogLoadingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= DialogLoadingBinding.inflate(inflater, container, false)
        val message= arguments?.getInt(DIALOG_FOR_MESSAGE_KEY, -1) ?: -1
        val isError= arguments?.getBoolean(DIALOG_ERROR_INDICATION_KEY, true) ?: true
        binding.message.visibility= if (message> -1) {
            if (!isError) {
                binding.message.setTextColor(ContextCompat.getColor(activity!!, R.color.colorPrimary))
            }
            binding.message.setText(message)
            binding.animationLoading.visibility= View.GONE
            binding.animationOk.visibility= View.GONE
            View.VISIBLE
        } else {
            binding.animationLoading.visibility= View.VISIBLE
            binding.animationOk.visibility= View.GONE
            View.GONE
        }
        return binding.root
    }
    private fun updateSuccessAndHide(delay: Long) {
        binding.animationLoading.visibility= View.GONE
        binding.animationOk.visibility= View.VISIBLE
        binding.message.visibility= View.GONE
        dismissDialog(delay)
    }
    private fun updateMessageAndHide(message: Int, delay: Long, error: Boolean) {
        binding.animationLoading.visibility= View.GONE
        binding.animationOk.visibility= View.GONE
        binding.message.visibility= View.VISIBLE
        binding.message.setText(message)
        if (!error) {
            binding.message.setTextColor(ContextCompat.getColor(activity!!, R.color.colorPrimary))
        }
        dismissDialog(delay)
    }
    private fun dismissDialog(delay: Long) {
        if (delay> 0) {
            binding.root.postDelayed({
                dismiss()
            }, delay)
        } else {
            dismiss()
        }
    }
}