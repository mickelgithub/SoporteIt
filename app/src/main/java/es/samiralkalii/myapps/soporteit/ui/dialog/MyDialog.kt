package es.samiralkalii.myapps.soporteit.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.slf4j.LoggerFactory

open class MyDialog(): BottomSheetDialogFragment() {

    private val logger = LoggerFactory.getLogger(MyDialog::class.java)

    companion object {
        const val DIALOG_DISMISS_DELAY= 2000L
        const val FRAGMENT_TAG= "dialog"
        const val DIALOG_FOR_MESSAGE_KEY= "message_dialog"
        const val DIALOG_ERROR_INDICATION_KEY= "message_error"
    }

    sealed class DialogState {
        class ShowProgressDialog(val message: String= ""): DialogState()
        class UpdateSuccess(val delay: Long= DIALOG_DISMISS_DELAY): DialogState()
        class UpdateMessage(val message: String, val error: Boolean= true): DialogState()
        class UpdateMessageAndHide(val message: String, val error: Boolean= true, val delay: Long= DIALOG_DISMISS_DELAY): DialogState()
        class HideDialog(val delay: Long= DIALOG_DISMISS_DELAY): DialogState()
        class ShowMessageDialog(val message: String, val error: Boolean= true): DialogState()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onAttach******")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onCreate******")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onViewCreated******")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onActivityCreated******")
    }

    override fun onStart() {
        super.onStart()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onStart******")
    }

    override fun onResume() {
        super.onResume()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onResume******")
    }

    override fun onPause() {
        super.onPause()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onPause******")
    }

    override fun onStop() {
        super.onStop()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onStop******")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onDestroyView******")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.debug("${this.javaClass.simpleName} ${this.hashCode()} onDestroy******")
    }
}