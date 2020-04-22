package es.samiralkalii.myapps.usecase.teammanagement

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository

class ConfirmDenyMemberUseCase(val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    suspend operator fun invoke(
        user: String,
        isConfirmed: Boolean,
        profile: String= "",
        profileId: String= "",
        holidayDays: Int= 22,
        internal: Boolean= false)= remoteTeamManagementRepository.confirmDenyMember(user,
        isConfirmed, profile, profileId, holidayDays, internal)
}