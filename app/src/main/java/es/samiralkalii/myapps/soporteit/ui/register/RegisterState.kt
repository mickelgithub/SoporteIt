package es.samiralkalii.myapps.soporteit.ui.register

sealed class RegisterState {

    class ShowMessage(val message: String): RegisterState()
    object RegisteredOk: RegisterState()
}