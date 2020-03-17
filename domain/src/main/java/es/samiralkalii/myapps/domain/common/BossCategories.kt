package es.samiralkalii.myapps.domain.common

class BossCategories(val bossCategories: List<BossCategory>) {

    fun getBossCategoriesName()= bossCategories.map { it.name }

}

data class BossCategory(val id: String, val name: String)