package es.samiralkalii.myapps.soporteit.ui.dialog

import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class MyDialog: BottomSheetDialogFragment() {

    companion object {
        const val DIALOG_DISMISS_DELAY= 2000L
        const val FRAGMENT_TAG= "dialog"
        const val DIALOG_FOR_MESSAGE_KEY= "message_dialog"
        const val DIALOG_MESSAGE_COLOR= "message_color"
    }

    sealed class DialogState {
        object ShowLoadingData: DialogState()
        object ShowDialog: DialogState()
        object ShowLoading: DialogState()
        object ShowSuccess: DialogState()
        object DismissInmediatly: DialogState()
        class ShowMessage(val message: Int): DialogState()

    }
}