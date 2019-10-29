package es.samiralkalii.myapps.soporteit.framework.localstorage.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile_info")
data class User(
    @PrimaryKey
    val email: String,
    val password: String,
    val name: String,
    val id: String,
    val localProfileImage: String?,
    val remoteProfileImage: String?,
    val creationDate: Long?,
    val emailVerified: Boolean?
) {


}