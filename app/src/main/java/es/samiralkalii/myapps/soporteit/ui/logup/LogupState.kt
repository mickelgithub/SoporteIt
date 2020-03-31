package es.samiralkalii.myapps.soporteit.ui.logup

import es.samiralkalii.myapps.domain.User

sealed class LogupState {

    class ShowMessage(val message: Int, val messageParams: List<Any> = listOf()): LogupState()
    class UpdateMessage(val message: Int, val messageParams: List<Any> = listOf()): LogupState()
    class LoggedupOk(val user: User): LogupState()
    class LoggedupAsManagerTeamOk(val user: User): LogupState()
}