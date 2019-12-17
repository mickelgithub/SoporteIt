package es.samiralkalii.myapps.soporteit.ui.home.home

sealed class HomeFragmentChangeState {

    class ShowMessage(val message: Int): HomeFragmentChangeState()
    object teamAddedOk: HomeFragmentChangeState()
    
}