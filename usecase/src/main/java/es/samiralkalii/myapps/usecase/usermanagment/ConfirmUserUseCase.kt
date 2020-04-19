package es.samiralkalii.myapps.usecase.usermanagment

import es.samiralkalii.myapps.data.teammanagement.RemoteTeamManagementRepository
import es.samiralkalii.myapps.preference.PreferenceRepository

class ConfirmUserUseCase(private val preferenceRepository: PreferenceRepository, val remoteTeamManagementRepository: RemoteTeamManagementRepository) {

    suspend operator fun invoke(user: String) {
        //02remote
    }
}