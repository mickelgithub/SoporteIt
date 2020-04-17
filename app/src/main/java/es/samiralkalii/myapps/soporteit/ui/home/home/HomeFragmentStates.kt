package es.samiralkalii.myapps.soporteit.ui.home.home

import es.samiralkalii.myapps.soporteit.ui.home.home.adapter.MemberUserViewModelTemplate

class HomeFragmentStates {

    sealed class GetGroupsState {

        class ShowMessage(val message: Int, val messageParams: List<Any> = listOf()): GetGroupsState()
        class UpdateMessage(val message: Int, val messageParams: List<Any> = listOf()): GetGroupsState()
        class GetGroupsStateOk(val members: List<MemberUserViewModelTemplate>): GetGroupsState()
    }

}

