package es.samiralkalii.myapps.soporteit.ui.logup

sealed class LoginState {

    class ShowMessage(val message: Int): LoginState()
    object LoginOk: LoginState()
}