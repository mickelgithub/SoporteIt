package es.samiralkalii.myapps.soporteit.ui.home.profile

sealed class ProfileChangeState {

    class ShowMessage(val message: Int): ProfileChangeState()
    object changeOk: ProfileChangeState()
}