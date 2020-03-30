package es.samiralkalii.myapps.domain.common

class BossCategories(val bossCategories: List<BossCategory>) {

    fun getBossCategoriesName()= bossCategories.map { it.name }

    fun getBossCategory(name: String)= bossCategories.filter { it.name.equals(name, true) }.first()

    fun isEmpty()= (bossCategories== null || bossCategories.isEmpty())

}

data class BossCategory(val id: String, val name: String, val level: Int)