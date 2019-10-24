package es.samiralkalii.myapps.soporteit.ui.logup

import es.samiralkalii.myapps.domain.User

sealed class LoginState {

    class ShowMessage(val message: Int): LoginState()
    class LoginOk(val user: User): LoginState()
}