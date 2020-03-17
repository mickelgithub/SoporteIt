package es.samiralkalii.myapps.soporteit.framework.remotestorage.database

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import es.samiralkalii.myapps.data.common.IRemoteCommonDataDataSource
import es.samiralkalii.myapps.domain.common.Area
import es.samiralkalii.myapps.domain.common.AreasDepartments
import es.samiralkalii.myapps.domain.common.Department
import kotlinx.coroutines.tasks.await

private const val AREAS_REF= "areas"
private const val KEY_NAME= "name"
private const val DEPARTMENTS_REF= "departments"

class RemoteCommonDataDatasourceManager(private val fstore: FirebaseFirestore): IRemoteCommonDataDataSource {

    override suspend fun getAreasDepartments(): AreasDepartments {
        val areasDepartments= mutableMapOf<Area, List<Department>>()
        val areasResult= fstore.collection(AREAS_REF).get(Source.SERVER).await()
        if (!areasResult.isEmpty) {
            for (areaDocument in areasResult) {
                val area= areaDocument.data.get(KEY_NAME) as String
                val departments= mutableListOf<Department>()
                val departmentsResult= fstore.collection(AREAS_REF).document(areaDocument.id).collection(DEPARTMENTS_REF).get(Source.SERVER).await()
                if (!departmentsResult.isEmpty) {
                    for (departmentDocument in departmentsResult) {
                        val department= departmentDocument.data.get(KEY_NAME) as String
                        departments.add(Department(departmentDocument.id, department))
                    }
                    areasDepartments[Area(areaDocument.id, area)]= departments
                }
            }
        }
        return AreasDepartments(areasDepartments)
    }
}