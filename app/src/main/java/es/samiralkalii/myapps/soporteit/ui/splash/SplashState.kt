package es.samiralkalii.myapps.soporteit.ui.splash

import es.samiralkalii.myapps.domain.User

sealed class SplashState {

    class ShowMessage(val message: Int): SplashState()
    class LoggedIn(val user: User): SplashState()
    class Relogged(val user: User): SplashState()
    object FirstAccess: SplashState()

}