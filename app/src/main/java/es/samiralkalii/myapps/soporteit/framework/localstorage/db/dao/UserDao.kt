package es.samiralkalii.myapps.soporteit.framework.localstorage.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import es.samiralkalii.myapps.soporteit.framework.localstorage.db.model.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Query("SELECT * FROM user_profile_info LIMIT 1")
    fun getUser(): User
}