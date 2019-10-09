package es.samiralkalii.myapps.soporteit.ui.splash

sealed class SplashState {

    class ShowMessage(message: String): SplashState()
    object LoggedIn: SplashState()

}