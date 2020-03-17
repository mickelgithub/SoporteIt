package es.samiralkalii.myapps.domain.common

class AreasDepartments(val areasDepartments: Map<Area, List<Department>>) {

    fun getDepartments(area: String): List<Department> {
        var areaEntry= areasDepartments.keys.filter { it.name== area }.first()
        return areasDepartments[areaEntry]!!
    }

    fun isEmpty()= areasDepartments.isEmpty()

}

data class Area(val id: String, val name: String)

data class Department(val id: String, val name: String)
