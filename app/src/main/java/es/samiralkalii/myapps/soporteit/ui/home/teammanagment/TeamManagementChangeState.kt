package es.samiralkalii.myapps.soporteit.ui.home.teammanagment

sealed class TeamManagementChangeState {

    class ShowMessage(val message: Int): TeamManagementChangeState()
    object teamAddedOk: TeamManagementChangeState()
}