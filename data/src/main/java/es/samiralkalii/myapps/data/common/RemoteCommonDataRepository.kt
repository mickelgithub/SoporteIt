package es.samiralkalii.myapps.data.common

import es.samiralkalii.myapps.domain.common.AreasDepartments
import es.samiralkalii.myapps.domain.common.BossCategories

class RemoteCommonDataRepository(private val remoteCommonDataDataSource: IRemoteCommonDataDataSource) {

    suspend fun getAreasDepartments()= remoteCommonDataDataSource.getAreasDepartments()
    suspend fun getBossCategorties()= remoteCommonDataDataSource.getBossCategorties()

}

interface IRemoteCommonDataDataSource {

    suspend fun getAreasDepartments(): AreasDepartments
    suspend fun getBossCategorties(): BossCategories
}