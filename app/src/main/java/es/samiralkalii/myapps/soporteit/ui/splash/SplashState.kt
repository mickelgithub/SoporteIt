package es.samiralkalii.myapps.soporteit.ui.splash

sealed class SplashState {

    class ShowMessage(val message: Int): SplashState()
    object LoggedIn: SplashState()
    object Relogged: SplashState()
    object FirstAccess: SplashState()

}