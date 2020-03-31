package es.samiralkalii.myapps.soporteit.ui.dialog

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class MyDialog(): BottomSheetDialogFragment() {

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
}