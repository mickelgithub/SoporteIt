package es.samiralkalii.myapps.soporteit.ui.register

sealed class LoginState {

    class ShowMessage(val message: Int): LoginState()
    object LoginOk: LoginState()
}