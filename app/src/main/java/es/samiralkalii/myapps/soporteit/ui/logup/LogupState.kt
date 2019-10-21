package es.samiralkalii.myapps.soporteit.ui.logup

sealed class LogupState {

    class ShowMessage(val message: Int): LogupState()
    object RegisteredOk: LogupState()
}