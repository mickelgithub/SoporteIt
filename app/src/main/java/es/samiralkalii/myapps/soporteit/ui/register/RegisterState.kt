package es.samiralkalii.myapps.soporteit.ui.register

sealed class RegisterState {

    class ShowMessage(val message: Int): RegisterState()
    object RegisteredOk: RegisterState()
}