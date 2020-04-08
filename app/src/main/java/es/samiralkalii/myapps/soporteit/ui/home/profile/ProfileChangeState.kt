package es.samiralkalii.myapps.soporteit.ui.home.profile

sealed class ProfileChangeState {

    class ShowMessage(val message: Int, val messageParams: List<Any> = listOf()): ProfileChangeState()
    object changeOk: ProfileChangeState()
}