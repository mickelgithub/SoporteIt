package es.samiralkalii.myapps.soporteit.ui.logup

import es.samiralkalii.myapps.domain.User

sealed class LoginState {

    //class ShowMessage(val message: Int, val messageParams: List<Any> = listOf()): LoginState()
    class UpdateMessage(val message: Int, val messageParams: List<Any> = listOf()): LoginState()
    class LoginOk(val user: User): LoginState()
}