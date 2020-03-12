package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.firestore.FirebaseFirestore
import es.samiralkalii.myapps.data.common.IRemoteCommonDataDataSource
import es.samiralkalii.myapps.domain.common.AreasDepartments
import kotlinx.coroutines.tasks.await
import org.slf4j.LoggerFactory

private const val AREAS_REF= "areas"
private const val KEY_NAME= "name"
private const val DEPARTMENTS_REF= "departments"

class RemoteCommonDataDatasourceManager(private val fstore: FirebaseFirestore): IRemoteCommonDataDataSource {

    private val logger= LoggerFactory.getLogger(RemoteCommonDataDatasourceManager::class.java)

    override suspend fun getAreasDepartments(): AreasDepartments {
        val areasDepartments= mutableMapOf<String, List<String>>()
        val areasResult= fstore.collection(AREAS_REF).get().await()
        if (!areasResult.isEmpty) {
            for (areaDocument in areasResult) {
                val area= areaDocument.data.get(KEY_NAME) as String
                val departments= mutableListOf<String>()
                val departmentsResult= fstore.collection(AREAS_REF).document(areaDocument.id).collection(DEPARTMENTS_REF).get().await()
                logger.debug("por aqui")
                if (!departmentsResult.isEmpty) {
                    for (departmentDocumento in departmentsResult) {
                        val department= departmentDocumento.data.get(KEY_NAME) as String
                        departments.add(department)
                    }
                    areasDepartments[area]= departments
                }
            }
        }
        return AreasDepartments(areasDepartments)
    }
}