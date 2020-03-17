package es.samiralkalii.myapps.usecase.common

import es.samiralkalii.myapps.data.common.RemoteCommonDataRepository

class GetBossCategoriesUseCase(private val remoteCommonDataRepository: RemoteCommonDataRepository) {

    suspend operator fun invoke()= remoteCommonDataRepository.getBossCategorties()
}