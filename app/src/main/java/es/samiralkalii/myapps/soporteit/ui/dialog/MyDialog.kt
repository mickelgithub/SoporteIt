package es.samiralkalii.myapps.soporteit.ui.dialog

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class MyDialog(): BottomSheetDialogFragment() {

    companion object {
        const val DIALOG_DISMISS_DELAY= 2000L
        const val FRAGMENT_TAG= "dialog"
        const val DIALOG_FOR_MESSAGE_KEY= "message_dialog"
        const val DIALOG_MESSAGE_COLOR= "message_color"
    }

    sealed class DialogState {
        class ShowDialog(val message: Int= -1, val modal: Boolean= true, val error: Boolean= false): DialogState()
        object ShowProgress: DialogState()
        class UpdateSuccess(val delay: Long= DIALOG_DISMISS_DELAY): DialogState()
        class UpdateMessage(val message: Int, val modal: Boolean, val error: Boolean, val delay: Long)
        class HideDialog(val delay: Long): DialogState()

    }
}